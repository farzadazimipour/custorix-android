plugins {
    alias(libs.plugins.custorix.android.library)
}

android {
    namespace = "com.custorix.core.network"
}

dependencies {
    implementation(libs.arrow.core)
}