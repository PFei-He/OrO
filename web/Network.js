import {NativeModules} from 'react-native';

export default {

	/**
     * 设置调试模式
     *
     * @param trueOrFalse 开关
     */
	debugMode: function(trueOrFalse) {
		NativeModules.Network.debugMode(trueOrFalse)
	},

	/**
     * 设置超时时隔
     *
     * @param sec 时隔（秒）
     */
	timeoutInterval: function(sec) {
		NativeModules.Network.timeoutInterval(sec)
	},

	/**
     * 设置重试次数
     *
     * @param count 次数
     */
	retryTimes: function(count) {
		NativeModules.Network.retryTimes(count)
	},

	/**
     * 发送 GET 请求
     *
     * @param url 请求的地址
     * @param response 响应消息
     */
	GET: function(url, response) {
		NativeModules.Network.GET(url, response)
	},

	/**
     * 发送 POST 请求
     *
     * @param url 请求的地址
     * @param params 请求的参数
     * @param response 响应消息
     */
	POST: function(url, params, response) {
		NativeModules.Network.POST(url, params, response)
	},

	/**
     * 发送 DELETE 请求
     *
     * @param url 请求的地址
     * @param params 请求的参数
     * @param response 响应消息
     */
	DELETE: function(url, params, response) {
		NativeModules.Network.DELETE(url, params, response)
	}
}
