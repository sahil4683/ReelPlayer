# 🎬 ReelPlayer

A native Android app that scans your local storage and plays videos in a smooth vertical scroll experience — just like Instagram Reels or YouTube Shorts.

📱 Download Latest Artifact APK : https://github.com/sahil4683/ReelPlayer/actions/runs/24381892620/artifacts/6420627065
---

## ✨ Features

| Feature | Description |
|---|---|
| 📋 Video List | Browse all local videos with thumbnails before playing |
| 🎞️ Reel Scroll | Full-screen vertical swipe to browse videos |
| ▶️ Auto Play | Videos play automatically as you scroll |
| ⏭️ Auto Next | Automatically advances to the next video when one ends |
| 🔀 Sort By | Sort videos by newest, oldest, longest, or shortest |
| ⚡ 2x Speed | Long press anywhere on video to play at 2x speed |
| ⏸️ Tap to Pause | Tap video to toggle play / pause |
| 🔋 Background Safe | Pauses when app goes to background, resumes on return |

---

## 📱 Screenshots

```
Splash Screen        Video List           Reel Player
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│             │     │ 🎬 My Videos│     │             │
│  🎬         │     │ Sort by: ▼  │     │   [VIDEO]   │
│ ReelPlayer  │     │─────────────│     │   PLAYING   │
│             │     │ [▶] Title 1 │     │             │
│ Your local  │     │ [▶] Title 2 │     │             │
│ videos.     │     │ [▶] Title 3 │     │  Title.mp4  │
│ Reel style. │     │ [▶] Title 4 │     │  /DCIM      │
│             │     │ [▶] Title 5 │     │         ⏸  │
│ [Allow ✓]  │     │ [▶] Title 6 │     │             │
└─────────────┘     └─────────────┘     └─────────────┘
```

---

## 🏗️ Tech Stack

- **Language:** Kotlin
- **Video Player:** ExoPlayer (Media3)
- **Scroll UI:** ViewPager2 (vertical orientation)
- **Image Loading:** Glide
- **Architecture:** MVVM (ViewModel + LiveData)
- **Storage Scanning:** MediaStore API
- **Build:** Gradle 8.1.1

---

## 📂 Project Structure

```
ReelPlayer/
├── .github/
│   └── workflows/
│       └── build.yml               # GitHub Actions CI/CD
├── app/src/main/
│   ├── java/com/reelplayer/
│   │   ├── SplashActivity.kt       # Permission request screen
│   │   ├── VideoListActivity.kt    # Video browser with sort
│   │   ├── MainActivity.kt         # Reel player screen
│   │   ├── adapter/
│   │   │   ├── ReelAdapter.kt      # ViewPager2 reel adapter
│   │   │   └── VideoListAdapter.kt # RecyclerView list adapter
│   │   ├── model/
│   │   │   └── VideoItem.kt        # Video data model
│   │   ├── util/
│   │   │   ├── VideoScanner.kt     # MediaStore video scanner
│   │   │   └── PermissionHelper.kt # Runtime permission helper
│   │   └── viewmodel/
│   │       └── VideoViewModel.kt   # MVVM ViewModel + sort logic
│   └── res/
│       ├── layout/                 # XML layouts
│       ├── drawable/               # Gradients, badges, shapes
│       └── values/                 # Themes, strings
└── gradle/
    └── wrapper/
        └── gradle-wrapper.properties
```

---

## 🚀 Build the APK (No Android Studio Needed)

This project uses **GitHub Actions** to build the APK automatically in the cloud for free.

### Step 1 — Fork or upload to GitHub
Create a new GitHub repository and upload all project files.

### Step 2 — Trigger the build
The build runs automatically on every push. You can also trigger it manually:
- Go to **Actions** tab → **Build APK** → **Run workflow**

### Step 3 — Download your APK
- Wait ~3-5 minutes for the build to finish
- Click the completed workflow run
- Scroll to **Artifacts** section
- Download **ReelPlayer-debug**

### Step 4 — Install on your phone
1. Transfer the APK to your Android device
2. Go to **Settings → Install unknown apps** → enable for your browser/file manager
3. Tap the APK file to install

---

## 🔐 Permissions

| Permission | Why |
|---|---|
| `READ_MEDIA_VIDEO` | Android 13+ — read video files |
| `READ_EXTERNAL_STORAGE` | Android 12 and below |

No internet permission. No data leaves your device.

---

## 🎮 How to Use

1. **Open the app** → grant storage permission
2. **Browse** your video list — sort by date or duration
3. **Tap any video** → opens in fullscreen reel player
4. **Swipe up/down** to go to next/previous video
5. **Tap** the screen to pause/play
6. **Long press** to play at 2x speed — release to go back to normal
7. Video **auto-advances** to the next when finished

---

## ⚙️ Minimum Requirements

| Requirement | Value |
|---|---|
| Android Version | 5.0 (API 21) and above |
| Target SDK | 34 (Android 14) |
| Architecture | ARM, ARM64, x86, x86_64 |

---

## 🛠️ Local Development

If you want to build locally:

```bash
# Clone the repo
git clone https://github.com/YOUR_USERNAME/ReelPlayer.git
cd ReelPlayer

# Build debug APK
./gradlew assembleDebug

# APK will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

Requires JDK 17 and Android SDK installed.

---

## 🗺️ Roadmap

- [ ] Search/filter videos by name
- [ ] Volume control gesture (slide up/down on right side)
- [ ] Brightness control gesture (slide up/down on left side)
- [ ] Double tap to seek ±10 seconds
- [ ] Progress bar scrubbing
- [ ] Folder filter (show only specific folder)
- [ ] Share video option
- [ ] Picture-in-picture mode

---

## 📄 License

MIT License — free to use, modify, and distribute.

---

*Built with ❤️ using Kotlin + ExoPlayer*
