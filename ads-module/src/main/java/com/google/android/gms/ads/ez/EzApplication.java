package com.google.android.gms.ads.ez;


import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.ads.ez.admob.AdmobNativeAdUtils;
import com.google.android.gms.ads.ez.admob.AdmobOpenAdUtils;
import com.google.android.gms.ads.ez.remote.AppConfigs;

public class EzApplication extends MultiDexApplication implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private static EzApplication INSTANCE;

    public static EzApplication getInstance() {
        return INSTANCE;
    }

    private static Activity currentActivity;


    private boolean isSkipNextAds = false;
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;


        registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        AppConfigs.getInstance(this);
        InstallReferrerReceiver.receiverInstall(this);
        IAPUtils.getInstance().init(this);
        AdmobNativeAdUtils.getInstance(this);

    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {


        if (currentActivity == null) {
            currentActivity = activity;
            EzAdControl.initAd(currentActivity);
            EzAdControl.getInstance(currentActivity).loadAd();
            RewardedAdUtils.getInstance(currentActivity);
        }
        currentActivity = activity;
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        LogUtils.logString(EzApplication.class, "ON_START");
//        if (!Utils.checkTopActivityIsAd(currentActivity)) {
//            AdmobOpenAdUtils.getInstance(currentActivity).showAdIfAvailable(true);
//        }

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        LogUtils.logString(EzApplication.class, "onActivityStarted");

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        LogUtils.logString(EzApplication.class, "onActivityResumed");
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    public boolean isSkipNextAds() {
        return isSkipNextAds;
    }

    public void setSkipNextAds(boolean skipNextAds) {
        isSkipNextAds = skipNextAds;
    }
}
