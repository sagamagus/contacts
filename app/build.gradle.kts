plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.sqldelight.plugin)
    alias(libs.plugins.dagger.hilt)
}

android {
    namespace = "com.alfredoguerrero.contacts"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.alfredoguerrero.contacts"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.sqldelight)
    implementation (libs.picasso)
    implementation (libs.androidx.recyclerview)
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

sqldelight {
    databases {
        create("ContactsDatabase") {
            packageName.set("com.alfredoguerrero.contacts")
        }
    }
}