# Vendor Dashboard - E-Commerce Android App

A complete vendor dashboard application built with Jetpack Compose, MVVM architecture, Hilt dependency injection, and Firebase backend.

## 🎯 Features

- **Authentication**: Email/Password login and registration using Firebase Authentication
- **Product Management**: Add, edit, delete, and view products
- **Image Upload**: Upload product images to Firebase Storage
- **Real-time Sync**: Products sync in real-time with Firebase Firestore
- **Clean UI**: Material 3 design with responsive layout
- **MVVM Architecture**: Clean separation of concerns with ViewModel and Repository pattern

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Backend**: Firebase (Auth, Firestore, Storage)
- **Image Loading**: Coil
- **Navigation**: Navigation Compose
- **State Management**: StateFlow & Kotlin Coroutines

## 📋 Prerequisites

Before running this app, you need to:

1. **Android Studio**: Latest version (Hedgehog or newer recommended)
2. **Firebase Project**: Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)

## 🔧 Firebase Setup

### Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" and follow the steps
3. Enable Google Analytics (optional)

### Step 2: Add Android App

1. In your Firebase project, click on "Add app" and select Android
2. Enter package name: `com.ayaan.sellerdashboard`
3. Download the `google-services.json` file
4. Replace the placeholder file in `app/google-services.json` with your downloaded file

### Step 3: Enable Firebase Services

#### Enable Authentication
1. In Firebase Console, go to "Authentication"
2. Click "Get Started"
3. Enable "Email/Password" sign-in method

#### Enable Firestore Database
1. In Firebase Console, go to "Firestore Database"
2. Click "Create database"
3. Start in **test mode** (for development)
4. Choose a location
5. Update security rules (optional, for production):

```firestore
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /products/{productId} {
      // Allow vendors to read/write their own products
      allow read, write: if request.auth != null && 
                          resource.data.vendorId == request.auth.uid;
      // Allow creation of new products
      allow create: if request.auth != null;
    }
  }
}
```

#### Enable Firebase Storage
1. In Firebase Console, go to "Storage"
2. Click "Get Started"
3. Start in **test mode** (for development)
4. Update security rules (optional, for production):

```storage
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /product_images/{imageId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

## 🚀 Running the App

1. **Clone the repository** (if applicable)

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project directory

3. **Sync Gradle**
   - Android Studio should automatically sync
   - If not, click "File > Sync Project with Gradle Files"

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift + F10`

## 📱 App Structure

```
com.ayaan.sellerdashboard/
│
├── di/                         # Hilt dependency injection modules
│   └── FirebaseModule.kt       # Firebase instance providers
│
├── data/
│   ├── model/                  # Data models
│   │   ├── Product.kt
│   │   ├── User.kt
│   │   └── Result.kt
│   │
│   └── repository/             # Data repositories
│       ├── AuthRepository.kt
│       └── ProductRepository.kt
│
├── ui/
│   ├── navigation/             # Navigation setup
│   │   ├── Screen.kt
│   │   └── NavigationGraph.kt
│   │
│   ├── screens/                # UI screens
│   │   ├── login/
│   │   │   ├── LoginScreen.kt
│   │   │   └── LoginViewModel.kt
│   │   ├── register/
│   │   │   ├── RegisterScreen.kt
│   │   │   └── RegisterViewModel.kt
│   │   ├── productlist/
│   │   │   ├── ProductListScreen.kt
│   │   │   └── ProductListViewModel.kt
│   │   ├── addproduct/
│   │   │   ├── AddProductScreen.kt
│   │   │   └── AddProductViewModel.kt
│   │   └── editproduct/
│   │       ├── EditProductScreen.kt
│   │       └── EditProductViewModel.kt
│   │
│   └── theme/                  # App theming
│       ├── Color.kt
│       ├── Type.kt
│       └── Theme.kt
│
├── VendorDashboardApp.kt       # Application class with Hilt
└── MainActivity.kt             # Main activity
```

## 🎨 Features Detail

### Authentication Flow
- Users can register with email and password
- Login with existing credentials
- Automatic navigation based on auth state
- Logout functionality

### Product Management
- **Add Product**: Select image, enter name, price, and description
- **View Products**: Grid/List view of all vendor's products
- **Edit Product**: Update product details and image
- **Delete Product**: Remove product with confirmation dialog

### Data Sync
- Real-time updates using Firestore snapshots
- Automatic product list refresh when data changes
- Loading and error states handled gracefully

## 🔒 Security Notes

**Important**: The current Firebase rules are set to test mode for development. Before deploying to production:

1. Update Firestore security rules to restrict access
2. Update Storage security rules to validate uploads
3. Implement proper authentication checks
4. Add data validation on the backend

## 📝 Dependencies

All dependencies are managed in `gradle/libs.versions.toml`:

- Jetpack Compose & Material 3
- Hilt (Dependency Injection)
- Firebase (Auth, Firestore, Storage)
- Coil (Image Loading)
- Navigation Compose
- Coroutines & Flow

## 🐛 Troubleshooting

### Google Services Plugin Error
- Ensure `google-services.json` is in the `app/` directory
- Check that the package name matches in both files

### Build Errors
- Clean and rebuild: `Build > Clean Project` then `Build > Rebuild Project`
- Invalidate caches: `File > Invalidate Caches / Restart`

### Firebase Connection Issues
- Verify internet permissions in AndroidManifest.xml
- Check Firebase project configuration
- Ensure google-services.json is properly configured

## 📄 License

This project is for educational purposes.

## 👤 Author

Created as a complete vendor dashboard solution for Android.

---

**Note**: Remember to replace the placeholder `google-services.json` with your actual Firebase configuration file before running the app!

