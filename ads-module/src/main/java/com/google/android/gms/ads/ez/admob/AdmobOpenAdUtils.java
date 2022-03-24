package com.google.android.gms.ads.ez.admob;


import android.app.Activity;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.ez.AdChecker;
import com.google.android.gms.ads.ez.AdFactoryListener;
import com.google.android.gms.ads.ez.EzAdControl;
import com.google.android.gms.ads.ez.LogUtils;
import com.google.android.gms.ads.ez.adparam.AdUnit;
import com.google.android.gms.ads.ez.utils.StateOption;

import java.util.Date;

public class AdmobOpenAdUtils {
    private static AdmobOpenAdUtils INSTANCE;

    public static AdmobOpenAdUtils getInstance(Activity activity) {
        if (INSTANCE == null) {
            INSTANCE = new AdmobOpenAdUtils(activity);
            return INSTANCE;
        }
        INSTANCE.mActivity = activity;
        return INSTANCE;
    }

    private static final String LOG_TAG = "AppOpenManager";
    private AppOpenAd appOpenAd = null;
    private Activity mActivity;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private long loadTime = 0;
    private AdFactoryListener adListener;
    private AdChecker adChecker;

    private StateOption stateOption;

    /**
     * Constructor
     */
    public AdmobOpenAdUtils(Activity activity) {
        LogUtils.logString(this, "Contructor");
        stateOption = new StateOption();
        mActivity = activity;
        adChecker = new AdChecker(activity);

    }

    public AdmobOpenAdUtils setAdListener(AdFactoryListener adListener) {
        this.adListener = adListener;
        return this;
    }


    /**
     * Request an ad
     */
    public void loadAd() {
        LogUtils.logString(this, "Load Ad Call");
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            if (adListener != null) {
                adListener.onLoaded();
            }
            return;
        }


        LogUtils.logString(this, "Load Ad Accept ");
        new CountDownTimer(5000, 5000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (stateOption.isLoading()) {
                    LogUtils.logString(AdmobOpenAdUtils.class, "Request Timeout");
                    stateOption.setOnFailed();
                    if (adListener != null) {
                        adListener.onError();
                        adListener = null;
                    }
                }

            }
        }.start();


        loadCallback =
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);
                        LogUtils.logString(AdmobOpenAdUtils.class, "Ad Loaded ");
                        AdmobOpenAdUtils.this.appOpenAd = appOpenAd;
                        AdmobOpenAdUtils.this.loadTime = (new Date()).getTime();
                        stateOption.setOnLoaded();
                        if (adListener != null) {
                            adListener.onLoaded();
                        }

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);

                        LogUtils.logString(AdmobOpenAdUtils.class, "Ad Failed");


                        loadAdx();

                    }
                };


        if (!stateOption.isLoading()) {
            AdRequest request = getAdRequest();
            LogUtils.logString(AdmobOpenAdUtils.class, "Show app open ad with id: " + AdUnit.getAdmobOpenId());
            AppOpenAd.load(
                    mActivity, AdUnit.getAdmobOpenId(), request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
            stateOption.setOnLoading();
        }
    }

    public void loadAdx() {
        LogUtils.logString(this, "Load Adx Call");


        AppOpenAd.AppOpenAdLoadCallback loadCallback2 =
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);
                        LogUtils.logString(AdmobOpenAdUtils.class, "Adx Loaded ");
                        AdmobOpenAdUtils.this.appOpenAd = appOpenAd;
                        AdmobOpenAdUtils.this.loadTime = (new Date()).getTime();
                        stateOption.setOnLoaded();
                        if (adListener != null) {
                            adListener.onLoaded();
                        }

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);

                        LogUtils.logString(AdmobOpenAdUtils.class, "Adx Failed");


                        setOnError();

                    }
                };


        AdRequest request = getAdRequest();
        LogUtils.logString(AdmobOpenAdUtils.class, "Show app open adx with id: " + AdUnit.getAdxOpenId());
        AppOpenAd.load(
                mActivity, AdUnit.getAdxOpenId(), request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback2);
    }

    public void showAdIfAvailable(boolean iscapping) {
        LogUtils.logString(AdmobOpenAdUtils.class, "Show  Ad ");
        if (isAdAvailable()) {

            LogUtils.logString(AdmobOpenAdUtils.class, "Show Ad Accept");
            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            LogUtils.logString(AdmobOpenAdUtils.class, "Ad Dismiss");
                            stateOption.setDismisAd();
                            // Set the reference to null so isAdAvailable() returns false.
                            AdmobOpenAdUtils.this.appOpenAd = null;


//                            loadAd();
                            // tam thoi bo viec load lai ads thi dismis


                            if (adListener != null) {
                                adListener.onClosed();

                            }
                            adListener = null;
                            if (iscapping) {
                                adChecker.setShowAds();
                            }
                            // set bang null de k ban ve dc nua

                            loadAd();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            LogUtils.logString(AdmobOpenAdUtils.class, "Ad Show Fail " + adError);
                            if (adListener != null) {
                                adListener.onDisplayFaild();
                            }
                            adListener = null;
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            LogUtils.logString(AdmobOpenAdUtils.class, "Show Ad Success " + adListener);
                            stateOption.setShowAd();

                            if (adListener != null) {
                                adListener.onDisplay();
                            }
                        }
                    };

            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(mActivity);

        } else {
            LogUtils.logString(AdmobOpenAdUtils.class, "Can't show ad");
            if (stateOption.isLoaded()) {
                if (adListener != null) {
                    adListener.onDisplayFaild();
                }
            }
        }
    }

    public StateOption getStateOption() {
        return stateOption;
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
//        LogUtils.logString(AdmobOpenAdUtils.class, "isAdAvailable \n appOpenAd !=null " + (appOpenAd != null)
//                + "\n wasLoadTimeLessThanNHoursAgo " + (wasLoadTimeLessThanNHoursAgo(4)) +
//                "\n adChecker   " + adChecker.checkShowAds() +
//                "\n stateOption.isLoading()   " + stateOption.isLoading() +
//                "\n stateOption.isLoaded()   " + stateOption.isLoaded());
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4) && adChecker.checkShowAds() && stateOption.isLoaded();

//        return false;
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }


    private void setOnError() {
        stateOption.setOnFailed();

        // o day deu ban ra on errror vi neu ban ra on loaded thi se lai call show open ad.
        // ban ra error de chuyen activity con vao activity trong ms show
        EzAdControl.getInstance(mActivity).setAdListener(new AdFactoryListener() {
            @Override
            public void onError() {
                LogUtils.logString(AdmobOpenAdUtils.class, "Inter onError");
                if (adListener != null) {
                    adListener.onError();
                    adListener = null;
                }
            }

            @Override
            public void onLoaded() {
                LogUtils.logString(AdmobOpenAdUtils.class, "Inter onLoaded");
                if (adListener != null) {
                    adListener.onError();
                    adListener = null;
                }
            }
        });

    }

}
