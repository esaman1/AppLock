package com.google.android.gms.ads.ez;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.ads.ez.adparam.AdUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class Utils {
  
    public static String getTopActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        LogUtils.logString(Utils.class, "Top Activity " + taskInfo.get(0).topActivity.getClassName());
        return taskInfo.get(0).topActivity.getClassName();
    }

    public static boolean checkTopActivityIsAd(Context context) {
        if (Utils.getTopActivity(context).equals("com.google.android.gms.ads.AdActivity")) {
            return true;
        }
        return false;
    }

    public static boolean checkTopActivityIsEzTeam(Context context) {
        if (Utils.getTopActivity(context).indexOf("ezteam") != -1) {
            return true;
        }
        return false;
    }

    public static boolean CheckInstallerId(Context context) {
        if (AdUnit.isTEST()) {
            return true;
        }
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        // The package name of the app that has installed your app
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());

        // true if your app has been downloaded from Play Store
        Log.e("Utils", "CheckInstallerId: " + (installer != null && validInstallers.contains(installer)));
        return installer != null && validInstallers.contains(installer);
    }

    public static void RemoveViewInParent(ViewGroup view) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
    }
}
