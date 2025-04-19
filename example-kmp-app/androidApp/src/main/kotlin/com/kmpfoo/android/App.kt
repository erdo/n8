package com.kmpfoo.android

import android.app.Application

/**
 * Copyright © 2015-2021 early.co. All rights reserved.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        inst = this

        OG.setApplication(this)
        OG.init()
    }

    companion object {
        lateinit var inst: App private set
    }
}
