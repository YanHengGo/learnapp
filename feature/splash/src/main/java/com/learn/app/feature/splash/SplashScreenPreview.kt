package com.learn.app.feature.splash

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, name = "スプラッシュ画面")
@Composable
private fun PreviewSplash() {
    MaterialTheme {
        SplashContent()
    }
}
