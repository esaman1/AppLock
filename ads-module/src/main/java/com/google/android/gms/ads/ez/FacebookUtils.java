package com.google.android.gms.ads.ez;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.ez.adparam.AdUnit;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class FacebookUtils extends AdsFactory {
    public static FacebookUtils INSTANCE;
    private static final String TAG = "FacebookUtils";

    public static FacebookUtils getInstance(Activity context) {
        if (INSTANCE == null) {
            INSTANCE = new FacebookUtils(context);
        }
        return INSTANCE;
    }


    private InterstitialAd fbInterstitialAd;

    public FacebookUtils(Activity mContext) {
        super(mContext);
    }


    @Override
    public void loadAds() {
        LogUtils.logString(this, "");
        LogUtils.logString(FacebookUtils.class, "Load Ads");
//        if (!Utils.CheckInstallerId(mContext)) {
//            if (mListener != null) {
//                mListener.onError();
//            }
//            return;
//        }

        String id = AdUnit.getFacebookInterId();
        LogUtils.logString(FacebookUtils.class, "Load Facebook With Id " + id);

        if (!id.equals("")) {
            fbInterstitialAd = new InterstitialAd(mContext, id);
        }

        if (fbInterstitialAd == null) {
            if (mListener != null) {
                mListener.onError();
            }
            return;
        }


        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                LogUtils.logString(FacebookUtils.class, "Facebook Impression");
                if (mListener != null) {
                    mListener.onDisplay();
                }
                isLoaded = false;
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                LogUtils.logString(FacebookUtils.class, "Facebook Closed");
                if (mListener != null) {
                    mListener.onClosed();
                }
                isLoaded = false;
                EzAdControl.getInstance(mContext).loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                LogUtils.logString(FacebookUtils.class, "Facebook Failed " + adError.getErrorMessage());
                isLoading = false;
                isLoaded = false;
                if (mListener != null) {
                    mListener.onError();
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                LogUtils.logString(FacebookUtils.class, "Facebook Loaded");
                isLoading = false;
                isLoaded = true;
                // Show the ad
                if (mListener != null) {
                    mListener.onLoaded();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                LogUtils.logString(FacebookUtils.class, "Facebook Ad Click");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown

        LogUtils.logString(FacebookUtils.class, "Check Load " + isLoaded + "  " + isLoading);
        if (!isLoading && !isLoaded) {
            LogUtils.logString(FacebookUtils.class, "Load Facebook: Start Loading ");
            fbInterstitialAd.loadAd(
                    fbInterstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
            isLoaded = false;
            isLoading = true;
        } else if (isLoaded) {
            if (mListener != null) {
                mListener.onLoaded();
            }
        }
    }


    @Override
    public boolean showAds() {
        if (fbInterstitialAd == null || !fbInterstitialAd.isAdLoaded()) {
            return false;
        }
        if (fbInterstitialAd.isAdInvalidated()) {
            return false;
        }

        isLoaded = false;
        fbInterstitialAd.show();
        return true;
    }


}
