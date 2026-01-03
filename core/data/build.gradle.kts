plugins {
    alias(libs.plugins.custorix.android.library)
}

android {
    namespace = "com.custorix.core.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.database)
}