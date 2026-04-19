package com.learn.app.core.startup

data class AppStartSnapshot(
    val startType: String,
    val reason: String,
    val wasForceStopped: Boolean,
    val launchMode: String,
    val bindApplicationMs: Long?,
    val appOnCreateMs: Long?,
    val firstFrameMs: Long?,
)
