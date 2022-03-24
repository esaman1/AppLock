package com.google.android.gms.ads.ez;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.ez.adparam.AdUnit;
import com.google.android.gms.ads.ez.analytics.FlurryAnalytics;
import com.google.android.gms.ads.ez.utils.StateOption;

import java.util.ArrayList;
import java.util.List;

public class EzAdControl {

    private static EzAdControl INSTANCE;
    private StateOption stateOption = StateOption.getInstance();

    public static EzAdControl getInstance(Activity context) {
        if (INSTANCE == null) {
            INSTANCE = new EzAdControl(context);
        } else {
            INSTANCE.mContext = context;
        }
        return INSTANCE;
    }


    private Activity mContext;
    private AdFactoryListener adListener;
    private AdChecker adChecker;
    private List<String> listAds;
    private boolean isApplovinInit = false;

    public EzAdControl(Activity context) {
        mContext = context;
        adChecker = new AdChecker(mContext);
    }

    public static void initAd(Activity context) {
        LogUtils.logString(EzAdControl.class, "Init Ad");

        getInstance(context);


        AdmobUtils.getInstance(context).init();
        AudienceNetworkAds.initialize(context);
    }

    public static void initFlurry(Context context, String flurryId) {
        FlurryAnalytics.init(context, flurryId);
    }

    public EzAdControl setAdListener(AdFactoryListener adListener) {
        LogUtils.logString(this.getClass(), "setAdListener");
        this.adListener = adListener;
        if (stateOption.isLoaded() && !stateOption.isLoading() && !stateOption.isShowed()) {
            // khi o man GetStart neu da loaded roi thi khi sang splash set listener kiem tra xem co loaded chua roi thi ban ve onloaded luon
            new CountDownTimer(1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setOnAdLoaded();
                }
            }.start();

        }

        if (!stateOption.isLoaded() && !stateOption.isLoading() && !stateOption.isShowed()) {
            // khi o man GetStart neu da loaded roi thi khi sang splash set listener kiem tra xem co loaded chua roi thi ban ve onloaded luon

            new CountDownTimer(1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setOnAdError();
                }
            }.start();
        }

        return this;
    }

    public void loadAd() {

        loadAdx();
        loadFacebook();
        loadAdmob();
        stateOption.setOnLoading();

        new CountDownTimer(5000, 5000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                if (!stateOption.isShowed() && !stateOption.isLoaded()) {
                    LogUtils.logString(EzAdControl.class, "Ez Inter Request Ad Time Out");
                    if (adListener != null) {
                        adListener.onError();
                    }
                    adListener = null;
                }
            }
        }.start();
    }


    private void loadFacebook() {
        LogUtils.logString(EzAdControl.class, "Load Facebook");
        FacebookUtils.getInstance(mContext)
                .setListener(new AdFactoryListener() {
                    @Override
                    public void onError() {
                        LogUtils.logString(EzAdControl.class, "Facebook onError");
                        setOnAdError();
                    }

                    @Override
                    public void onLoaded() {
                        setOnAdLoaded();
                    }

                    @Override
                    public void onDisplay() {
                        super.onDisplay();
                        setOnAdDisplayed();
                    }

                    @Override
                    public void onClosed() {
                        super.onClosed();
                        setOnAdClosed();
                    }
                })
                .loadAds();
    }



    private void loadAdmob() {
        LogUtils.logString(EzAdControl.class, "Load Admob");
        AdmobUtils.getInstance(mContext)
                .setListener(new AdFactoryListener() {
                    @Override
                    public void onError() {
                        setOnAdError();
                    }

                    @Override
                    public void onLoaded() {
                        LogUtils.logString(EzAdControl.class, "Admob Loaded");
                        setOnAdLoaded();
                    }

                    @Override
                    public void onDisplay() {
                        super.onDisplay();
                        setOnAdDisplayed();
                    }

                    @Override
                    public void onClosed() {
                        super.onClosed();
                        setOnAdClosed();
                    }
                })
                .loadAds();
    }

    private void loadAdx() {
        LogUtils.logString(EzAdControl.class, "Load Adx");
        AdxUtils.getInstance(mContext)
                .setListener(new AdFactoryListener() {
                    @Override
                    public void onError() {
                        setOnAdError();
                    }

                    @Override
                    public void onLoaded() {
                        LogUtils.logString(EzAdControl.class, "Adx Loaded");
                        setOnAdLoaded();
                    }

                    @Override
                    public void onDisplay() {
                        super.onDisplay();
                        setOnAdDisplayed();
                    }

                    @Override
                    public void onClosed() {
                        super.onClosed();
                        setOnAdClosed();
                    }
                })
                .loadAds();
    }


    public void showAds() {

        Message message = mHandler1.obtainMessage();
        message.arg1 = 1;
        message.sendToTarget();
    }

    private boolean isloading() {
        return FacebookUtils.getInstance(mContext).isLoading() || AdmobUtils.getInstance(mContext).isLoading()  ;
    }

    final Handler mHandler1 = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            showAdss();
        }
    };

    private boolean showAdss() {

        LogUtils.logString(EzAdControl.class, "Call Show Ad");
        if (!adChecker.checkShowAds()) {
            LogUtils.logString(EzAdControl.class, "Ad Checker false");
            setOnAdDisplayFaild();
            return false;
        }


        listAds = new ArrayList<>();

        String[] array = AdUnit.getMasterAdsNetwork().split(",", -1);
        LogUtils.logString(EzAdControl.class, AdUnit.getMasterAdsNetwork());
        for (String s : array) {
            LogUtils.logString(EzAdControl.class, s);
            listAds.add(s);
        }

        showAds2();
//        loadAd();
        return false;

    }

    private boolean showAds2() {
        // neu het phan tu thi return ve 0, luc nay da load qua 4 mang nhung k show dc
        if (listAds == null || listAds.size() == 0) {
            setOnAdDisplayFaild();
            return false;
        }
        String network = listAds.get(0);

        listAds.remove(0);

        switch (network) {
            case "admob":
                if (AdmobUtils.getInstance(mContext).showAds()) {
                    LogUtils.logString(EzAdControl.class, "Show Admob Success");
                    adChecker.setShowAds();
                    return true;
                }
                if (showAds2()) {
                    return true;
                }
                break;
            case "facebook":
                if (FacebookUtils.getInstance(mContext).showAds()) {
                    LogUtils.logString(EzAdControl.class, "Show Facebook Success");
                    adChecker.setShowAds();
                    return true;
                }
                if (showAds2()) {
                    return true;
                }
                break;
            case "adx":

                if (AdxUtils.getInstance(mContext).showAds()) {
                    LogUtils.logString(EzAdControl.class, "Show Adx Success");
                    adChecker.setShowAds();
                    return true;
                }
                if (showAds2()) {
                    return true;
                }
                break;
        }

        setOnAdDisplayFaild();


        return false;
    }

    public static Context getContext() {
        if (INSTANCE != null) {
            return INSTANCE.mContext;
        }
        return null;
    }


    private void setOnAdLoaded() {
        if (!isloading()) {
            if (adListener != null) {
                adListener.onLoaded();
//                adListener = null; // tat tam k huy listenner
                // them doan nay de huy listenner
            }
            stateOption.setOnLoaded();
        }
    }

    private void setOnAdError() {
        if (!isloading()) {
            if (adListener != null) {
                adListener.onError();
                adListener = null;
            }
            stateOption.setOnFailed();
        }
    }

    private void setOnAdDisplayed() {
        if (adListener != null) {
            adListener.onDisplay();
        }
        stateOption.setShowAd();
    }

    private void setOnAdClosed() {
        if (adListener != null) {
            adListener.onClosed();
            adListener = null;
        }
        stateOption.setDismisAd();
    }

    private void setOnAdDisplayFaild() {
        if (adListener != null) {
            adListener.onDisplayFaild();
            adListener = null;
        }
    }
}
