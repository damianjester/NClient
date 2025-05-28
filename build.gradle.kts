// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.spotless) apply true
}

spotless {
    kotlin {
        ktlint("1.6.0")
        target("app/src/**/*.kt")
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }
}
