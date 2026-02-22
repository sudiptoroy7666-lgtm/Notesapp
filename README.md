# 📝 Notes App

> Secure, offline-first note-taking with Firebase sync, image attachments, and smart reminders

![Platform](https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android)
![Language](https://img.shields.io/badge/Language-Java-orange?style=flat-square&logo=java)
![Firebase](https://img.shields.io/badge/Backend-Firebase-yellow?style=flat-square&logo=firebase)
![Min SDK](https://img.shields.io/badge/Min%20SDK-API%2021-blue?style=flat-square)

---

## Overview

A production-quality Android note-taking app built in Java that goes far beyond a simple CRUD demo. It handles the full identity lifecycle — email verification, secure auth, password reset — then layers on image attachments stored in Firebase Storage, WorkManager-scheduled reminders that fire even when the app is killed, and seamless offline read access.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 Email/Password Auth | Full registration and login with Firebase Authentication |
| ✉️ Email Verification | Account activation via email before access is granted |
| 🔑 Password Reset Flow | Self-service password recovery without third-party UI |
| 🖼️ Image Attachments | Attach photos to notes stored in Firebase Storage |
| ⏰ WorkManager Reminders | Scheduled notifications that survive app death and reboots |
| 📴 Offline Read Access | Notes readable without internet via Firestore persistence |
| ☁️ Firestore Sync | Real-time sync across devices when online |

---

## 🛠️ Tech Stack

- **Language:** Java
- **Auth:** Firebase Authentication
- **Database:** Cloud Firestore (with offline persistence)
- **Storage:** Firebase Storage
- **Image Loading:** Picasso
- **Background Tasks:** WorkManager
- **Min SDK:** API 21 (Android 5.0)

---

## 🏗️ Architecture

```
├── auth/
│   ├── LoginActivity.java
│   ├── RegisterActivity.java
│   └── ResetPasswordActivity.java
├── notes/
│   ├── NotesListActivity.java
│   ├── CreateNoteActivity.java
│   └── NoteAdapter.java
├── workers/
│   └── NotificationWorker.java
└── utils/
    └── FirebaseHelper.java
```

---

## 💡 Implementation Highlights

**WorkManager Reminders**
The reminder system uses a custom `NotificationWorker` that survives app death and device restarts. Unlike `AlarmManager`, WorkManager respects Doze mode and battery optimisation constraints introduced in Android 6+, ensuring reminders fire reliably regardless of the device state.

**Offline-First Firestore**
Firestore offline persistence is enabled so reads are served instantly from the local cache. Writes are queued locally and sync automatically the moment connectivity returns — no manual retry logic needed.

**Auth Lifecycle**
The full register → email verify → login → password reset flow is implemented natively without any third-party auth UI library, giving full control over UX.

---

## 📸 Screenshots

<p align="center">
  <img src="https://github.com/sudiptoroy7666-lgtm/portfolio/blob/fbc009ea41d89c1956497af02910183aa3dd1ecc/note1.jpg" width="18%"/>
  <img src="https://github.com/sudiptoroy7666-lgtm/portfolio/blob/fbc009ea41d89c1956497af02910183aa3dd1ecc/note2.jpg" width="18%"/>
  <img src="https://github.com/sudiptoroy7666-lgtm/portfolio/blob/fbc009ea41d89c1956497af02910183aa3dd1ecc/note3.jpg" width="18%"/>
  <img src="https://github.com/sudiptoroy7666-lgtm/portfolio/blob/fbc009ea41d89c1956497af02910183aa3dd1ecc/note4.jpg" width="18%"/>
  <img src="https://github.com/sudiptoroy7666-lgtm/portfolio/blob/fbc009ea41d89c1956497af02910183aa3dd1ecc/note5.jpg" width="18%"/>
</p>
<p align="center">
  <img src="https://github.com/sudiptoroy7666-lgtm/portfolio/blob/fbc009ea41d89c1956497af02910183aa3dd1ecc/note6.jpg" width="18%"/>
  <img src="https://github.com/sudiptoroy7666-lgtm/portfolio/blob/fbc009ea41d89c1956497af02910183aa3dd1ecc/note7.jpg" width="18%"/>
</p>

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- A Firebase project with Authentication and Firestore enabled

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/sudiptoroy7666-lgtm/notes-app.git
   cd notes-app
   ```

2. **Connect Firebase**
   - Go to the [Firebase Console](https://console.firebase.google.com/)
   - Create a new project and add an Android app
   - Download `google-services.json` and place it in the `app/` directory

3. **Enable Firebase services**
   - Authentication → Email/Password provider
   - Cloud Firestore → Create database
   - Storage → Set up storage bucket

4. **Build and run**
   ```bash
   ./gradlew assembleDebug
   ```

---

## 🔮 Future Improvements

- [ ] Full-text search with tags and filters
- [ ] Export / import notes as JSON backup
- [ ] End-to-end encryption for sensitive notes
- [ ] Rich text editor (bold, italic, bullet lists)
- [ ] Note sharing between users

---

## 👤 Author

**Sudipta Roy**  
Android Developer | Java & Kotlin  
📧 sudiptoroy7666@gmail.com  
🔗 [Portfolio](https://sudiptoroy7666-lgtm.github.io/portfolio/) · [LinkedIn](https://www.linkedin.com/in/sudipta-roy-3873512b4/) · [GitHub](https://github.com/sudiptoroy7666-lgtm)
