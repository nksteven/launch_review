package com.iyaffle.launchreview;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import android.content.Intent;
import android.net.Uri;
import android.content.ActivityNotFoundException;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * LaunchReviewPlugin
 */
public class LaunchReviewPlugin implements MethodCallHandler {

  private final Registrar mRegistrar;

  private LaunchReviewPlugin(Registrar registrar) {
    this.mRegistrar = registrar;
  }

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "launch_review");
    LaunchReviewPlugin instance = new LaunchReviewPlugin(registrar);
    channel.setMethodCallHandler(instance);
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("launch")) {
      String appPackageName = call.argument("android_id");

      if (appPackageName == null) {
        appPackageName = mRegistrar.activity().getPackageName();
      }

      boolean isAppInstalled = false;
      PackageInfo packageInfo = null;
      try {
        packageInfo = mRegistrar.activity().getPackageManager().getPackageInfo(appPackageName, 0);
      } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
      } finally {
        isAppInstalled = packageInfo != null;
      }

      try {
        Intent intent;

        if (isAppInstalled) {
          PackageManager packageManager = mRegistrar.activity().getPackageManager();
          intent = packageManager.getLaunchIntentForPackage(appPackageName);
        } else {
          intent = new Intent(Intent.ACTION_VIEW,
                  Uri.parse("market://details?id=" + appPackageName));
        }

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        mRegistrar.activity().startActivity(intent);
      } catch (ActivityNotFoundException e) {
          mRegistrar.activity().startActivity(new Intent(Intent.ACTION_VIEW,
              Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
      }

      result.success(null);
    } else if(call.method.equals("launchBrower")){
      String url = call.argument("url");
      if (url != null && url != "") {
        Uri uri = Uri.parse(url);
        Intent uriIntent = new Intent(Intent.ACTION_VIEW, uri);
        mRegistrar.activity().startActivity(uriIntent);
        result.success(null);
      } else {
        result.error("exception", "the argument url is must be required", null);
      }
    }else{
      result.notImplemented();
    }
  }
}
