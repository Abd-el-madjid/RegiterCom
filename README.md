# RegiterCom

RegiterCom is a mobile application that simplifies the submission and tracking of business registration requests from users' mobile phones.

## Features:

### Business Registration Requests
- Users can seamlessly submit and track business registration requests through the mobile application.

### Communication with Admin
- Users can message the admin directly through the app for support, inquiries, or reporting issues.

### Profile Management
- Users can modify their profile information for accurate and up-to-date details.

### App Settings
- Customizable app settings, including language preferences and brightness adjustments (dark/light mode).

### Notification and Email System

#### Server.js
- Run `server.js` to enable notification functionality.
- The server listens to the `Notification` table, sending notifications and emails to associated users.
- Configure the source email and password in `server.js`. Due to Google's recent changes (as of May 30, 2022), consider enabling 2-factor authentication (2FA) or switching to XOAuth2.

#### Firebase Service Account
- Utilize the `serviceAccount` generated from the Firebase Console for secure communication with Firebase services.

### Connecting Firebase to Android Studio

1. **Create a Firebase Project:**
   - Visit the [Firebase Console](https://console.firebase.google.com/).
   - Click on "Add Project" and follow the setup instructions.

2. **Add Your Android App to Firebase:**
   - In the Firebase Console, select your project.
   - Click on "Add app" and follow the setup instructions to register your Android app.

3. **Download the `google-services.json` File:**
   - After registering your app, download the `google-services.json` file.
   - Place the file in the `app` directory of your Android Studio project.

4. **Add Firebase SDK to Your App:**
   - Open your `build.gradle` file (Module: `app`) and add the following dependencies:
     ```gradle
     implementation 'com.google.firebase:firebase-analytics:20.0.0'
     // Add other Firebase dependencies as needed
     ```
   - Sync your project with the updated Gradle files.

5. **Initialize Firebase in Your App:**
   - In your `Application` class or main activity, add the following code to initialize Firebase:
     ```java
     import com.google.firebase.FirebaseApp;

     // Inside onCreate() method
     FirebaseApp.initializeApp(this);
     ```

## Getting Started:

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/Abd-el-madjid/RegiterCom.git
   cd RegiterCom
