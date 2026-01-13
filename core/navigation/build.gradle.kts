plugins {
    alias(libs.plugins.custorix.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.custorix.core.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewModel.navigation3)
}