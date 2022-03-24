package com.google.android.gms.ads.ez.utils;

import android.util.Log;

public class StateOption {
    private boolean isLoading = false;
    private boolean isLoaded = false;
    private boolean isShowed = false;

    public StateOption() {
        isLoading = false;
        isLoaded = false;
    }

    public static StateOption getInstance() {
        return new StateOption();
    }

    public boolean isLoading() {
        return isLoading;
    }


    public boolean isLoaded() {
        return isLoaded;
    }


    public void setOnLoading() {
        isLoading = true;
        isLoaded = false;
    }

    public void setOnLoaded() {
        isLoaded = true;
        isLoading = false;
    }

    public void setOnFailed() {
        isLoaded = false;
        isLoading = false;
    }

    public void setShowAd() {
        isLoaded = false;
        isLoading = false;
        isShowed = true;
    }
    public void setDismisAd() {
        isShowed = false;
    }
    public boolean isShowed() {
        return isShowed;
    }


    public boolean isAcceptedLoadAd() {
        Log.e("AdmobOpenAdUtils", "isAcceptedLoadAd: " + isLoaded + "  " + isLoading);
        if (isLoading || isLoaded) {

            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StateOption{" +
                "isLoading=" + isLoading +
                ", isLoaded=" + isLoaded +
                ", isShowed=" + isShowed +
                '}';
    }
}
