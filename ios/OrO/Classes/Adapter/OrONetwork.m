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

// 网络请求
@property (nonatomic, strong) AFHTTPSessionManager *manager;

// 超时时隔
@property (nonatomic) NSInteger timeoutInterval;

// 尝试次数
@property (nonatomic) NSInteger tryTimes;

@end

@implementation OrONetwork

#pragma mark - Setter / Getter Methods

// 网络请求
- (AFHTTPSessionManager *)manager
{
    if (!_manager) {
        _manager = [AFHTTPSessionManager manager];
        _manager.responseSerializer.acceptableContentTypes = [_manager.responseSerializer.acceptableContentTypes setByAddingObject:@"text/html"];
    }
    return _manager;
}

// 超时时隔
- (void)setTimeoutInterval:(NSInteger)timeoutInterval
{
    self.manager.requestSerializer.timeoutInterval = timeoutInterval;
    _timeoutInterval = timeoutInterval;
}


#pragma mark - Private Methods

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
- (void)sendWithMethod:(OrONetworkRequestMethod)method url:(NSString *)url params:(NSDictionary *)params tryTimes:(NSInteger)tryTimes response:(RCTResponseSenderBlock)callback
{
    tryTimes--;
    switch (method) {
        case OrONetworkRequestMethodGET:
        {
            [self.manager GET:url parameters:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nonnull responseObject) {
                callback(@[[self parseJSON:responseObject]]);
            } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
                if (tryTimes < 1) NSLog(@"%@", error);
                else [self sendWithMethod:OrONetworkRequestMethodGET url:url params:nil tryTimes:tryTimes response:callback];
            }];
        }
            break;
        case OrONetworkRequestMethodPOST:
        {
            [self.manager POST:url parameters:params success:^(NSURLSessionDataTask * _Nonnull task, id  _Nonnull responseObject) {
                callback(@[[self parseJSON:responseObject]]);
            } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
                if (tryTimes < 1) NSLog(@"%@", error);
                else [self sendWithMethod:OrONetworkRequestMethodPOST url:url params:params tryTimes:tryTimes response:callback];
            }];
        }
            break;
        case OrONetworkRequestMethodDELETE:
        {
            [self.manager DELETE:url parameters:params success:^(NSURLSessionDataTask * _Nonnull task, id  _Nonnull responseObject) {
                callback(@[[self parseJSON:responseObject]]);
            } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
                if (tryTimes < 1) NSLog(@"%@", error);
                else [self sendWithMethod:OrONetworkRequestMethodDELETE url:url params:params tryTimes:tryTimes response:callback];
            }];
        }
            break;
        default:
            break;
    }
}


#pragma mark - React-Native Methods

RCT_EXPORT_MODULE(Network)

/**
 * 设置超时时隔
 *
 * @param sec: 时隔（秒）
 */
RCT_EXPORT_METHOD(timeoutInterval:(NSNumber *)sec) {
    self.timeoutInterval = sec.integerValue;
}

/**
 * 发送 GET 请求
 *
 * @param url: 请求的地址
 * @param callback: 与 JavaScript 通信的变量，用于响应消息后回调
 */
RCT_EXPORT_METHOD(GET:(NSString *)url response:(RCTResponseSenderBlock)callback) {
    [self sendWithMethod:OrONetworkRequestMethodGET url:url params:nil tryTimes:self.tryTimes response:callback];
}

/**
 * 发送 POST 请求
 *
 * @param url: 请求的地址
 * @param params: 请求的参数
 * @param callback: 与 JavaScript 通信的变量，用于响应消息后回调
 */
RCT_EXPORT_METHOD(POST:(NSString *)url params:(NSDictionary *)params response:(RCTResponseSenderBlock)callback) {
    [self sendWithMethod:OrONetworkRequestMethodPOST url:url params:params tryTimes:self.tryTimes response:callback];
}

/**
 * 发送 DELETE 请求
 *
 * @param url: 请求的地址
 * @param params: 请求的参数
 * @param callback: 与 JavaScript 通信的变量，用于响应消息后回调
 */
RCT_EXPORT_METHOD(DELETE:(NSString *)url params:(NSDictionary *)params response:(RCTResponseSenderBlock)callback) {
    [self sendWithMethod:OrONetworkRequestMethodDELETE url:url params:params tryTimes:self.tryTimes response:callback];
}

@end
