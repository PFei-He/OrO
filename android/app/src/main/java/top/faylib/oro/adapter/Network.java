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

import android.net.Uri;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    // 请求结果状态码
    private int statusCode;

    //endregion


    //region Life Cycle

    public Network(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    //endregion


    //region Private Methods

    // JSONObject 格式转 Map 格式
    private static Map<String, Object> toMap(JSONObject jsonObject) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }   return map;
    }

    // JSONArray 格式转 List 格式
    private static List<Object> toList(JSONArray jsonArray) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }   return list;
    }

    // 拼接参数
    private String appendParameter(String url, Map<String, String> params) {
        Uri uri = Uri.parse(url);
        Uri.Builder builder = uri.buildUpon();
        for(Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build().getQuery();
    }

    // 打印调试信息
    private void debugLog(String ... strings) {
        if (debugMode) {
            for (String string : strings) {
                Log.i("OrO", "[ OrO ][ NETWORK ]" + string + ".");
            }
        }
    }

    // 获取方法名
    private String getMethodName() {

        /* p.s. STACK_TRACE_INDEX = 3是因为Android是下标为3获取方法名， 纯Java是下标为2获取方法名。 */
        final int STACK_TRACE_INDEX = 3;

        // 获取调用的函数堆栈信息
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement targetElement = stackTrace[STACK_TRACE_INDEX];

        return targetElement.getMethodName();
    }

    // 发送请求
    private void requset(int method, String url, Map params, int retryTimes, Callback callback) {

        switch (method) {
            case 0:
                debugLog("[ REQUEST ] Start sending", "[ URL ] " + url, "[ METHOD ] GET", "[ PARAMS ] " + params.toString(), "[ RETRY TIMES ] " + String.valueOf(retryTimes), "[ TIMEOUT INTERVAL ] " + String.valueOf(timeoutInterval/1000));
                break;
            case 1:
                debugLog("[ REQUEST ] Start sending", "[ URL ] " + url, "[ METHOD ] POST", "[ PARAMS ] " + params.toString(), "[ RETRY TIMES ] " + String.valueOf(retryTimes), "[ TIMEOUT INTERVAL ] " + String.valueOf(timeoutInterval/1000));
                break;
            case 3:
                debugLog("[ REQUEST ] Start sending", "[ URL ] " + url, "[ METHOD ] DELETE", "[ PARAMS ] " + params.toString(), "[ RETRY TIMES ] " + String.valueOf(retryTimes), "[ TIMEOUT INTERVAL ] " + String.valueOf(timeoutInterval/1000));
                break;
            default:
                break;
        }

        retryTimes--;
        int count = retryTimes;

        JsonObjectRequest request = new JsonObjectRequest(method, url, null, response -> {
            parse(url, statusCode, response, callback);
        }, error -> {
            if (count < 1) {
                parse(url, statusCode, error, callback);
            } else {
                requset(method, url, params, count, callback);
            }
        }) {
            // 重写解析服务器返回的数据
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }

            // 重写请求体的内容类型
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
            }

            // 重写请求体
            @Override
            public byte[] getBody() {
                try {
                    final String string = appendParameter(url, params);
                    return string.getBytes(PROTOCOL_CHARSET);
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };

        // 添加请求超时时隔
        request.setRetryPolicy(new DefaultRetryPolicy(timeoutInterval,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // 添加请求到队列
        queue.add(request);
    }

    // 数据处理
    private void parse(String url, int statusCode, Object result, Callback callback) {

        // 处理请求结果
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("statusCode", statusCode);
            jsonObject.put("result", result);
        } catch (JSONException e) {
//            e.printStackTrace();
        }

        // 回调结果到 Web 端
        if (statusCode == 200) {
            if (result instanceof JSONObject) {
                debugLog("[ REQUEST ] Success", "[ URL ] " + url);
                callback.invoke(jsonObject.toString());
            } else {
                debugLog("[ REQUEST ] Success but not JSON data", "[ URL ] " + url);
                callback.invoke(jsonObject.toString());
            }
        } else {
            debugLog("[ REQUEST ] Failure", "[ URL ] " + url);
            callback.invoke(jsonObject.toString());
        }
    }

    //endregion


    //region React-Native Methods

    @Override
    public String getName() {
        return "Network";
    }

    /**
     * 设置调试模式
     * @param openOrNot 开关
     */
    @ReactMethod
    public void debugMode(boolean openOrNot) {
        debugMode = openOrNot;
        debugLog("[ FUNCTION ] '" + getMethodName() + "' run", " Debug Mode Open");
    }

    /**
     * 设置超时时隔
     * @param sec 时隔（秒）
     */
    @ReactMethod
    public void timeoutInterval(int sec) {
        debugLog("[ FUNCTION ] '" + getMethodName() + "' run");
        timeoutInterval = sec;
    }

    /**
     * 设置重试次数
     * @param count 次数
     */
    @ReactMethod
    public void retryTimes(int count) {
        debugLog("[ FUNCTION ] '" + getMethodName() + "' run");
        retryTimes = count;
    }

    /**
     * 发送 GET 请求
     * @param url 请求的地址
     * @param params 请求的参数
     * @param callback 与 JavaScript 通信的变量，用于响应消息后回调
     */
    @ReactMethod
    public void GET(String url, JSONObject params, Callback callback) {
        try {
            debugLog("[ FUNCTION ] '" + getMethodName() + "' run");
            Map map = toMap(params!=null ? params : new JSONObject("{}"));
            requset(Request.Method.GET, url, map, retryTimes, callback);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    /**
     * 发送 POST 请求
     * @param url 请求的地址
     * @param params 请求的参数
     * @param callback 与 JavaScript 通信的变量，用于响应消息后回调
     */
    @ReactMethod
    public void POST(String url, JSONObject params, Callback callback) {
        try {
            debugLog("[ FUNCTION ] '" + getMethodName() + "' run");
            Map map = toMap(params!=null ? params : new JSONObject("{}"));
            requset(Request.Method.POST, url, map, retryTimes, callback);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    /**
     * 发送 DELETE 请求
     * @param url 请求的地址
     * @param params 请求的参数
     * @param callback 与 JavaScript 通信的变量，用于响应消息后回调
     */
    @ReactMethod
    public void DELETE(String url, JSONObject params, Callback callback) {
        try {
            debugLog("[ FUNCTION ] '" + getMethodName() + "' run");
            Map map = toMap(params!=null ? params : new JSONObject("{}"));
            requset(Request.Method.DELETE, url, map, retryTimes, callback);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    /**
     * 重置请求
     */
    @ReactMethod
    public void reset() {
        debugLog("[ FUNCTION ] '" + getMethodName() + "' run");
        timeoutInterval = 120000;
        retryTimes = 1;
    }

    //endregion
}
