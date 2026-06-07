# 🛡️ HotspotGuard — Hotspot Auto-Restart App

## App kya karta hai?
- Jaise hi aap **Hotspot band** karte ho — **5 minute mein automatic on ho jaata hai**
- Sath mein **Mobile Data bhi on** ho jaata hai
- **Har baar** hota hai — continuous loop
- Phone restart ke baad bhi kaam karta hai (auto-start)

---

## Android Studio mein kaise build karein?

### Step 1: Project open karo
1. Android Studio open karo
2. **File → Open** karo
3. `HotspotGuard` folder select karo
4. Gradle sync ka wait karo

### Step 2: APK build karo
1. Menu mein jao: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Wait karo build complete hone ka
3. "APK(s) generated successfully" message aayega
4. **"locate"** click karo — `app/build/outputs/apk/debug/app-debug.apk` milega

### Step 3: Phone pe install karo
1. APK file phone mein transfer karo (USB ya Bluetooth)
2. Phone pe "Unknown sources" allow karo (Settings → Security)
3. APK install karo

---

## App kaise use karein?

1. App open karo
2. **"Guard Shuru Karo"** button dabao
3. Ab background mein monitor ho raha hai
4. Notification bar mein status dikhega

---

## Important Notes

### Android Version ke hisaab se:
| Android Version | Hotspot Control |
|---|---|
| Android 7 aur neeche | ✅ Full control |
| Android 8-11 | ⚠️ Partial (Local hotspot) |
| Android 12+ | ⚠️ Settings redirect |

> **Note:** Android 8+ pe Google ne restrictions laga diye hain. Poora hotspot control ke liye device ka **root** hona helpful hota hai, ya manufacturer-specific workarounds kaam kar sakte hain.

---

## Files ki list

```
app/src/main/java/com/hotspotguard/
├── MainActivity.kt         — Main screen
├── HotspotMonitorService.kt — Background service (5-min timer)
├── HotspotStateReceiver.kt  — Hotspot on/off detect karta hai
├── BootReceiver.kt          — Phone restart pe auto-start
├── HotspotUtils.kt          — Hotspot enable/disable logic
├── MobileDataUtils.kt       — Mobile data enable logic
└── AppPreferences.kt        — Settings save karta hai
```
