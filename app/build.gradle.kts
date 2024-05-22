plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  kotlin("kapt")
  id("com.google.dagger.hilt.android")
}

android {
  namespace = "com.aura"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.aura"
    minSdk = 24
    targetSdk = 33
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    viewBinding = true
  }
}

dependencies {

  implementation("androidx.core:core-ktx:1.9.0")
  implementation("androidx.activity:activity-ktx:1.8.2")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.8.0")
  implementation("androidx.annotation:annotation:1.6.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

  // Dagger Hilt Dependency Injection
  implementation("com.google.dagger:hilt-android:2.50")
  kapt("com.google.dagger:hilt-android-compiler:2.50")

  // Moshi JSON Library
  implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

  // Retrofit for Network Requests
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
  implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

  //datastore
  implementation("androidx.datastore:datastore-preferences:1.1.0")


  // Testing Dependencies
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  testImplementation("org.mockito:mockito-inline:5.2.0")
  testImplementation("org.mockito:mockito-core:5.2.0")
  testImplementation("io.mockk:mockk:1.13.9")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0-RC2")
  testImplementation ("androidx.arch.core:core-testing:2.1.0")
  testImplementation("com.google.truth:truth:1.4.2")
  testImplementation("org.robolectric:robolectric:4.12.2")
}

kapt {
  correctErrorTypes = true
}