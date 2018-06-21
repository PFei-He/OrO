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

import {
	NativeModules,
	NativeEventEmitter,
} from 'react-native';

const Adapter = new NativeEventEmitter(NativeModules.Adapter);

export default {
	/**
	 调用 Native 的 fromWeb 方法，Web 发起通信，Native 响应后回调到 Web

	 .toNative("I'm the communication from Web!", function (value) {
  		console.log(value);
	 });
	 */
	toNative: function (string, response) {
		NativeModules.Adapter.fromWeb(string, response)
	},

	/**
	 添加 Native 的 toWeb 方法的监听，Native 发起通信，Web 响应

	 .fromNative(data => {
  		console.log(data); 
	 });
	 */
	fromNative: function (response) {
		Adapter.addListener('toWeb', response);
	}
}
