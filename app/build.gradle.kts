plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "2.0.0"
}

android {
    namespace = "com.github.damianjester.nclient"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.github.damianjester.nclient"
        minSdk = 21
        targetSdk = 34

        versionCode = 1
        versionName = "0.1.0"

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            versionNameSuffix = "-release"
            resValue("string", "app_name", "NClient")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "NClient Debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // AndroidX
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.material)
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.swiperefreshlayout)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material2)
    implementation(libs.androidx.material.icons)

    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)

    implementation(libs.decompose)
    implementation(libs.decompose.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.bundles.ktor)
    implementation(libs.kotlinx.datetime)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.telescope)
    implementation(libs.coil.okhttp)

    // Other
    implementation(libs.okhttp.urlconnection) // Because of min SDK
    implementation(libs.persistentcookiejar)
    implementation(libs.jsoup)
    implementation(libs.acra.core)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.glide) {
        exclude("com.android.support")
    }
    implementation(libs.ambilwarna)
    implementation(libs.fastscroll)
    implementation(libs.localehelper)
}
