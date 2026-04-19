package com.learn.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.learn.app.core.profiling.StartupProfilingTrigger
import com.learn.app.core.startup.AppStartInfoCollector
import com.learn.app.core.startup.AppStartInfoLogger
import com.learn.app.ui.theme.LearnAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LearnAppTheme {
                NavGraph()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 35) {
            val snapshot = AppStartInfoCollector(this).collect()
            snapshot?.let {
                AppStartInfoLogger.log(it)
                StartupProfilingTrigger(this).maybeProfile(it)
            }
        }
    }
}
