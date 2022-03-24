package com.google.android.gms.ads.ez.analytics;


import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseAnalTool {
    private static FirebaseAnalTool mFirebaseAnalTool;
    private FirebaseAnalytics mFirebaseAnalytics;
    public static final String SPACE = " <-> ";
    private final String KEY_SCREEN = "SCREEN";
    private final String KEY_EVENT_NAME = "EVENT_NAME";

    public static class Param {
        public static final String OPEN_APP = "Open app";
        public static final String OPEN_SCREEN = "Open screen";
        public static final String APP_OPEN_WITH_MAIN = "Open app by main";
        public static final String APP_OPEN_WITH_FILE = "Open app by file";
        public static final String CLICK_BUTTON = "Click to button";
        public static final String ADS_LOAD_ERROR = "Ads load error";
        public static final String ADS_LOAD_SUCCESS = "Ads load success";


    }

    public static class Event {
        public static final String ACTION_IN_APP = "action_in_app";
        public static final String ACTION_PURCHASE = "action_purchase";
        public static final String ACTION_SHOW_ADS = "action_show_ads";
    }

    public static class Screen {
        public static final String SPLASH_ACTIVITY = "SplashAct";
        public static final String UNKNOW_ACTIVITY = "UnknowAct";
        public static final String MAIN_ACTIVITY = "MainAct";
        public static final String PDF_READER_ACTIVITY = "PDFReaderAct";
    }

    private static Context mContext;

    public static FirebaseAnalTool getInstance(Context context) {
        if (mFirebaseAnalTool == null || (mContext != null && !mContext.equals(context))) {
            mContext = context;
            mFirebaseAnalTool = new FirebaseAnalTool(context);
        }
        return mFirebaseAnalTool;
    }

    public FirebaseAnalTool(Context context) {
        if (context != null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
    }


    public void trackEvent(String screen, String eventNames) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, screen + SPACE + eventNames);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, eventNames);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, screen);
        if (mFirebaseAnalytics != null)
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
