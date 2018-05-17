import {NativeModules} from 'react-native';

export default {
	
	// 调用原生的 test 方法
	test: function(string, response) {
		NativeModules.Adapter.test(string, response)
	}
}
