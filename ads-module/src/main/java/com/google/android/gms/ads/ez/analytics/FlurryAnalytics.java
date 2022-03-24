package com.google.android.gms.ads.ez.analytics;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.ez.LogUtils;
import com.flurry.android.FlurryAgent;

public class FlurryAnalytics {

    public static void init(Context context, String id) {
        LogUtils.logString(FlurryAnalytics.class, id);
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(context, id);


    }

    public static void logEvent(String screen, String action) {
        LogUtils.logString(FlurryAnalytics.class, "Log Event Flurry - " + screen + "__" + action);
        FlurryAgent.logEvent(screen + "__" + action);
    }
}