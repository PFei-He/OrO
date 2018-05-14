//
//  OrONetwork.m
//  OrO
//
//  Created by Fay on 2018/5/14.
//  Copyright © 2018年 Facebook. All rights reserved.
//

#import "OrONetwork.h"

@implementation OrONetwork

// 此方法为定义一个给 JavaScript 调用此类的唯一标识名
RCT_EXPORT_MODULE(Network)

RCT_EXPORT_METHOD(GET:(NSString *)url callback:(RCTResponseSenderBlock)callback) {
  NSLog(@"%@", url);
  callback(@[@"I'm the callback from Objective-C!"]);
}

@end
