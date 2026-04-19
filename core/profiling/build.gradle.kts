plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.learn.app.core.profiling"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.minSdk.get().toInt() }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmVersion.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmVersion.get())
    }
    kotlin { jvmToolchain(libs.versions.jvmVersion.get().toInt()) }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(project(":core:startup"))
}
