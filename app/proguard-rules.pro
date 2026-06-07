-keep class com.hotspotguard.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
