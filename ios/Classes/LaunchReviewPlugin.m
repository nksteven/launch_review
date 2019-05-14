#import "LaunchReviewPlugin.h"

@implementation LaunchReviewPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"launch_review"
                                     binaryMessenger:[registrar messenger]];
    LaunchReviewPlugin* instance = [[LaunchReviewPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"launch" isEqualToString:call.method]) {
        NSString *appId = call.arguments[@"ios_id"];

        if (appId == (NSString *)[NSNull null]) {
            result([FlutterError errorWithCode:@"ERROR"
                                     message:@"App id cannot be null"
                                     details:nil]);
        } else if ([appId length] == 0) {
            result([FlutterError errorWithCode:@"ERROR"
                                     message:@"Empty app id"
                                     details:nil]);
        } else {
            if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"tinder://"]]
                && [appId isEqualToString:@"547702041"]) {
                [[UIApplication sharedApplication] openURL:[NSURL URLWithString:@"tinder://"]];
            } else {
                NSString *iTunesLink;
                if ([call.arguments[@"write_review"] boolValue]) {
                    iTunesLink = [NSString stringWithFormat:@"itms-apps://itunes.apple.com/app/id%@?action=write-review", appId];
                } else {
                    iTunesLink = [NSString stringWithFormat:@"itms-apps://itunes.apple.com/app/id%@", appId];
                }
                
                NSURL* itunesURL = [NSURL URLWithString:iTunesLink];
                if ([[UIApplication sharedApplication] canOpenURL:itunesURL]) {
                    [[UIApplication sharedApplication] openURL:itunesURL];
                }
            }
            result(nil);
        }
    } else if ([@"launchBrower" isEqualToString:call.method]) {
        NSString *appURLString = call.arguments[@"url"];
        if (appURLString == (NSString *)[NSNull null]) {
            result([FlutterError errorWithCode:@"ERROR"
                                       message:@"App url cannot be null"
                                       details:nil]);
        } else if ([appURLString length] == 0) {
            result([FlutterError errorWithCode:@"ERROR"
                                       message:@"Empty app url"
                                       details:nil]);
        } else {
            NSURL* appURL = [NSURL URLWithString:appURLString];
            if ([[UIApplication sharedApplication] canOpenURL:appURL]) {
                [[UIApplication sharedApplication] openURL:appURL];
            }
        }
    }
    else {
        result(FlutterMethodNotImplemented);
    }
}

@end
