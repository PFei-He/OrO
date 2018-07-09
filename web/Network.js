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

import {NativeModules} from 'react-native';

export default {

	/**
     * 设置调试模式
     * @param openOrNot 开关
     */
	debugMode: function (openOrNot) {
		NativeModules.Network.debugMode(openOrNot)
	},

	/**
     * 设置超时时隔
     * @param sec 时隔（秒）
     */
	timeoutInterval: function (sec) {
		NativeModules.Network.timeoutInterval(sec)
	},

	/**
     * 设置重试次数
     * @param count 次数
     */
	retryTimes: function (count) {
		NativeModules.Network.retryTimes(count)
	},

	/**
     * 发送 GET 请求
     * @param url 请求的地址
     * @param response 响应消息
     */
	GET: function (url, response) {
		NativeModules.Network.GET(url, response)
	},

	/**
     * 发送 POST 请求
     * @param url 请求的地址
     * @param params 请求的参数
     * @param response 响应消息
     */
	POST: function (url, params, response) {
		NativeModules.Network.POST(url, params, response)
	},

	/**
     * 发送 DELETE 请求
     * @param url 请求的地址
     * @param params 请求的参数
     * @param response 响应消息
     */
	DELETE: function (url, params, response) {
		NativeModules.Network.DELETE(url, params, response)
	}
}
