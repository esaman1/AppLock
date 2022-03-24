package com.google.android.gms.ads.ez;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.ez.adparam.AdUnit;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;


public class AdxUtils extends AdsFactory {
    public static AdxUtils INSTANCE;
    private final String TAG = "AdxUtils";

    public static AdxUtils getInstance(Activity context) {
        if (INSTANCE == null) {
            INSTANCE = new AdxUtils(context);
        }
        INSTANCE.mContext = context;
        return INSTANCE;
    }


    private AdManagerInterstitialAd mInterstitialAd;

    public AdxUtils(Activity mContext) {
        super(mContext);
    }

    @Override
    public void loadAds() {
        LogUtils.logString(this, "Load AdxUtils");
        String id = AdUnit.getAdxInterId();
        LogUtils.logString(this, "Load AdxUtils With Id " + id);


        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        AdManagerInterstitialAdLoadCallback adLoadCallback = new AdManagerInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                LogUtils.logString(AdxUtils.class, "AdxUtils Loaded");

                mInterstitialAd = interstitialAd;
                interstitialAd.setFullScreenContentCallback(
                        new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                mInterstitialAd = null;
                                LogUtils.logString(AdxUtils.class, "AdxUtils Closed");
                                if (mListener != null) {
                                    mListener.onClosed();
                                }
                                LogUtils.logString(AdxUtils.class, "Call Reload EzAd");
                                EzAdControl.getInstance(mContext).loadAd();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                LogUtils.logString(AdxUtils.class, "AdxUtils Impression");
                                if (mListener != null) {
                                    mListener.onDisplay();
                                }
                            }
                        });


                isLoading = false;
                isLoaded = true;
                if (mListener != null) {
                    mListener.onLoaded();
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                LogUtils.logString(AdxUtils.class, "AdxUtils Failed");
                isLoading = false;
                isLoaded = false;
                if (mListener != null) {
                    mListener.onError();
                }

            }
        };


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

            LogUtils.logString(AdxUtils.class, "Load AdxUtils: Start Loading ");
            AdManagerInterstitialAd.load(
                    mContext,
                    AdUnit.getAdxInterId(),
                    adRequest, adLoadCallback
            );
            isLoading = true;
        }


    }

    @Override
    public boolean showAds() {
        LogUtils.logString(AdxUtils.class, "xxx " + isLoaded + "  " + mContext + "  " + (mContext instanceof Activity));
        if (isLoaded && mContext != null) {
            mInterstitialAd.show((Activity) mContext);
            isLoaded = false;
            return true;
        }
        return false;
    }


}
