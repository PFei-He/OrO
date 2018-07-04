//
//  Copyright (c) 2018 faylib.top
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
//

#import "OrONetwork.h"
#import <AFNetworking/AFNetworking.h>

typedef NS_ENUM(NSUInteger, OrONetworkRequestMethod) {
    OrONetworkRequestMethodGET,
    OrONetworkRequestMethodPOST,
    OrONetworkRequestMethodDELETE,
};

@interface OrONetwork ()

// 调试模式
@property (nonatomic, assign) BOOL debugMode;

// 网络请求
@property (nonatomic, strong) AFHTTPSessionManager *manager;

// 超时时隔
@property (nonatomic) NSInteger timeoutInterval;

// 重试次数
@property (nonatomic) NSInteger retryTimes;

@end

@implementation OrONetwork

#pragma mark - Setter / Getter Methods

// 网络请求
- (AFHTTPSessionManager *)manager
{
    if (!_manager) {
        _manager = [AFHTTPSessionManager manager];
        _manager.responseSerializer.acceptableContentTypes = [_manager.responseSerializer.acceptableContentTypes setByAddingObject:@"text/html"];
        _manager.requestSerializer.timeoutInterval = 120;
    }
    return _manager;
}

// 超时时隔
- (void)setTimeoutInterval:(NSInteger)timeoutInterval
{
    self.manager.requestSerializer.timeoutInterval = timeoutInterval;
    _timeoutInterval = timeoutInterval;
}

// 重试次数
- (NSInteger)retryTimes
{
    if (_retryTimes == 0) {
        _retryTimes = 1;
    }
    return _retryTimes;
}


#pragma mark - Private Methods

// 打印调试信息
- (void)debugLog:(NSString *)strings, ...
{
    if (self.debugMode) {
        NSLog(@"[ OrO ][ NETWORK ][ DEBUG ]%@.", strings);
        va_list list;
        va_start(list, strings);
        while (strings != nil) {
            NSString *string = va_arg(list, NSString *);
            if (!string) break;
            NSLog(@"[ OrO ][ NETWORK ][ DEBUG ]%@.", string);
        }
        va_end(list);
    }
}

// 解析 JSON
- (NSString *)parseJSON:(id)json
{
    NSError *error;
    NSString *jsonString;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:json options:NSJSONWritingPrettyPrinted error:&error];
    if (jsonData) jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    return jsonString;
}

// 发送请求
- (void)requsetWithMethod:(OrONetworkRequestMethod)method url:(NSString *)url params:(NSDictionary *)params retryTimes:(NSInteger)count response:(RCTResponseSenderBlock)callback
{
    [self debugLog:@" Request sending with arguments", nil];
    
    if (method == OrONetworkRequestMethodGET) [self debugLog:@"[ METHOD ] GET", nil];
    else if (method == OrONetworkRequestMethodPOST) [self debugLog:@"[ METHOD ] POST", nil];
    else if (method == OrONetworkRequestMethodDELETE) [self debugLog:@"[ METHOD ] DELETE", nil];
    
    [self debugLog:[NSString stringWithFormat:@"[ URL ] %@", url], [NSString stringWithFormat:@"[ PARAMS ] %@", params], [NSString stringWithFormat:@"[ RETRY TIMES ] %@", @(count)], [NSString stringWithFormat:@"[ TIMEOUT INTERVAL ] %@", @(self.manager.requestSerializer.timeoutInterval)], nil];

    count--;
    
    switch (method) {
        case OrONetworkRequestMethodGET:
        {
            [self.manager GET:url parameters:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nonnull responseObject) {
                [self parseWithURL:url task:task result:responseObject response:callback];
            } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
                if (count < 1) [self parseWithURL:url task:task result:[NSString stringWithFormat:@"%@", error] response:callback];
                else [self requsetWithMethod:OrONetworkRequestMethodGET url:url params:params retryTimes:count response:callback];
            }];
        }
            break;
        case OrONetworkRequestMethodPOST:
        {
            [self.manager POST:url parameters:params success:^(NSURLSessionDataTask * _Nonnull task, id  _Nonnull responseObject) {
                [self parseWithURL:url task:task result:responseObject response:callback];
            } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
                if (count < 1) [self parseWithURL:url task:task result:[NSString stringWithFormat:@"%@", error] response:callback];
                else [self requsetWithMethod:OrONetworkRequestMethodPOST url:url params:params retryTimes:count response:callback];
            }];
        }
            break;
        case OrONetworkRequestMethodDELETE:
        {
            [self.manager DELETE:url parameters:params success:^(NSURLSessionDataTask * _Nonnull task, id  _Nonnull responseObject) {
                [self parseWithURL:url task:task result:responseObject response:callback];
            } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
                if (count < 1) [self parseWithURL:url task:task result:[NSString stringWithFormat:@"%@", error] response:callback];
                else [self requsetWithMethod:OrONetworkRequestMethodDELETE url:url params:params retryTimes:count response:callback];
            }];
        }
            break;
        default:
            break;
    }
}

// 数据处理
- (void)parseWithURL:(NSString *)url task:(NSURLSessionTask *)task result:(id)result response:(RCTResponseSenderBlock)callback
{
    // 请求结果状态码
    NSHTTPURLResponse *response = (NSHTTPURLResponse *)task.response;
    NSInteger statusCode = [response statusCode];
    
    // 回调结果到 Web 端
    if (statusCode == 200) {
        if ([result isKindOfClass:[NSDictionary class]]) {
            [self debugLog:@" Request success", [NSString stringWithFormat:@"[ URL ] %@", url], nil];
            callback(@[@{@"statusCode": @(statusCode), @"result": result}]);
        } else {
            [self debugLog:@" Request success but not JSON data", [NSString stringWithFormat:@"[ URL ] %@", url], nil];
            callback(@[@{@"statusCode": @(statusCode), @"result": result}]);
        }
    } else {
        [self debugLog:@" Request failure", [NSString stringWithFormat:@"[ URL ] %@", url], nil];
        callback(@[@{@"statusCode": @(statusCode), @"result": result}]);
    }
}


#pragma mark - React-Native Methods

RCT_EXPORT_MODULE(Network)

/**
 设置调试模式
 @param yesOrNo: 开关
 */
RCT_EXPORT_METHOD(debugMode:(BOOL)yesOrNo) {
    self.debugMode = yesOrNo;
    [self debugLog:[NSString stringWithFormat:@" '%@' run", NSStringFromSelector(_cmd)], @" Debug Mode Open", nil];
}

/**
 设置超时时隔
 @param sec: 时隔（秒）
 */
RCT_EXPORT_METHOD(timeoutInterval:(NSNumber *)sec) {
    [self debugLog:[NSString stringWithFormat:@" '%@' run", NSStringFromSelector(_cmd)], nil];
    self.timeoutInterval = sec.integerValue;
}

/**
 设置重试次数
 @param count: 次数
 */
RCT_EXPORT_METHOD(retryTimes:(NSNumber *)count) {
    [self debugLog:[NSString stringWithFormat:@" '%@' run", NSStringFromSelector(_cmd)], nil];
    self.retryTimes = count.integerValue;
}

/**
 发送 GET 请求
 @param url: 请求的地址
 @param callback: 与 JavaScript 通信的变量，用于响应消息后回调
 */
RCT_EXPORT_METHOD(GET:(NSString *)url response:(RCTResponseSenderBlock)callback) {
    [self debugLog:[NSString stringWithFormat:@" '%@' run", NSStringFromSelector(_cmd)], nil];
    [self requsetWithMethod:OrONetworkRequestMethodGET url:url params:nil retryTimes:self.retryTimes response:callback];
}

/**
 发送 POST 请求
 @param url: 请求的地址
 @param params: 请求的参数
 @param callback: 与 JavaScript 通信的变量，用于响应消息后回调
 */
RCT_EXPORT_METHOD(POST:(NSString *)url params:(NSDictionary *)params response:(RCTResponseSenderBlock)callback) {
    [self debugLog:[NSString stringWithFormat:@" '%@' run", NSStringFromSelector(_cmd)], nil];
    [self requsetWithMethod:OrONetworkRequestMethodPOST url:url params:params retryTimes:self.retryTimes response:callback];
}

/**
 发送 DELETE 请求
 @param url: 请求的地址
 @param params: 请求的参数
 @param callback: 与 JavaScript 通信的变量，用于响应消息后回调
 */
RCT_EXPORT_METHOD(DELETE:(NSString *)url params:(NSDictionary *)params response:(RCTResponseSenderBlock)callback) {
    [self debugLog:[NSString stringWithFormat:@" '%@' run", NSStringFromSelector(_cmd)], nil];
    [self requsetWithMethod:OrONetworkRequestMethodDELETE url:url params:params retryTimes:self.retryTimes response:callback];
}

@end
