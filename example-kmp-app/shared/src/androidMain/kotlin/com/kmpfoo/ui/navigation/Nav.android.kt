package com.kmpfoo.ui.navigation

import android.app.Application
import okio.Path
import okio.Path.Companion.toOkioPath

actual fun dataPath(application: Any?): Path {
    return (application as Application).filesDir.toOkioPath()
}