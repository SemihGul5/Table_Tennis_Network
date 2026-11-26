// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.dagger.hilt.android") version "2.60.1" apply false
    alias(libs.plugins.ksp) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies{
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.21")

    }
}
