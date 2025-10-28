# Common Errors & Solutions

This document lists common errors you might encounter and their solutions.

## Firebase Errors

### 1. ❌ FAILED_PRECONDITION: The query requires an index

**Error Message:**
```
com.google.firebase.firestore.FirebaseFirestoreException: FAILED_PRECONDITION: 
The query requires an index.
```

**Solution:**
✅ **Already Fixed!** The app now sorts products in memory instead of using Firestore's `orderBy()`, which requires a composite index.

**Optional**: For better performance with large product lists, you can create the index:
1. Click the URL in the error message (it takes you directly to the index creation page)
2. OR manually create it in Firebase Console → Firestore → Indexes
   - Collection: `products`
   - Fields: `vendorId` (Ascending), `createdAt` (Descending)

---

### 2. ❌ Default FirebaseApp is not initialized

**Error Message:**
```
FirebaseApp with name [DEFAULT] doesn't exist.
```

**Causes:**
- `google-services.json` not in the correct location
- Package name mismatch
- Gradle sync not completed

**Solutions:**
1. Verify `google-services.json` is in `/app/` directory
2. Check package name in `google-services.json` matches `com.ayaan.sellerdashboard`
3. Sync Gradle: `File → Sync Project with Gradle Files`
4. Clean and rebuild: `Build → Clean Project` then `Build → Rebuild Project`

---

### 3. ❌ Permission Denied (Firestore/Storage)

**Error Message:**
```
PERMISSION_DENIED: Missing or insufficient permissions
```

**Causes:**
- Firestore/Storage security rules are too restrictive
- User not authenticated
- Test mode rules expired

**Solutions:**

**For Firestore:**
1. Go to Firebase Console → Firestore → Rules
2. Ensure test mode rules:
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if request.time < timestamp.date(2025, 12, 31);
       }
     }
   }
   ```
3. Update the date if expired

**For Storage:**
1. Go to Firebase Console → Storage → Rules
2. Ensure test mode rules:
   ```javascript
   rules_version = '2';
   service firebase.storage {
     match /b/{bucket}/o {
       match /{allPaths=**} {
         allow read, write: if request.time < timestamp.date(2025, 12, 31);
       }
     }
   }
   ```

---

### 4. ❌ Authentication Failed

**Error Message:**
```
The email address is already in use by another account
```

**Solution:**
- Use a different email
- OR delete the existing user in Firebase Console → Authentication → Users

**Error Message:**
```
The password is invalid or the user does not have a password
```

**Solution:**
- Check password is correct
- Ensure the email exists in Authentication → Users
- Password must be at least 6 characters

---

### 5. ❌ Image Upload Failed

**Error Message:**
```
Upload failed / Storage error
```

**Causes:**
- No internet connection
- Storage rules too restrictive
- File too large
- Invalid file type

**Solutions:**
1. Check internet connection
2. Verify Storage rules allow writes
3. Ensure image file is less than 10MB
4. Only select image files (jpg, png, etc.)

---

## Build Errors

### 6. ❌ Unresolved reference errors

**Error Message:**
```
Unresolved reference: hilt
Unresolved reference: firebase
```

**Solution:**
1. **Sync Gradle first!** `File → Sync Project with Gradle Files`
2. Wait for sync to complete
3. Errors should disappear
4. If not, try: `Build → Clean Project` then rebuild

---

### 7. ❌ google-services plugin error

**Error Message:**
```
File google-services.json is missing
```

**Solution:**
1. Download `google-services.json` from Firebase Console
2. Place it in `/app/` directory (NOT the root directory)
3. Sync Gradle again

---

### 8. ❌ Duplicate class errors

**Error Message:**
```
Duplicate class found in modules
```

**Solution:**
1. Clean the project: `Build → Clean Project`
2. Delete `.gradle` folder in project root
3. Invalidate caches: `File → Invalidate Caches / Restart`
4. Rebuild

---

## Runtime Errors

### 9. ❌ App crashes on image selection

**Error Message:**
```
Permission denial / SecurityException
```

**Causes:**
- Missing photo/media permissions
- Android 13+ requires specific permissions

**Solutions:**
1. For Android 13+, the app should automatically request permissions
2. Manually grant permissions: Settings → Apps → Vendor Dashboard → Permissions → Photos
3. Ensure `READ_MEDIA_IMAGES` is in AndroidManifest.xml (already added)

---

### 10. ❌ Network error / No internet

**Error Message:**
```
Unable to resolve host
Failed to connect
```

**Solutions:**
1. Check device has internet connection
2. Try WiFi and mobile data
3. Verify `INTERNET` permission in AndroidManifest.xml (already added)
4. Check if emulator has network access
5. Firewall/proxy blocking Firebase?

---

### 11. ❌ Products not showing after adding

**Possible Causes:**
- Product added to wrong vendor ID
- Real-time listener not working
- Network issue

**Solutions:**
1. Check Firebase Console → Firestore → products collection
2. Verify `vendorId` field matches your user ID
3. Pull down to refresh the list
4. Logout and login again
5. Check internet connection

---

### 12. ❌ Image not loading in product card

**Possible Causes:**
- Invalid image URL
- Storage permissions
- Network issue
- Coil error

**Solutions:**
1. Check image URL in Firestore (should start with `https://`)
2. Verify image exists in Storage console
3. Check Storage security rules allow read
4. Clear app data and try again

---

## Gradle/Build Configuration

### 13. ❌ Version conflict errors

**Error Message:**
```
Dependency requires a different version
```

**Solution:**
1. Update dependencies in `gradle/libs.versions.toml`
2. Use consistent version numbers
3. Sync Gradle
4. If issues persist, use the version catalog as-is

---

### 14. ❌ Kapt errors

**Error Message:**
```
Kapt error: KaptWithoutKotlincTask
```

**Solution:**
1. Ensure kapt plugin is applied in `app/build.gradle.kts`
2. Check `kotlin-kapt` is in the plugins block
3. Sync Gradle

---

## Emulator/Device Issues

### 15. ❌ App not installing

**Solutions:**
1. Uninstall existing app from device
2. Clean project and rebuild
3. Try a different device/emulator
4. Check minimum SDK (app requires API 28+)

---

### 16. ❌ Emulator too slow

**Solutions:**
1. Use a physical device if possible
2. Enable hardware acceleration
3. Use x86 emulator image (faster than ARM)
4. Allocate more RAM to emulator

---

## Quick Debugging Checklist

When facing any error:

- [ ] Have you synced Gradle recently?
- [ ] Is `google-services.json` in the correct location?
- [ ] Are all Firebase services enabled in console?
- [ ] Are security rules in test mode?
- [ ] Is there internet connection?
- [ ] Have you tried Clean Project → Rebuild?
- [ ] Have you tried Invalidate Caches & Restart?
- [ ] Are you using the correct package name?
- [ ] Is your Android Studio up to date?

---

## Still Having Issues?

1. Check Android Studio's **Logcat** for detailed error messages
2. Look for the full stack trace (red text in Logcat)
3. Search for the specific error message online
4. Check Firebase Console → Usage tab for any quota limits
5. Verify all steps in `FIREBASE_SETUP.md` were completed

---

## Pro Tips

💡 **Enable Verbose Logging**
For Firebase debugging, enable debug logging:
```kotlin
FirebaseFirestore.setLoggingEnabled(true)
```

💡 **Test Mode Rules Expiration**
Remember to update the date in test mode rules regularly, or switch to production rules.

💡 **Check Firebase Status**
Visit [Firebase Status Dashboard](https://status.firebase.google.com/) to check if Firebase services are down.

💡 **Offline Persistence**
Firestore has offline persistence enabled by default. Sometimes you need to clear app data to reset the cache.

---

**Last Updated:** October 26, 2025

