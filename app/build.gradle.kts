import com.custorix.CustorixBuildType

plugins {
    alias(libs.plugins.custorix.android.application)
    alias(libs.plugins.custorix.android.application.compose)
}

android {
    namespace = "com.custorix.app"
    defaultConfig {
        applicationId = "com.custorix.app"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = CustorixBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = providers.gradleProperty("minifyWithR8").map(String::toBooleanStrict).getOrElse(true)
            applicationIdSuffix = CustorixBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}