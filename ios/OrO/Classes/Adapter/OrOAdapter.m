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

#import "OrOAdapter.h"

@implementation OrOAdapter

#pragma mark - React-Native Methods

// 此方法为定义一个给 JavaScript 调用此类的唯一标识名
RCT_EXPORT_MODULE(Adapter)

// 定义一个给 JavaScript 调用的方法，方法的第一个参数名为 JavaScript 的调用名，也是唯一标识名，所以此处不能有重名方法，否则最后定义的方法会顶替之前全部的重名方法
RCT_EXPORT_METHOD(test:(NSString *)string response:(RCTResponseSenderBlock)callback) {
    NSLog(@"%@", string);
    callback(@[@"I'm the callback from Objective-C!"]);
}

@end
