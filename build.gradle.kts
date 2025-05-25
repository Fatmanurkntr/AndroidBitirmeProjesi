// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinKapt) apply false
    alias(libs.plugins.androidxNavigationSafeargsKotlin) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.kotlinParcelize) apply false // libs.versions.toml'daki kotlinParcelize alias'ına göre
}
