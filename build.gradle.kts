// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.20")
    }
}
