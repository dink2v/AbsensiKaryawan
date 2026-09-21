plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.absensikaryawan"

    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.absensikaryawan"

        minSdk = 24
        targetSdk = 37

        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {

    // ==========================================================
    // COMPOSE BOM
    // ==========================================================

    implementation(
        platform(
            "androidx.compose:compose-bom:2026.08.00"
        )
    )

    androidTestImplementation(
        platform(
            "androidx.compose:compose-bom:2026.08.00"
        )
    )


    // ==========================================================
    // ANDROIDX
    // ==========================================================

    implementation(
        "androidx.core:core-ktx:1.19.0"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.11.0"
    )

    implementation(
        "androidx.activity:activity-compose:1.13.0"
    )


    // ==========================================================
    // COMPOSE
    // ==========================================================

    implementation(
        "androidx.compose.ui:ui"
    )

    implementation(
        "androidx.compose.ui:ui-graphics"
    )

    implementation(
        "androidx.compose.ui:ui-tooling-preview"
    )

    implementation(
        "androidx.compose.material3:material3"
    )

    implementation(
        "androidx.compose.material:material-icons-extended"
    )

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )

    debugImplementation(
        "androidx.compose.ui:ui-test-manifest"
    )


    // ==========================================================
    // NAVIGATION
    // ==========================================================

    implementation(
        "androidx.navigation:navigation-compose:2.9.8"
    )


    // ==========================================================
    // FIREBASE
    // ==========================================================

    implementation(
        platform(
            "com.google.firebase:firebase-bom:34.16.0"
        )
    )

    implementation(
        "com.google.firebase:firebase-auth"
    )

    implementation(
        "com.google.firebase:firebase-firestore"
    )

    implementation(
        "com.google.firebase:firebase-messaging"
    )


    // ==========================================================
    // GOOGLE LOGIN
    // ==========================================================

    implementation(
        "androidx.credentials:credentials:1.5.0"
    )

    implementation(
        "androidx.credentials:credentials-play-services-auth:1.5.0"
    )

    implementation(
        "com.google.android.libraries.identity.googleid:googleid:1.1.1"
    )


    // ==========================================================
    // COROUTINE
    // ==========================================================

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2"
    )


    // ==========================================================
    // DATASTORE
    // ==========================================================

    implementation(
        "androidx.datastore:datastore-preferences:1.1.1"
    )


    // ==========================================================
    // CAMERA
    // ==========================================================

    implementation(
        "androidx.camera:camera-camera2:1.4.2"
    )

    implementation(
        "androidx.camera:camera-lifecycle:1.4.2"
    )

    implementation(
        "androidx.camera:camera-view:1.4.2"
    )


    // ==========================================================
    // ML KIT QR
    // ==========================================================

    implementation(
        "com.google.mlkit:barcode-scanning:17.3.0"
    )

    implementation(
        "androidx.camera:camera-mlkit-vision:1.4.2"
    )

    implementation(
        "com.google.zxing:core:3.5.3"
    )


    // ==========================================================
    // TEST
    // ==========================================================

    testImplementation(
        "junit:junit:4.13.2"
    )

    androidTestImplementation(
        "androidx.test.ext:junit:1.3.0"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.7.0"
    )

    androidTestImplementation(
        "androidx.compose.ui:ui-test-junit4"
    )
}