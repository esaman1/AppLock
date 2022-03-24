package com.google.android.gms.ads.ez.admob;

import android.content.Context;

import com.google.android.gms.ads.ez.LogUtils;
import com.google.android.gms.ads.ez.adparam.AdUnit;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.ez.utils.StateOption;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;

public class AdmobNativeAdUtils {
    private NativeAd nativeAd;
    private Context mContext;
    private static AdmobNativeAdUtils INSTANCE;
    private StateOption stateOption;

    public static AdmobNativeAdUtils getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AdmobNativeAdUtils(context);
        }
        return INSTANCE;
    }

    public AdmobNativeAdUtils(Context context) {
        stateOption = new StateOption();
        mContext = context;
        INSTANCE = this;
        loadAd();
    }

    public NativeAd getNativeAd() {
        NativeAd nati = nativeAd;
        if (stateOption.isLoaded()) {
            stateOption.setShowAd();
            loadAd();
            return nati;
        }
        loadAd();
        return null;
    }


    private void loadAd() {
        LogUtils.logString(AdmobNativeAdUtils.class, "loadAd " );
        nativeAd = null;
        if (stateOption.isLoading() || stateOption.isLoaded()) {
            return;
        }

        AdLoader.Builder builder = new AdLoader.Builder(mContext, AdUnit.getAdmobNativeId())
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nati) {
                        LogUtils.logString(AdmobNativeAdUtils.class, "onNativeAdLoaded" );
                        nativeAd = nati;
                    }
                });


        builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build());
        builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                LogUtils.logString(AdmobNativeAdUtils.class, "onAdFailedToLoad " + adError);
                stateOption.setOnFailed();
            }

            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                stateOption.setOnLoaded();
            }
        }).build().loadAd(new AdRequest.Builder().build());

        stateOption.setOnLoading();
    }
}
