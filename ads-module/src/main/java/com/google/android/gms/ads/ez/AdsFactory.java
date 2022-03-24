package com.google.android.gms.ads.ez;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.ez.utils.StateOption;


public abstract class AdsFactory {
    protected Activity mContext;
    protected AdFactoryListener mListener;
    protected boolean isLoading = false, isLoaded = false;
    protected StateOption stateOption ;

    public AdsFactory(Activity mContext) {
        this.mContext = mContext;
        stateOption = new StateOption();
    }

    public AdsFactory setListener(AdFactoryListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public abstract void loadAds();

    public abstract boolean showAds();

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isLoaded() {
        return isLoaded;
    }
}
