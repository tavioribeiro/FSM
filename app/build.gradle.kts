plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("app.cash.sqldelight")
}

android {
    namespace = "com.example.fsm"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.fsm"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}




dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    implementation("androidx.media3:media3-exoplayer:1.4.1")
//    implementation("androidx.media3:media3-ui:1.4.1")
//    implementation("androidx.media3:media3-common:1.4.1")
//    implementation("androidx.media3:media3-session:1.4.1")

    //implementation("androidx.media:media:1.7.0")

    implementation("io.coil-kt:coil:2.7.0")
    //implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.media:media:1.7.0")

    // Suporte para NotificationCompat
    implementation("androidx.core:core-ktx:1.9.0")




    // Koin Core features
    implementation("io.insert-koin:koin-core:3.4.3")
    testImplementation("io.insert-koin:koin-test:3.4.3")
    implementation("io.insert-koin:koin-android:3.4.3")
    implementation("io.insert-koin:koin-android-compat:3.4.3")
    implementation("io.insert-koin:koin-androidx-workmanager:3.4.3")
    implementation("io.insert-koin:koin-androidx-compose:3.4.3")



    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")



    //sqldelight
    implementation("app.cash.sqldelight:android-driver:2.0.2")
    //implementation("app.cash.sqldelight:native-driver:2.0.0")
    implementation("app.cash.sqldelight:coroutines-extensions-jvm:2.0.2")


    //Swipe
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")


    //FruFru
    implementation("androidx.palette:palette:1.0.0")
}