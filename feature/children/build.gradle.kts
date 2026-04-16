plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.learn.app.feature.children"
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
        // TestStorage の出力を Gradle が中継し、collectScreenshots タスクが
        // screenshots/children/NNN/ へコピーする
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
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    // TestStorage API（スクリーンショットを Gradle が自動収集する仕組み）
    androidTestImplementation(libs.androidx.test.services)
    androidTestUtil(libs.androidx.test.services)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// ─── スクリーンショット収集タスク ──────────────────────────────────────────
// connectedAndroidTest 完了後に自動実行。adb 不要（ホスト側ファイルコピーのみ）。
//
// 保存先: <rootProject>/screenshots/children/001/ 〜 NNN/
//   NNN は実行ごとにカウントアップ
//
// 手動実行: ./gradlew :feature:children:collectScreenshots

val screenName = "children"

tasks.register("collectScreenshots") {
    group = "verification"
    description = "Copy screenshots from build output to screenshots/$screenName/NNN/"
    doLast {
        // TestStorage が収集した PNG を全デバイスフォルダから探す
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

        // NNN: 既存フォルダの最大番号 + 1
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

// connectedAndroidTest 完了後に自動で collectScreenshots を実行
tasks.matching { it.name == "connectedDebugAndroidTest" }.configureEach {
    finalizedBy("collectScreenshots")
}
