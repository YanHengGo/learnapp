plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.learn.app.feature.summary"
    compileSdk = 36
    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["useTestStorageService"] = "true"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin { jvmToolchain(11) }
    buildFeatures { compose = true }
    testOptions {
        managedDevices {}
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.services)
    androidTestUtil(libs.androidx.test.services)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// ─── スクリーンショット収集タスク ──────────────────────────────────────────────
val screenName = "summary"

tasks.register("collectScreenshots") {
    group = "verification"
    description = "Copy screenshots from build output to screenshots/$screenName/NNN/"
    doLast {
        val sourceRoot = project.file(
            "build/outputs/connected_android_test_additional_output/debugAndroidTest/connected"
        )
        val pngFiles = sourceRoot.walkTopDown()
            .filter { it.isFile && it.extension == "png" }
            .toList()

        if (pngFiles.isEmpty()) {
            println("No screenshots found in $sourceRoot")
            return@doLast
        }

        val destBase = rootProject.file("screenshots/$screenName")
        destBase.mkdirs()
        val nextNum = (destBase.listFiles()
            ?.mapNotNull { it.name.toIntOrNull() }
            ?.maxOrNull() ?: 0) + 1
        val destDir = File(destBase, "%03d".format(nextNum))
        destDir.mkdirs()

        pngFiles.forEach { png -> png.copyTo(File(destDir, png.name), overwrite = true) }
        println("${pngFiles.size} screenshots → ${destDir.absolutePath}")
    }
}

tasks.matching { it.name == "connectedDebugAndroidTest" }.configureEach {
    finalizedBy("collectScreenshots")
}
