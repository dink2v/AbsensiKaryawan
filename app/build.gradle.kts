plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.absensikaryawan"

    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.absensikaryawan"

        minSdk = 24

        targetSdk = 37

        versionCode = 1

        versionName = "1.0"

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
        sourceCompatibility =
            JavaVersion.VERSION_11

        targetCompatibility =
            JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // ==========================================================
    // COMPOSE BOM
    // ==========================================================

    implementation(
        platform(
            libs.androidx.compose.bom
        )
    )

    androidTestImplementation(
        platform(
            libs.androidx.compose.bom
        )
    )


    // ==========================================================
    // ANDROIDX
    // ==========================================================

    implementation(
        libs.androidx.core.ktx
    )

    implementation(
        libs.androidx.lifecycle.runtime.ktx
    )

    implementation(
        libs.androidx.activity.compose
    )


    // ==========================================================
    // COMPOSE
    // ==========================================================

    implementation(
        libs.androidx.compose.ui
    )

    implementation(
        libs.androidx.compose.ui.graphics
    )

    implementation(
        libs.androidx.compose.ui.tooling.preview
    )

    implementation(
        libs.androidx.compose.material3
    )

    implementation(
        "androidx.compose.material:material-icons-extended"
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
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


    // ==========================================================
    // TEST
    // ==========================================================

    testImplementation(
        libs.junit
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )
}