package com.google.android.gms.ads.ez;

import android.util.Log;

public class LogUtils {
    public static void logString(Object o, String s) {
        Log.e(o.getClass().getSimpleName() + "_EzAd", s);
    }
    public static void logString(Class o, String s) {
        Log.e(o.getSimpleName() + "_EzAd", s);
    }
    public static void logString( String s) {
        Log.e("NoName"+ "_EzAd", s);
    }
}
