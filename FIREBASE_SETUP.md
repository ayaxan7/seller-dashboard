# Firebase Configuration Guide

This guide will help you set up Firebase for the Vendor Dashboard app.

## Prerequisites
- Google account
- Android Studio with the project opened

## Step-by-Step Firebase Setup

### 1. Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"** or **"Create a project"**
3. Enter project name: `vendor-dashboard` (or your preferred name)
4. Click **Continue**
5. Enable/Disable Google Analytics (your choice)
6. Click **Create project**
7. Wait for project creation to complete

### 2. Add Android App to Firebase

1. In Firebase Console, click the **Android icon** to add an Android app
2. Enter the package name: `com.ayaan.sellerdashboard`
   - ⚠️ **Important**: This must match exactly!
3. App nickname (optional): `Vendor Dashboard`
4. Debug signing certificate SHA-1 (optional for now, needed later for some features)
5. Click **Register app**

### 3. Download Configuration File

1. Download the `google-services.json` file
2. Place it in your project at:
   ```
   /Users/ayaan/AndroidStudioProjects/sellerdashboard/app/google-services.json
   ```
3. **Replace** the existing placeholder file
4. Click **Next** and **Continue to console**

### 4. Enable Authentication

1. In Firebase Console sidebar, click **Authentication**
2. Click **Get Started**
3. Go to **Sign-in method** tab
4. Click on **Email/Password**
5. Enable the first toggle (Email/Password)
6. Click **Save**

### 5. Set Up Firestore Database

1. In Firebase Console sidebar, click **Firestore Database**
2. Click **Create database**
3. Choose **Start in test mode** (for development)
   ```
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if request.time < timestamp.date(2025, 12, 31);
       }
     }
   }
   ```
4. Select a **Cloud Firestore location** (choose closest to you)
   - Example: `us-central` or `europe-west`
5. Click **Enable**
6. Wait for provisioning to complete

### 6. Set Up Firebase Storage

1. In Firebase Console sidebar, click **Storage**
2. Click **Get Started**
3. Review the security rules (start in test mode):
   ```
   rules_version = '2';
   service firebase.storage {
     match /b/{bucket}/o {
       match /{allPaths=**} {
         allow read, write: if request.time < timestamp.date(2025, 12, 31);
       }
     }
   }
   ```
4. Click **Next**
5. Select the same location as Firestore
6. Click **Done**

### 7. Firestore Indexes (Optional)

The app currently sorts products in memory to avoid requiring composite indexes. If you want to improve performance for large product lists, you can create a composite index:

1. Go to **Firestore Database** → **Indexes** tab
2. Click **Create Index**
3. Collection ID: `products`
4. Add fields:
   - Field: `vendorId`, Order: `Ascending`
   - Field: `createdAt`, Order: `Descending`
5. Click **Create**

**Note**: The app works without this index, but it will improve query performance for vendors with many products.

## Production Security Rules

⚠️ **Important**: Before going to production, update your security rules!

### Firestore Production Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow users to read/write only their own products
    match /products/{productId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && 
                       request.resource.data.vendorId == request.auth.uid;
      allow update, delete: if request.auth != null && 
                               resource.data.vendorId == request.auth.uid;
    }
  }
}
```

### Storage Production Rules

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /product_images/{imageId} {
      // Anyone can read product images
      allow read: if true;
      // Only authenticated users can upload
      allow write: if request.auth != null &&
                      request.resource.size < 5 * 1024 * 1024 && // Max 5MB
                      request.resource.contentType.matches('image/.*');
    }
  }
}
```

## Verify Setup

### In Firebase Console

1. **Authentication** tab should show "Email/Password" enabled
2. **Firestore Database** tab should show an active database
3. **Storage** tab should show a storage bucket

### In Android Studio

1. Verify `google-services.json` exists in `app/` folder
2. Sync Gradle (File → Sync Project with Gradle Files)
3. No build errors should appear

## Test Your Setup

1. **Build the app**
   ```bash
   ./gradlew assembleDebug
   ```

2. **Run on device/emulator**
   - Make sure device has internet connection

3. **Test Registration**
   - Open app → Register screen
   - Enter email: `test@example.com`
   - Enter password: `test123`
   - Click Register
   - Check Firebase Console → Authentication → Users

4. **Test Product Creation**
   - After login → Click FAB (+)
   - Select an image
   - Fill product details
   - Click Add Product
   - Check Firestore Console → products collection
   - Check Storage Console → product_images folder

## Troubleshooting

### "Default FirebaseApp is not initialized"
- Ensure `google-services.json` is in the correct location
- Rebuild the project
- Check package name matches

### "Permission Denied" on Firestore/Storage
- Check security rules are in test mode
- Ensure user is authenticated
- Check dates in test mode rules haven't expired

### "INTERNET permission denied"
- Check `AndroidManifest.xml` has `INTERNET` permission
- Ensure device has internet connection

### Build fails with google-services error
- Verify `google-services.json` is valid JSON
- Ensure it's in `app/` directory, not project root
- Sync Gradle again

## Getting SHA-1 for Debug Key (Optional)

For some Firebase features, you'll need your debug SHA-1:

```bash
cd ~/.android
keytool -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Copy the SHA-1 and add it to your Firebase app settings.

## Useful Firebase Console Links

- **Project Overview**: https://console.firebase.google.com/project/YOUR_PROJECT_ID
- **Authentication**: https://console.firebase.google.com/project/YOUR_PROJECT_ID/authentication/users
- **Firestore**: https://console.firebase.google.com/project/YOUR_PROJECT_ID/firestore
- **Storage**: https://console.firebase.google.com/project/YOUR_PROJECT_ID/storage

---

## ✅ Checklist

- [ ] Firebase project created
- [ ] Android app added to Firebase
- [ ] `google-services.json` downloaded and placed correctly
- [ ] Authentication enabled (Email/Password)
- [ ] Firestore Database created
- [ ] Firebase Storage set up
- [ ] Project builds successfully
- [ ] Test user can register
- [ ] Test product can be created

Once all items are checked, you're ready to go! 🚀

