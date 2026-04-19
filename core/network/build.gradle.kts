plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.learn.app.core.network"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.minSdk.get().toInt() }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmVersion.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmVersion.get())
    }
    kotlin { jvmToolchain(libs.versions.jvmVersion.get().toInt()) }
    buildFeatures { buildConfig = true }
    buildTypes {
        debug {
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://ts-memo-api-1.onrender.com/\""
            )
        }
        release {
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://ts-memo-api-885966500966.asia-northeast1.run.app/\""
            )
        }
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
