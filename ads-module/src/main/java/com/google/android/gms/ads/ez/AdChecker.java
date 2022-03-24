package com.google.android.gms.ads.ez;


import android.content.Context;

import com.google.android.gms.ads.ez.adparam.AdUnit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AdChecker {

    public static final String TAG_NUMBER_SHOW = "number_show";
    public static final String TAG_NUMBER_CLICK = "number_click";
    public static final String TAG_COUNT_SHOW = "count_show";
    public static final String TAG_LAST_SHOW = "last_show";
    public static final String TAG_FIRST_RUNNING = "first_running";
//xx

    private Context mContext;
    private int breakPos = 0;

    public AdChecker(Context context) {
        mContext = context;
    }

    public int getBreakPos() {
        return breakPos;
    }

    public boolean checkShowAds() {

        if (IAPUtils.getInstance().isPremium( )) {
            LogUtils.logString(AdChecker.class, "Da mua khong show ads");
            breakPos = 1;
            return false;
        }
        if (checkLimitShowPerDay()) {
            LogUtils.logString(AdChecker.class, "Today limit ads");
            // neu da limit k cho show ad nua return false
            breakPos = 2;
            return false;
        }


        if (checkLastShow()) {
            LogUtils.logString(AdChecker.class, "Last show time not enough");
            breakPos = 3;
            return false;
        }


        if (checkCountShow()) {
            LogUtils.logString(AdChecker.class, "Count show time enough");
            breakPos = 5;
            return false;
        }

        if (EzApplication.getInstance().isSkipNextAds()) {
            EzApplication.getInstance().setSkipNextAds(false);
            LogUtils.logString(AdChecker.class, "Skip next ads");
            breakPos = 6;
            return false;
        }
        // neu qua het cac case khac thi return true -> cho phep show ads
        return true;
    }


    private boolean checkCountShow() {
        // count show phai luon de la 1
        int count = AdFactoryPreferencesUtils.getTagInt(mContext, TAG_COUNT_SHOW);
        if (count % AdUnit.getCountShowAds() == 0) {
            count++;
            AdFactoryPreferencesUtils.setTagInt(mContext, TAG_COUNT_SHOW, count);
            return false;
        } else {
            count++;
            AdFactoryPreferencesUtils.setTagInt(mContext, TAG_COUNT_SHOW, count);
            return true;
        }
    }


    public void setShowAds() {
        LogUtils.logString(AdChecker.class, "Set Show Ad");
        int count = AdFactoryPreferencesUtils.getTagInt(mContext, TAG_NUMBER_SHOW);
        count++;
        AdFactoryPreferencesUtils.setTagInt(mContext, TAG_NUMBER_SHOW, count);

        AdFactoryPreferencesUtils.setTagLong(mContext, TAG_LAST_SHOW, System.currentTimeMillis());


    }


    private boolean checkLastShow() { // ham nay tra ve true thi k show ad
        long time = AdFactoryPreferencesUtils.getTagLong(mContext, TAG_LAST_SHOW);
        if (System.currentTimeMillis() - time <= AdUnit.getTimeLastShowAds() * 1000) {
            // chua du time return true -> k show ad
            LogUtils.logString(AdChecker.class, "Last show time enough " + (System.currentTimeMillis() - time) / 1000 + "  " + AdUnit.getTimeLastShowAds());
            return true;
        } else {
            return false;
        }
    }


    private boolean checkLimitShowPerDay() {
        int count = AdFactoryPreferencesUtils.getTagInt(mContext, TAG_NUMBER_SHOW);
        if (count <= AdUnit.getLimitShowAds()) {
            // neu show duoi 10 lan thi chua limit return false
            return false;
        } else {
            if (checkNewDay()) {
                // neu la ngay moi set lai so lan show ve o va return ve false chua limit
                AdFactoryPreferencesUtils.setTagInt(mContext, TAG_NUMBER_SHOW, 0);
                return false;
            } else {
                LogUtils.logString(AdChecker.class, "Limit ad per day: Current Show Times " + count + " Limit " + AdUnit.getLimitShowAds());
                // da limit k show nua
                return true;
            }
        }
    }

    private boolean checkNewDay() {
        long time = AdFactoryPreferencesUtils.getTagLong(mContext, TAG_LAST_SHOW);
        if (time < System.currentTimeMillis() + getCountDownYesterday()) {
            // so sanh lan show ad cuoi cung voi thoi gian 0h ngay hom nay
            return true;
        } else {
            return false;
        }

    }

    private long getCountDownYesterday() {
        // thoi gian cua ngay hom nay vi du 0h = 0 , la mot so am
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getDefault());
        Date date1 = null;
        Date date2 = null;
        long difference = 0;
        try {
            Date currentDate = new Date();
            String time1 = format.format(currentDate);
            date1 = format.parse(time1);
            date2 = format.parse(time1.substring(0, 10) + " 00:00:00");
            difference = date2.getTime() - date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return difference;
    }
}
