import com.custorix.CustorixBuildType

plugins {
    alias(libs.plugins.custorix.android.application)
    alias(libs.plugins.custorix.android.application.compose)
    alias(libs.plugins.custorix.hilt)
    alias(libs.plugins.kotlin.serialization)
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
    /**
     * App depends on data module only for Hilt DI bindings (DataModule)
     * This violates clean architecture but is required for Hilt to discover @Binds annotations
     * All data module classes are internal - UI layer only accesses domain interfaces
     */
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewModel.navigation3)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.espresso.core)
}