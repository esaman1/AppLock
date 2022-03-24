# Analytics Log


## 1. Flurry Analytics
- Flurry có thể add hoặc không, nếu cần add a sẽ nói thêm và gửi ID
- Thêm vào SplashActivity
#### Init
```java
EzAdControl.initFlurry(this, "Flurry ID");
```
#### Log Event
- Hàm `trackEvent` vào 2 tham số `ScreenName` (fragment, activity, dialog, view), `Action`
- Hạn chế tạo nhiều Action và Screen có thể tạo 1 file định nghĩa. Tạo nhiều loạn log k đọc được đâu
```java
FlurryAnalytics.logEvent("ScreenName", "Action");
```
## 2. Firebase Analytics

#### Log Event
- Hàm `trackEvent` vào 2 tham số `ScreenName` (fragment, activity, dialog, view), `Action`
- Hạn chế tạo nhiều Action và Screen có thể tạo 1 file định nghĩa. Tạo nhiều loạn log k đọc được đâu
```java
FirebaseAnalTool.getInstance(Context).trackEvent("ScreenName", "Action");
```

## 3. Logcat
- Truyền vào Object đang code hoặc không
- Filter `EzAd`
```java
LogUtils.logString(FlurryAnalytics.class, "Message");
LogUtils.logString(this, "Message");
LogUtils.logString("Message");
```
