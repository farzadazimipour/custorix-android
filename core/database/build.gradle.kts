plugins {
    alias(libs.plugins.custorix.android.library)
    alias(libs.plugins.custorix.android.room)
    alias(libs.plugins.custorix.hilt)
}

android {
    namespace = "com.custorix.core.database"
}

dependencies {
    implementation(libs.kotlinx.datetime)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}