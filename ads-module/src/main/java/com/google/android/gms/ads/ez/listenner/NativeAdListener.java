package com.google.android.gms.ads.ez.listenner;

import android.widget.RelativeLayout;

import com.google.android.gms.ads.ez.nativead.AdmobNativeAdView;

public abstract class NativeAdListener {
    public abstract void onError();

    public abstract void onLoaded(RelativeLayout nativeAd);

    public void onClickAd() {

    }

    public void onPurchased(RelativeLayout nativeAd) {

    }

}
