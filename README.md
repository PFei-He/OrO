OrO
===
[![](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/PFei-He/OrO/master/LICENSE)
![](https://img.shields.io/badge/platform-android-lightgrey.svg)
![](https://img.shields.io/badge/platform-ios-lightgrey.svg)

React-Native 构建的 Hybrid App


About / 简介
---
随着现在移动开发周期越来越短，各种各样的应用井喷式的出现，又如潮水般消失，导致应用的生命周期变得非常不稳定，各种快速开发，跨平台集成的需求越来越大，所以就催生了 `Native + Web` 这种快速构建的模式，利用 HTML5 搭建应用界面，原生做数据和逻辑处理。这样做的好处是减轻了移动端对不同设备做界面适配的工作，只需一套界面即可在所有设备中运行，大大减少开发时间。<br>
<br>
目前市面上存在的比较流行的框架分为 `React-Native` 和 `Cordova` ，这里选用的是 `React-Native` 。<br>
<br>
这套工程存在的目的，即是为不了解 Hybrid App 的开发者提供快速上手的教程，帮助其学会如何从零开始搭建，编写到最后打包成一个完整的应用。


Structure / 目录结构
---
```
OrO/
	../android/         // Android 端根目录
	App.js              // Web 端源码
	app.json            // 工程信息文件
	index.js            // Web 端入口文件
	../ios/             // iOS 端根目录
	../node_modules/    // npm 目录，存放构建工程需要的工具
	package.json        // 构建时的参数信息
	README.md           // 工程说明（即为此文件）
	../resources/       // 资源文件夹，用于存放工程用到的所有源文件
	../shell/           // 存放 Shell 脚本文件
	../web/             // Web 端根目录
	yarn.lock
```


Environment / 开发环境
---
Mac (macOS 10.13.3)


Development Tools / 开发工具
---
* `Web`<br>
[IntelliJ IDEA](https://www.jetbrains.com/idea/)<br>
[Sublime Text](https://www.sublimetext.com)

* `Android`<br>
[Android Studio](https://developer.android.com/studio/)

* `iOS`<br>
[Xcode](https://developer.apple.com/xcode/)


Detail / 说明
---
### 构建工程必知
为了方便传输，本工程已进行了简化处理，各种编译包和构建工具都已删除，运行时会报错，所以需要执行如下的 Shell 脚本将工程完善。
1. `yarn-install`  安装 npm
2. `server-start`  运行 Android 工程前，需要手动启动 `React-Native` 的 `Server` 才能连通代码和设备，否则会出现无页面的状态（页面红色报错），运行 iOS 工程会自动启动 `Server` 。
3. 每次修改 Web 端代码并保存后，移动端无需重新编译，只需刷新即可。Android 端设备可连按两次 `R` 键刷新，iOS 端可按 `Command + R` 键刷新。


### Placeholder / 占位符文件
占位符文件对于整个工程并没有实际作用，只是用于上传工程到 `GitHub` 时可以将空文件夹上传，开发者可于下载工程后将所有的占位符文件删除，对工程运行完全没有影响。


### Port / 监听端口
`React-Native` 默认监听本地端口 `localhost:8081`，但某些情况下，`8081` 端口可能会被占用，需更改监听的端口。`8082` 端口也被 `React-Native` 使用，可以同时更改。
* 通过命令修改
```
$react-native start --port=8888
```

* 通过文件修改（构建工程后，在以下文件搜索 `8081` 和 `8082` 端口，并更改为自己需要监听的端口）
```
// 8081 端口
node_modules/dom-walk/example/static/index.html
node_modules/react-native/Libraries/Core/Devtools/getDevServer.js
node_modules/react-native/Libraries/RCTTest/RCTTestRunner.m
node_modules/react-native/Libraries/Utilities/HMRClient.js
node_modules/react-native/local-cli/runAndroid/runAndroid.js
node_modules/react-native/local-cli/runIOS/runIOS.js
node_modules/react-native/local-cli/server/middleware/statusPageMiddleware.js
node_modules/react-native/local-cli/server/server.js
node_modules/react-native/local-cli/util/isPackagerRunning.js
node_modules/react-native/React/Base/RCTBridgeDelegate.h
node_modules/react-native/React/Base/RCTDefines.h
node_modules/react-native/React/DevSupport/RCTInspectorDevServerHelper.mm
node_modules/react-native/React/React.xcodeproj/project.pbxproj
node_modules/react-native/ReactAndroid/src/main/java/com/facebook/react/common/DebugServerException.java
node_modules/react-native/ReactAndroid/src/main/java/com/facebook/react/modules/systeminfo/AndroidInfoHelpers.java
node_modules/react-native/ReactAndroid/src/main/java/com/facebook/react/packagerconnection/PackagerConnectionSettings.java

// 8082 端口
node_modules/react-native/React/DevSupport/RCTInspectorDevServerHelper.mm
node_modules/react-native/ReactAndroid/src/main/java/com/facebook/react/modules/systeminfo/AndroidInfoHelpers.java
node_modules/react-native/ReactCommon/cxxreact/JSCExecutor.cpp
node_modules/react-transform-hmr/README.md
```



React-Native
===

Install / 安装
---
* `react-native-cli`
```
$npm install -g react-native-cli
```

* `yarn`
```
$npm install -g yarn
```

* `yarn + react-native-cli`
```
$npm install -g yarn react-native-cli
```


Upgrade / 升级
---
```
$npm update -g react-native-cli
```


Create / 创建
---
```
$react-native init AppName

// example / 范例
$react-native init OrO
```


Dependencies / 依赖
---
```
$cd OrO
$yarn install
```
Or / 或
```
$cd OrO
$npm i
```


Run Server / 启动服务
---
```
$react-native start
```


Clean / 清空构建
---

### Add / 添加
```
$yarn add -D react-native-clean-project
```

### Run / 运行
```
$./node_modules/.bin/react-native-clean-project
```
Or add it as a script to your `OrO/package.json` / 或修改文件 `OrO/package.json`
###### Path / 文件路径
```
// line 8 / 8行
"clean": "react-native-clean-project"
```
```
$yarn clean
```


Android
===

Root Path / 目录
---
`OrO/android/`


Library / 类库
---
#### Path / 文件路径
`/OrO/android/app/build.gradle`

#### Edit / 编写
* [Volley](https://github.com/google/volley)
```
// line 152 / 152行
compile 'com.android.volley:volley:1.0.0'
```


Gradle
---
解决 `Android Studio` 使用 `Gradle` 构建工程慢，从 [Gradle官网](https://gradle.org) 下载适用的版本，将zip包放入到 `/Users/username/.gradle/wrapper/dists/gradle-*-all/*/` (* 表示版本号)目录下，重新打开工程，`Android Studio` 会自行解压安装。


Java 8
---
#### Path / 文件路径
`/OrO/android/app/build.gradle`

#### Edit / 编写
```
// line 110-112 / 110-112行
jackOptions {
	enabled true
}
```



iOS
===

Root Path / 目录
---
`OrO/ios/`


Library / 类库
---
#### Path / 文件路径
`/OrO/ios/Podfile`

#### Edit / 编写
* [AFNetworking](https://github.com/AFNetworking/AFNetworking)
```
// line 5 / 5行
pod 'AFNetworking', '~> 2.6.0'
```


CocoaPods
---
* `Install / 安装`
```
$sudo gem install -n /usr/local/bin cocoapods
```

* `Download / 下载类库`
```
$pod install
```



Shell
===

Root Path / 目录
---
`/OrO/shell/`


Detail / 说明
---
* `install-react-native`<br>
安装 `React-Native`

* `install-yarn`<br>
安装 `yarn`

* `pod-install`<br>
为 iOS 工程导入指定的类库

* `server-start`<br>
启动 `React-Native` 的服务

* `yarn-clean`<br>
删除 npm 构建工具包并清空构建缓存

* `yarn-install`<br>
安装 npm 构建工具包



License
===
`OrO` is released under the MIT license, see [LICENSE](https://raw.githubusercontent.com/PFei-He/OrO/master/LICENSE) for details.
