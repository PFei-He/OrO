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

    // 创建请求队列
    private RequestQueue queue = Volley.newRequestQueue(getReactApplicationContext());

    //region Life Cycle

    public Network(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    //endregion


    //region React-Native Methods

    @Override
    public String getName() {
        return "Network";
    }

    /**
     * 发送 GET 请求
     *
     * @param url 请求的地址
     * @param callback 与 JavaScript 通信的变量，用于响应消息后回调
     */
    @ReactMethod
    public void GET(String url, Callback callback) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            callback.invoke(response.toString());
        }, error -> {
            Log.v("OrO", error.toString());
        });
        queue.add(request);
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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params, response -> {
            callback.invoke(response.toString());
        }, error -> {
            Log.v("OrO", error.toString());
        });
        queue.add(request);
    }

    //endregion
}
