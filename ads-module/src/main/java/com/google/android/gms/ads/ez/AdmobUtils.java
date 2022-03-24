package com.google.android.gms.ads.ez;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.ez.adparam.AdUnit;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdmobUtils extends AdsFactory {
    public static AdmobUtils INSTANCE;
    private final String TAG = "AdmobUtils";

    public static AdmobUtils getInstance(Activity context) {
        if (INSTANCE == null) {
            INSTANCE = new AdmobUtils(context);
        }
        INSTANCE.mContext = context;
        return INSTANCE;
    }

    public void init() {
        MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
    }

    private InterstitialAd admobInterstitialAd;

    public AdmobUtils(Activity mContext) {
        super(mContext);
    }

    @Override
    public void loadAds() {
        LogUtils.logString(this, "Load Admob");
//        if (!com.google.android.gms.ads.ez.Utils.CheckInstallerId(mContext)) {
//            if (mListener != null) {
//                mListener.onError();
//            }
//            return;
//        }
        String id = AdUnit.getAdmobInterId();
        LogUtils.logString(this, "Load Admob With Id " + id);
        if (id.equals("")) {
            if (mListener != null) {
                mListener.onError();
            }
            return;
        }


        LogUtils.logString(this, "Check Load " + isLoaded + "  " + isLoading);

        if (isLoading) {
            // neu dang loading  thi k load nua
        } else if (isLoaded) {
            // neu da loaded thi goi callback luon
            if (mListener != null) {
                mListener.onLoaded();
            }
        } else {
            // neu k loading cung k loaded thi goi ham load ads va dat loading = true
            LogUtils.logString(AdmobUtils.class, "Load Admob: Start Loading ");


            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(mContext, id, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    admobInterstitialAd = interstitialAd;
                    LogUtils.logString(AdmobUtils.class, "Admob Loaded");
                    isLoading = false;
                    isLoaded = true;
                    setFullScreenContentListenner();
                    if (mListener != null) {
                        mListener.onLoaded();
                    }
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    admobInterstitialAd = null;
                    LogUtils.logString(AdmobUtils.class, "Admob Failed " + loadAdError.getMessage());
                    isLoading = false;
                    isLoaded = false;
                    if (mListener != null) {
                        mListener.onError();
                    }
                }
            });

            isLoading = true;
        }
    }

    private void setFullScreenContentListenner() {
        admobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                LogUtils.logString(AdmobUtils.class, "Admob Closed");
                if (mListener != null) {
                    mListener.onClosed();
                }
                LogUtils.logString(AdmobUtils.class, "Call Reload EzAd");
                EzAdControl.getInstance(mContext).loadAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                LogUtils.logString(AdmobUtils.class, "Admob Display Fail");
                if (mListener != null) {
                    mListener.onDisplayFaild();
                }
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                admobInterstitialAd = null;
                LogUtils.logString(AdmobUtils.class, "Admob Impression");
                if (mListener != null) {
                    mListener.onDisplay();
                }
            }
        });
    }

    @Override
    public boolean showAds() {
        if (admobInterstitialAd != null && mContext instanceof Activity && mContext != null) {
            admobInterstitialAd.show((Activity) mContext);
            isLoaded = false;
            return true;
        }
        return false;
    }


}
