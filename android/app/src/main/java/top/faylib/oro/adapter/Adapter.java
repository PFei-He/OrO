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

import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class Adapter extends ReactContextBaseJavaModule {

    //region Life Cycle

    public Adapter(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    //endregion


    //region React-Native Methods

    // 此方法为定义一个给 JavaScript 调用此类的唯一标识名
    @Override
    public String getName() {
        // 返回标识名
        return "Adapter";
    }

    // 定义一个给 JavaScript 调用的方法，必须加入 @ReactMethod 关键字，表示声明此方法是提供给 JavaScript 的，方法名是 JavaScript 的调用名，也是唯一标识名，所以此处不能有重名方法，否则最后定义的方法会顶替之前全部的重名方法
    @ReactMethod
    public void test(String string, Callback callback) {
        Log.i("OrO", string);
        callback.invoke("I'm the callback from Java!");
    }

    //endregion
}
