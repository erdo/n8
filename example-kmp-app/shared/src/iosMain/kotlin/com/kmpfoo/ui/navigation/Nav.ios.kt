package com.kmpfoo.ui.navigation

import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual fun dataPath(application: Any?): Path {
    val fileManager = NSFileManager.defaultManager()
    val urls = fileManager.URLsForDirectory(
        NSDocumentDirectory,
        NSUserDomainMask
    )
    val documentDirectoryUrl = urls.first() as NSURL
    val pathString = documentDirectoryUrl.path ?: ""
    return pathString.toPath().normalized()
}