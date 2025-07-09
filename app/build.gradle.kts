plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}


val geminiApiKey = project.findProperty("GEMINI_API_KEY") as String? ?: ""
android {
    namespace = "com.example.studypal"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.studypal"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
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
        isCoreLibraryDesugaringEnabled = true

    }
}

dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Amplify
    implementation("com.amplifyframework:core:2.27.4")
    implementation("com.amplifyframework:aws-api:2.27.4")
    implementation("com.amplifyframework:aws-auth-cognito:2.27.4")
    implementation("com.amplifyframework:aws-datastore:2.27.4")
    implementation("com.amplifyframework:aws-push-notifications-pinpoint:2.5.0")

    // AndroidX
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.android.material:material:1.9.0")

    // Circle ImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //volley
    implementation("com.android.volley:volley:1.2.1")

    // Java 8 support
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    //gemini
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20231013")

    implementation("androidx.work:work-runtime:2.9.0")

    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    //  Unit Tests
    testImplementation("junit:junit:4.13.2")
    implementation ("com.google.android.material:material:1.11.0")

    // Android Instrumented Tests — force correct versions
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
