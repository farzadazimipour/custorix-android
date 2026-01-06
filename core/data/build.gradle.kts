plugins {
    alias(libs.plugins.custorix.android.library)
    alias(libs.plugins.custorix.hilt)
}

android {
    namespace = "com.custorix.core.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.database)
    implementation(projects.core.network)

    implementation(libs.arrow.core)
}