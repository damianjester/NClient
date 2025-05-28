plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "com.github.damianjester.nclient"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.github.damianjester.nclient"
        minSdk = 21
        targetSdk = 35

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
        compileOptions {
            if (System.getProperty("idea.active") == "true") {
                println("Enable coroutine debugging")
                freeCompilerArgs = listOf("-Xdebug")
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)

    implementation(libs.bundles.ktor)
    implementation(libs.bundles.decompose)
    implementation(libs.bundles.coil)

    implementation(libs.telescope)
    implementation(libs.apache.commons.text)
    implementation(libs.kotlinx.datetime)
    implementation("app.cash.sqldelight:android-driver:2.1.0")
    implementation("app.cash.sqldelight:coroutines-extensions:2.1.0")
    implementation("org.slf4j:slf4j-android:1.7.36")

    // Legacy
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

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.github.damianjester.nclient")
        }
    }
}
