/**
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
 */

package top.faylib.oro.adapter;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
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
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

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

    // 请求头
    private Map<String, String> headers = new HashMap<>();

    // 请求结果状态码
    private int statusCode;

    //endregion


    //region Life Cycle

    public Network(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    //endregion


    //region Private Methods

    // 将 JSONObject 类型转换为 WritableMap 类型
    private static WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {
        WritableMap map = new WritableNativeMap();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                map.putMap(key, convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                map.putArray(key, convertJsonToArray((JSONArray) value));
            } else if (value instanceof Boolean) {
                map.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                map.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                map.putDouble(key, (Double) value);
            } else if (value instanceof String) {
                map.putString(key, (String) value);
            } else {
                map.putString(key, value.toString());
            }
        }
        return map;
    }

    // 将 JSONArray 类型转换为 WritableArray 类型
    private static WritableArray convertJsonToArray(JSONArray jsonArray) throws JSONException {
        WritableArray array = new WritableNativeArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                array.pushMap(convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                array.pushArray(convertJsonToArray((JSONArray) value));
            } else if (value instanceof Boolean) {
                array.pushBoolean((Boolean) value);
            } else if (value instanceof Integer) {
                array.pushInt((Integer) value);
            } else if (value instanceof Double) {
                array.pushDouble((Double) value);
            } else if (value instanceof String) {
                array.pushString((String) value);
            } else {
                array.pushString(value.toString());
            }
        }
        return array;
    }

    // 将 ReadableMap 类型转换为 JSONObject 类型
    private static JSONObject convertMapToJson(ReadableMap readableMap) throws JSONException {
        JSONObject object = new JSONObject();
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            switch (readableMap.getType(key)) {
                case Null:
                    object.put(key, JSONObject.NULL);
                    break;
                case Boolean:
                    object.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    object.put(key, readableMap.getDouble(key));
                    break;
                case String:
                    object.put(key, readableMap.getString(key));
                    break;
                case Map:
                    object.put(key, convertMapToJson(readableMap.getMap(key)));
                    break;
                case Array:
                    object.put(key, convertArrayToJson(readableMap.getArray(key)));
                    break;
            }
        }
        return object;
    }

    // 将 ReadableArray 类型转换为 JSONArray 类型
    private static JSONArray convertArrayToJson(ReadableArray readableArray) throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < readableArray.size(); i++) {
            switch (readableArray.getType(i)) {
                case Null:
                    break;
                case Boolean:
                    array.put(readableArray.getBoolean(i));
                    break;
                case Number:
                    array.put(readableArray.getDouble(i));
                    break;
                case String:
                    array.put(readableArray.getString(i));
                    break;
                case Map:
                    array.put(convertMapToJson(readableArray.getMap(i)));
                    break;
                case Array:
                    array.put(convertArrayToJson(readableArray.getArray(i)));
                    break;
            }
        }
        return array;
    }

    // 将 ReadableMap 类型转换为 Map<String, Object> 类型
    private static Map<String, Object> convertMapToMap(ReadableMap readableMap) {
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        Map<String, Object> map = new HashMap<>();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            ReadableType type = readableMap.getType(key);
            switch (type) {
                case Null:
                    map.put(key, null);
                    break;
                case Boolean:
                    map.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    map.put(key, readableMap.getDouble(key));
                    break;
                case String:
                    map.put(key, readableMap.getString(key));
                    break;
                case Map:
                    map.put(key, convertMapToMap(readableMap.getMap(key)));
                    break;
                case Array:
                    map.put(key, convertArrayToList(readableMap.getArray(key)));
                    break;
                default:
                    throw new IllegalArgumentException("Could not convert object with key: " + key + ".");
            }
        }
        return map;
    }

    // 将 ReadableMap 类型转换为 Map<String, String> 类型
    private static Map<String, String> convertMapToStringMap(ReadableMap readableMap) {
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        Map<String, String> map = new HashMap<>();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            map.put(key, readableMap.getString(key));
        }
        return map;
    }

    // 将 ReadableArray 类型转换为 List<Object> 类型
    private static List<Object> convertArrayToList(ReadableArray readableArray) {
        List<Object> list = new ArrayList<>(readableArray.size());
        for (int i = 0; i < readableArray.size(); i++) {
            ReadableType indexType = readableArray.getType(i);
            switch(indexType) {
                case Null:
                    list.add(i, null);
                    break;
                case Boolean:
                    list.add(i, readableArray.getBoolean(i));
                    break;
                case Number:
                    list.add(i, readableArray.getDouble(i));
                    break;
                case String:
                    list.add(i, readableArray.getString(i));
                    break;
                case Map:
                    list.add(i, convertMapToMap(readableArray.getMap(i)));
                    break;
                case Array:
                    list.add(i, convertArrayToList(readableArray.getArray(i)));
                    break;
                default:
                    throw new IllegalArgumentException("Could not convert object at index " + i + ".");
            }
        }
        return list;
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
    private void send(int method, String url, Map params, int retryTimes, Callback callback) {

        switch (method) {
            case 0:
                debugLog(retryTimes == this.retryTimes ? "[ REQUEST ] Start sending" : "[ REQUEST ] Retrying",
                        "[ URL ] " + url,
                        "[ METHOD ] GET",
                        "[ PARAMS ] " + params.toString(),
                        "[ RETRY TIMES ] " + String.valueOf(retryTimes),
                        "[ TIMEOUT INTERVAL ] " + String.valueOf(timeoutInterval/1000));
                break;
            case 1:
                debugLog(retryTimes == this.retryTimes ? "[ REQUEST ] Start sending" : "[ REQUEST ] Retrying",
                        "[ URL ] " + url,
                        "[ METHOD ] POST",
                        "[ PARAMS ] " + params.toString(),
                        "[ RETRY TIMES ] " + String.valueOf(retryTimes),
                        "[ TIMEOUT INTERVAL ] " + String.valueOf(timeoutInterval/1000));
                break;
            case 3:
                debugLog(retryTimes == this.retryTimes ? "[ REQUEST ] Start sending" : "[ REQUEST ] Retrying",
                        "[ URL ] " + url,
                        "[ METHOD ] DELETE",
                        "[ PARAMS ] " + params.toString(),
                        "[ RETRY TIMES ] " + String.valueOf(retryTimes),
                        "[ TIMEOUT INTERVAL ] " + String.valueOf(timeoutInterval/1000));
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
                send(method, url, params, count, callback);
            }
        }) {
            // 重写请求头
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.putAll(super.getHeaders());
                debugLog("[ HEADERS ] " + headers.toString());
                return headers;
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

            // 重写解析服务器返回的数据
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                return super.parseNetworkResponse(response);
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
            jsonObject.put("response", result.toString());
        } catch (JSONException e) { e.printStackTrace(); }

        // 回调结果到 Web 端
        if (statusCode == 200) {
            if (result instanceof JSONObject) {
                debugLog("[ REQUEST ] Success", "[ URL ] " + url);
                try {
                    callback.invoke(convertJsonToMap((JSONObject) result));
                } catch (JSONException e) { e.printStackTrace(); }
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
     * 设置重试次数
     * @param headers 请求头
     */
    @ReactMethod
    public void setHeaders(ReadableMap headers) {
        debugLog("[ FUNCTION ] '" + getMethodName() + "' run");
        this.headers.putAll(headers != null ? convertMapToStringMap(headers) : new HashMap());
    }

    /**
     * 发送 GET 请求
     * @param url 请求的地址
     * @param params 请求的参数
     * @param callback 与 JavaScript 通信的变量，用于响应消息后回调
     */
    @ReactMethod
    public void GET(String url, ReadableMap params, Callback callback) {
        debugLog("[ FUNCTION ] '" + getMethodName() + "' run");
        Map map = params != null ? convertMapToMap(params) : new HashMap();
        send(Request.Method.GET, url, map, retryTimes, callback);
    }

    /**
     * 发送 POST 请求
     * @param url 请求的地址
     * @param params 请求的参数
     * @param callback 与 JavaScript 通信的变量，用于响应消息后回调
     */
    @ReactMethod
    public void POST(String url, ReadableMap params, Callback callback) {
        debugLog("[ FUNCTION ] '" + getMethodName() + "' run");
        Map map = params != null ? convertMapToMap(params) : new HashMap();
        send(Request.Method.POST, url, map, retryTimes, callback);
    }

    /**
     * 发送 DELETE 请求
     * @param url 请求的地址
     * @param params 请求的参数
     * @param callback 与 JavaScript 通信的变量，用于响应消息后回调
     */
    @ReactMethod
    public void DELETE(String url, ReadableMap params, Callback callback) {
        debugLog("[ FUNCTION ] '" + getMethodName() + "' run");
        Map map = params != null ? convertMapToMap(params) : new HashMap();
        send(Request.Method.DELETE, url, map, retryTimes, callback);
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
