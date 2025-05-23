# Test Device Specifications

## Device Information
- **Model**: OnePlus 12 (CPH2583)
- **Operating System**: OxygenOS 15.0
- **Android Version**: 15
- **Kernel Version**: 6.1.75-android14-11-o-g4c9c8979e2a7
- **Version Number**: CPH2583_15.0.0.406(EX01)
- **Android Security Patch**: December 5, 2024
- **Root Status**: Rooted
- **Environment**: Kali NetHunter chroot, Magisk + Zygisk, LSPosed installed

## Hardware Specifications
- **Processor**: Snapdragon 8 Gen 3 Mobile Platform
- **RAM**: 16 GB (expandable +12 GB via RAM expansion)
- **Storage**: 512 GB (154 GB used)
- **Battery**: 5,400 mAh
- **Display**: 6.82-inch AMOLED

## Camera System
- **Rear Cameras**:
  - Main: 50 MP
  - Ultra-wide: 48 MP
  - Telephoto: 64 MP
- **Front Camera**: 32 MP

## Software Features
- **Performance**: Trinity Engine performance suite
- **Optimization Suite**: Trinity Engine
- **Security Features**: Full root access, Magisk, LSPosed, Zygisk
- **Pentest Tools**: Kali NetHunter, custom LSPosed modules, rooted exploit environment

## Device Specifics

Below is the master cheat-sheet I keep on my desk when I'm writing LSPosed hooks for a modern OnePlus build.
It groups **every OnePlus- or BBK-specific element that can matter to any future module**— from UI entry-points to deep vendor quirks—so you can search or pattern-match quickly while coding.

| Scope | "Stable" package / file name(s) | Why it matters to LSPosed | Typical on-disk path (readonly slots) | Notes / gotchas |
|-------|--------------------------------|---------------------------|---------------------------------------|-----------------|
| **SystemUI-class processes** | `com.oneplus.systemui` (aka **OPSystemUI**), `com.oplus.systemui.overlay.*`, `com.android.systemui.overlay.*.oneplus`, charging / fingerprint / AOD anim overlays | All status-bar, QS, lock-screen, power-menu hooks live here. Class names are **not** 1-to-1 with AOSP—e.g. `OpClock` subclasses AOSP `Clock`; horizon-light edge effects sit in `OpLightEffectController`. | `/system_ext/priv-app/OPSystemUI/OPSystemUI.apk` plus overlay splits under `/system/product/overlay/` | Hard-code these if your firmware never updates; otherwise keep a fallback to AOSP names. |
| **Launchers & home surfaces** | `com.oneplus.launcher`, `net.oneplus.launcher` (legacy), `com.oplus.launcher`, Widget provider `net.oneplus.widget` | Hooks for icon grid, recents animations, double-tap gestures. | `/product/priv-app/OPLauncher3/OPLauncher3.apk` | On CPH2583 the package exports a `oem.intent.action.SHELF_ENTER` broadcast for Shelf; hijack that if you're modding Shelf. |
| **Shelf / Scout / Smart suggestions** | `com.oneplus.shelf`, `com.heytap.quicksearchbox` (merged search backend), internal provider `com.oplus.appdetail` | Shelf cards rendering & "Scout" universal search; useful for injecting custom cards. | `/product/app/Shelf/Shelf.apk` | Scout search is a ContentProvider, not an Activity—LSPosed has to hook `call()` not `onCreate()`. |
| **Game Space & performance modes** | `com.oneplus.gamespace`, service `com.oplus.games`, perf harness `com.oplus.athena` | Game FPS overlay, mis-tap prevention; hooking lets you spoof game mode. | `/product/app/OPGameSpace/OPGameSpace.apk` | Game overlay lives in a separate process, so you need `handleLoadPackage` for both `gamespace` and `systemui`. |
| **Display / ambient / Horizon Light** | Vendor service `vendor.oneplus.hardware.displayeffect@1.0`, overlay pkg `com.oneplus.aod`, system prop `persist.sys.oplus.omnidisplay.*` | Brightness tints, edge-lighting, DC-dimming. | HIDL service lives under `/vendor/bin/` | You can't touch HIDL with LSPosed, but SystemUI calls its AIDL shim `OneplusDisplayEffectManager`; hook that instead. |
| **Dirac / Dolby audio** | `com.oneplus.sound.tuner`, `com.oneplus.dirac`, `com.oplus.audio.effectcenter` | Global EQ & Dolby Atmos. Hooking lets you override per-app presets. | `/system_ext/app/DiracManager/DiracManager.apk` | Almost every audio path update goes through `android.media.AudioSystem#setParameters()`—hook once. |
| **Zen / Focus / "Brick mode"** | `com.oneplus.brickmode`, `com.oplus.digitalwellbeing`, rules provider `com.oplus.deepthinker` | Device-wide "time-out" feature; good place to inject automation APIs. | `/system_ext/app/BrickMode/BrickMode.apk` | Disabling BrickMode crashes if its companion service `deepthinker` is missing—keep both packages enabled in LSPosed test builds. |
| **Security & privacy tooling** | `com.oneplus.security`, `com.oplus.safecenter`, `com.oplus.securitypermission`, Wallet `com.finshell.wallet`, account sync `com.oneplus.account` | Controls dynamic permission revocation & secure keyboard; often breaks invasive hooks. | `/system_ext/priv-app/OPSafecenter/OPSafecenter.apk` | Hooks that alter clipboard or key events should also whitelist the secure keyboard package. |
| **Power / thermal junk** | `com.oneplus.battery`, vendor service `oneplus.thermalservice`, sysfs `/sys/kernel/oplus_thermal/*` | Aggressive throttling if fps drops; you'll hook `TpdPowerMonitorService` in SystemUI. | Service JAR at `/system_ext/framework/oplus-services.jar` | Kernel path constants are hard-coded → safe to inline if firmware is frozen. |
| **Diagnostics & logging** | `com.oplus.logkit`, `com.oneplus.opbugreportlite`, `com.oem.oemlogkit`, `net.oneplus.commonlogtool` | Floods logcat; LSPosed crashes easier when these spam. | `/vendor/bin/logkitsd` (native) + `/system_ext/app/OPLogKit/...` | To silence spam, hook their `println_native()` wrappers instead of burying the whole tag. |
| **Factory / engineer / test apps** | `com.oneplus.engineermode`, `com.oneplus.factorymode.specialtest`, `com.oneplus.bttestmode`, `com.oplus.engineernetwork` | Hidden Activities with useful toggles (5G bands, high-brightness). | `/product/app/EngineerMode/EngineerMode.apk` | Never remove `com.oplus.exsystemservice`—device hard-bricks on boot |
| **Camera stack** | `com.oneplus.camera`, service `com.oneplus.camera.service`, ColorOS plug-in `com.oplus.camera`, portrait engine `com.oplus.portrait` | Camera HAL settings, filters; LSPosed can swap post-proc libs. | `/system_ext/app/OnePlusCamera/OnePlusCamera.apk` | ColorOS plug-in has its **own** `CameraProvider` binder—hook both. |
| **File manager / gallery / recorder** | `com.oneplus.filemanager`, `com.oneplus.gallery`, `com.oplus.gallery3d`, `com.oplus.screenrecorder`, `com.coloros.soundrecorder` | Custom SAF (Storage Access Framework) providers—great for injecting custom MIME types. | `/product/app/OPFileManager/...` etc. | File-manager SAF ID is `com.oneplus.filemanager.documents`; remember to whitelist in intents. |
| **Weather, Clock, Calculator, Notes** | `com.oneplus.weather`, `com.coloros.weather2`, `com.oneplus.deskclock`, `com.coloros.calculator`, `com.coloros.note` | Each exports widgets that inflate with OnePlus-only resources—hook `onCreateView()` if you want global theming. | `/product/app/OPWeather/...` | Clock faces sit in `com.oneplus.deskclock.widget` (not SystemUI). |
| **Cloud & OTA plumbing** | `com.oplus.ocloud`, `com.oplus.ota`, `com.oplus.romupdate`, `com.oplus.upgradeguide` | OTA checker auto-patches framework classes at boot; shield your hooks with a try/catch that ignores `NullPointerException` after OTAs—even if you never update there are background app hot-fixes. | `/system_ext/priv-app/OPOTA/...` |  |
| **HeyTap / ColorOS cross-share** | `com.heytap.cloud`, `com.heytap.themestore`, `com.heytap.smarthome`, `com.coloros.oshare`, `com.coloros.smartsidebar`, `com.coloros.shortcut` | Shared libraries originally from OPPO phones; occasionally start Services by reflection—hook class-not-found safe. | `/product/app/HeyTapCloud/...` | When these crash, SystemUI catches and shows a "Smart Sidebar stopped" toast—add your own error filter to keep logs readable. |
| **System properties you'll read / write** | `ro.display.series`, `ro.build.version.ota`, `ro.camera.oplus.isp_ver`, `persist.sys.oplus.dc_dim`, `persist.sys.oplus.hbm_mode`, `sys.oneplus.debug`, `persist.oneplus.cpuperf` | Quick way to toggle vendor features from hooks. | `getprop` / `setprop` | Props live in `/vendor/default.prop` or `/product/build.prop`; both are immutable at runtime, so use `setprop` only for debugging. |

### Overlay / Resource Directories

Common on all Android 15 OnePlus builds:

```
/system/product/overlay/com.oneplus.*.apk      # static RRO overlays
/system/system_ext/overlay/                    # targetSdk-specific overlays
/my_product/overlay/                           # dynamic (vendor) overlays
```

### Vendor-Specific Binder & HIDL Service Names

```
oneplus.display.feature@1.0::IOneplusDisplayFeature
oneplus.light.service::IOneplusLightService      # Horizon-Light
oneplus.thermal.control::IOneplusThermalService  # CPU / GPU governor
oplus.hardware.imageeffect@2.0                   # Portrait, Beauty
```

### Idiosyncratic File / Sysfs Paths

```
/sys/kernel/oplus_display/seed_mode
/sys/kernel/oplus_display/dimlayer_bl_en
/sys/devices/platform/soc/*.qcom,kgsl-3d0/oplus_peak_pwr
/data/adb/modules/yourmodule/system/system_ext/priv-app/OPSystemUI/  # Magisk overlay path
```

## Why This List Is "Enough"

* It covers every OnePlus- or OPlus/ColorOS-tagged app or service that ships **in the stock CPH2583_15.0.0.406 image** (see full package dump for ACE2, which is the same BBK tree and the OnePlus-specific subset).
* Any future LSPosed module—whether it skins the quick-settings, rewrites the battery governor or hijacks Game Space—will load into exactly one of the packages above or touch one of the listed system properties / vendor services.
* Everything outside this table is either plain AOSP or Qualcomm-generic and can be treated the same way you would on a Pixel.

Keep this index handy: when you write a new feature, just search for the package/service here, grab the exact class or property inside the APK with jadx or `grep -R`, and hook away.
