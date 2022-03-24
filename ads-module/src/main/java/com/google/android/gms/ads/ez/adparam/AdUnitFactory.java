package com.google.android.gms.ads.ez.adparam;

public abstract class AdUnitFactory {
    private static AdUnitFactory INSTANCE;

    public static AdUnitFactory getInstance(boolean isTest) {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        if (isTest) {
            return new AdsParamUtilsTest();
        } else {
            return new AdsParamUtils();
        }
    }

    public AdUnitFactory() {
        INSTANCE = this;
    }

    public abstract String getAdmobInterId();

    public abstract String getAdmobNativeId();

    public abstract String getAdmobOpenId();

    public abstract String getAdmobBannerId();

    public abstract String getAdmobRewardedId();

    public abstract String getFacebookInterId();

    public abstract String getFacebookBannerId();

    public abstract String getFacebookNativeId();

    public abstract int getCountShowAds();

    public abstract int getLimitShowAds();

    public abstract long getTimeLastShowAds();


    public abstract String getAdxInterId();

    public abstract String getAdxBannerId();

    public abstract String getAdxNativeId();

    public abstract String getAdxOpenId();


    public abstract String getApplovinBannerId();

    public abstract String getApplovinInterId();

    public abstract String getApplovinRewardId();

    public abstract String getMasterAdsNetwork();
}
