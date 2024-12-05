plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.dar.nclientv2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dar.nclientv2"
        minSdk = 21
        targetSdk = 34

        versionCode = 305
        versionName = "3.0.5-stable"

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            versionNameSuffix = "-release"
            resValue("string", "app_name", "NClientV2")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "NClientV2 Debug")
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
