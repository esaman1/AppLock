package com.google.android.gms.ads.ez.adparam;

import com.google.android.gms.ads.ez.remote.AppConfigs;
import com.google.android.gms.ads.ez.remote.RemoteKey;


public class AdsParamUtilsTest extends AdUnitFactory {


    @Override
    public String getAdmobInterId() {
        return "ca-app-pub-3940256099942544/1033173712";
    }

    @Override
    public String getAdmobNativeId() {
        return "ca-app-pub-3940256099942544/2247696110";
    }

    @Override
    public String getAdmobOpenId() {
        return "ca-app-pub-3940256099942544/3419835294";
    }

    @Override
    public String getAdmobBannerId() {
        return "ca-app-pub-3940256099942544/6300978111";
    }

    @Override
    public String getAdmobRewardedId() {
        return "ca-app-pub-3940256099942544/5224354917";
    }


    @Override
    public String getFacebookInterId() {
        return "256135302826650_256136129493234_";
    }

    @Override
    public String getFacebookBannerId() {
        return "270481753652191_602364100463953";
    }

    @Override
    public String getFacebookNativeId() {
        return "270481753652191_612014289498934";
    }


    @Override
    public String getMasterAdsNetwork() {
        return AppConfigs.getString(RemoteKey.MASTER_ADS_NETWORK);
    }

    @Override
    public String getAdxInterId() {
        return "/6499/example/interstitial";
    }

    @Override
    public String getAdxBannerId() {
        return null;
    }

    @Override
    public String getAdxNativeId() {
        return "/6499/example/native";
    }

    @Override
    public String getAdxOpenId() {
        return "/6499/example/app-open";
    }

    @Override
    public String getApplovinBannerId() {
        return "4400a73227b4e77f";
    }

    @Override
    public String getApplovinInterId() {
        return "c3a0cadabefea4e7";
    }

    @Override
    public String getApplovinRewardId() {
        return "8a24e5b04e6fdf66";
    }

    @Override
    public int getCountShowAds() {
        return 1;
    }

    @Override
    public int getLimitShowAds() {
        return 50;
    }

    @Override
    public long getTimeLastShowAds() {
        return 10;
    }


}
