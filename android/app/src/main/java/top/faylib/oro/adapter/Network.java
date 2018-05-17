/**
 *
 * Copyright (c) 2018 faylib.top
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package top.faylib.oro.adapter;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.json.JSONObject;

public class Network extends ReactContextBaseJavaModule {

    //region Member Variables

    // 调试模式
    private boolean debugMode = false;

    // 请求队列
    private RequestQueue queue = Volley.newRequestQueue(getReactApplicationContext());

    // 超时时隔
    private int timeoutInterval = 120000;

    // 重试次数
    private int retryTimes = 1;

    //endregion


    //region Life Cycle

    public Network(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    //endregion


    //region Private Methods

    // 打印调试信息
    private void debugLog(String ... strings) {
        if (debugMode) {
            for (String string : strings) {
                Log.i("OrO", "[ OrO ][ NETWORK ][ DEBUG ] " + string);
            }
        }
    }

    // 发送请求
    private void send(int method, String url, JSONObject params, int retryTimes, Callback callback) {

        if (debugMode) { // 调试信息
            Log.i("OrO", "[ OrO ][ NETWORK ] Request sending with arguments.");

            switch (method) {
                case 0:
                    Log.i("OrO", "[ OrO ][ METHOD ] GET");
                    break;
                case 1:
                    Log.i("OrO", "[ OrO ][ METHOD ] POST");
                    break;
                case 3:
                    Log.i("OrO", "[ OrO ][ METHOD ] DELETE");
                    break;
                default:
                    break;
            }

            Log.i("OrO", "[ OrO ][ URL ] " + url);
            Log.i("OrO", "[ OrO ][ PARAMS ] " + ((params==null)?"null":params.toString()));
            Log.i("OrO", "[ OrO ][ RETRY TIMES ] " + String.valueOf(retryTimes));
            Log.i("OrO", "[ OrO ][ TIMEOUT INTERVAL ] " + String.valueOf(timeoutInterval/1000));
        }

        retryTimes--;
        int count = retryTimes;

        JsonObjectRequest request = new JsonObjectRequest(method, url, params, response -> {
            callback.invoke(response.toString());
        }, error -> {
            if (count < 1) {
                Log.i("OrO", error.toString());
            } else {
                send(method, url, params, count, callback);
            }
        }) {// 重写解析服务器返回的数据

        };

        request.setRetryPolicy(new DefaultRetryPolicy(timeoutInterval,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    //endregion


    //region React-Native Methods

    @Override
    public String getName() {
        return "Network";
    }

    /**
     * 设置调试模式
     *
     * @param trueOrFalse 开关
     */
    @ReactMethod
    public void debugMode(boolean trueOrFalse) {
        debugMode = trueOrFalse;
    }

    /**
     * 设置超时时隔
     *
     * @param sec 时隔（秒）
     */
    @ReactMethod
    public void timeoutInterval(int sec) {
        timeoutInterval = sec;
    }

    /**
     * 设置重试次数
     *
     * @param count 次数
     */
    @ReactMethod
    public void retryTimes(int count) {
        retryTimes = count;
    }

    /**
     * 发送 GET 请求
     *
     * @param url 请求的地址
     * @param callback 与 JavaScript 通信的变量，用于响应消息后回调
     */
    @ReactMethod
    public void GET(String url, Callback callback) {
        send(Request.Method.GET, url, null, retryTimes, callback);
    }

    /**
     * 发送 POST 请求
     *
     * @param url 请求的地址
     * @param params 请求的参数
     * @param callback 与 JavaScript 通信的变量，用于响应消息后回调
     */
    @ReactMethod
    public void POST(String url, JSONObject params, Callback callback) {
        send(Request.Method.POST, url, params, retryTimes, callback);
    }

    /**
     * 发送 DELETE 请求
     *
     * @param url 请求的地址
     * @param params 请求的参数
     * @param callback 与 JavaScript 通信的变量，用于响应消息后回调
     */
    @ReactMethod
    public void DELETE(String url, JSONObject params, Callback callback) {
        send(Request.Method.DELETE, url, params, retryTimes, callback);
    }

    //endregion
}
