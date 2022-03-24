# Ads Module

## [Analytics Document](https://gitlab.com/ezmedia/ads-module/-/blob/main/AnalyticsLog.md)

 ## 1. Getting started

  Gắn ads thì gửi package name để a tạo file `google-services.json`

  ### Add your files
  - Copy file `google-services.json` vào thư mục **app** của project

  - Thêm đoạn sau vào **Project** `build.gradle` 
    ```java
      buildscript {
        repositories {
            google()  // Google's Maven repository

            // Add this line
            mavenCentral()
            maven { url 'https://artifacts.applovin.com/android' }
          }
        dependencies {
            // Add this line
            classpath "com.google.gms:google-services:4.3.10"
            classpath "com.google.firebase:firebase-crashlytics-gradle:2.7.1"
            classpath "com.applovin.quality:AppLovinQualityServiceGradlePlugin:+"
        }  
      }

      allprojects {
           
        repositories {
             
            google()   
            mavenCentral()
            maven { url "https://android-sdk.is.com" }
            maven { url "https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea" }
            maven { url "https://artifact.bytedance.com/repository/pangle" }

        }
      }
    ```

  - Add plugin vào **App** `build.gradle` 
    ``` java
      apply plugin: 'com.android.application'

      // Add this line
      apply plugin: 'com.google.gms.google-services'
      apply plugin: 'com.google.firebase.crashlytics'

      apply plugin: 'applovin-quality-service'
      applovin {
          apiKey "c6A1iFSitmVlIuhTlnunm8a4yA-CFW6s2KDr_2HZuSVzMhSZbMHS4_1zqTneldiz6Jiipza3UAkTOBukN6uGbe"
      }
    ```





 ## 2. Integration
- Nếu không override **Application** thì sửa `android:name` trong thẻ `<application>`
 
  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <manifest xmlns:android="http://schemas.android.com/apk/res/android"
      package="com.example.myapp">
      <application
              android:name="com.google.android.gms.ads.ez.EzApplication" >
      </application>
  </manifest>
  ```

- Nếu  override **Application** thì sửa kế thừa `com.google.android.gms.ads.ez.EzApplication`
  ```java
  public class MyApplication extends EzApplication { ... }
  ```


#### App Open
- Gọi trong Splash Activity
- Gọi sau khi thực hiện các hành động load dữ liệu, xin quyền
  ```java
  AdmobOpenAdUtils.getInstance(this).setAdListener(new AdFactoryListener() {
            @Override
            public void onError() {
                LogUtils.logString(SplashActivity.class, "onError");
                openMain();
            }

            @Override
            public void onLoaded() {
                LogUtils.logString(SplashActivity.class, "onLoaded");
                // show ads ngay khi loaded
                AdmobOpenAdUtils.getInstance(SplashActivity.this).showAdIfAvailable();
            }

            @Override
            public void onDisplay() {
                super.onDisplay();
                LogUtils.logString(SplashActivity.class, "onDisplay");
            }

            @Override
            public void onDisplayFaild() {
                super.onDisplayFaild();
                LogUtils.logString(SplashActivity.class, "onDisplayFaild");
                openMain();
            }

            @Override
            public void onClosed() {
                super.onClosed();
                // tam thoi bo viec load lai ads thi dismis
                LogUtils.logString(SplashActivity.class, "onClosed");
                openMain();
            }
        }).loadAd();
  ```


#### Interstitial
- Chỉ call khi có action, cứ thêm thoải mái a sẽ chính capping
- Tham số truyền vào là **Activity** (không cast từ context sang)
  ```java
  EzAdControl.getInstance(activity).showAds();
  ```


#### Banner
- Thêm vào xml của tất cả màn hình
  ```xml
  <com.google.android.gms.ads.ez.BannerAd
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
      </com.google.android.gms.ads.ez.BannerAd>
  ```


#### Native
- Tham số truyền vào: `Context` `Layout Id` `Listener`
  ```java
    AdmobNativeAdView.getNativeAd(Context, R.layout.native_admob_item, new AdmobNativeAdView.NativeAdListener() {
              @Override
              public void onError() {
                  // load ad failded
              }

              @Override
              public void onLoaded(RelativeLayout nativeAd) {
                  // load ad success
                  // add nativeAd  
              }
          });
  ```

#### RewardedVideo
  ```java
    ApplovinRewardedUtils.getInstance(MainActivity.this).setAdListener(new AdFactoryListener() {
                    @Override
                    public void onError() {
                        LogUtils.logString(MainActivity.class, "onError");
                    }

                    @Override
                    public void onLoaded() {
                        LogUtils.logString(MainActivity.class, "onLoaded");
                    }

                    @Override
                    public void onRewardVideoStart() {
                        super.onRewardVideoStart();
                        LogUtils.logString(MainActivity.class, "onRewardVideoStart");
                    }

                    @Override
                    public void onEarnedReward() {
                        super.onEarnedReward();
                        LogUtils.logString(MainActivity.class, "onEarnedReward");
                    }
                }).showAds();
  ```
- Thực hiện action khi vào onEarnedReward





 ## 3. Update ads id
  - Làm đến bước này thì liên hệ a để lấy ad id
  - Thay `APPLICATION_ID` trong [AndroidManifest](https://gitlab.com/ezmedia/ads-module/-/blob/main/src/main/AndroidManifest.xml)
    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="com.google.android.gms.ads.ez">

        <uses-permission android:name="android.permission.INTERNET" />
        <application>
            <!-- Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713 -->
            <meta-data
                android:name="com.google.android.gms.ads.APPLICATION_ID"
                android:value="ca-app-pub-5025628276811480~6419296825" />
        </application>
    </manifest>

    ```
  - Thay **Ad Id** [remote_ads.xml](https://gitlab.com/ezmedia/ads-module/-/blob/main/src/main/res/xml/remote_ads.xml)
    `admob_inter_id` `admob_open_id` `admob_banner_id` `admob_native_id` `adx_banner_id` `adx_inter_id` `fb_inter_id` `fb_banner_id`
 




 ## 4. Release
  - Vào [AdUnit](https://gitlab.com/ezmedia/ads-module/-/blob/main/src/main/java/com/google/android/gms/ads/ez/adparam/AdUnit.java) đổi `TEST` thành `false` (_Chỉ thay khi suất apk còn lúc test để về true_)
    ```java
    public static final boolean TEST = true;
    ```
  - Chỉ cần suất apk debug (yêu cầu chi tiết sẽ tùy mỗi app)
