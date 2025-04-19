package com.kmpfoo.android

import android.app.Application
import android.content.pm.ApplicationInfo
import co.early.fore.core.delegate.DebugDelegateDefault
import co.early.fore.core.delegate.Fore
import co.early.n8.N8
import co.early.n8.NavigationModel
import com.kmpfoo.ui.navigation.Location
import com.kmpfoo.ui.navigation.createNavigation

/**
 * Copyright © 2015-2023 early.co. All rights reserved.
 */
@Suppress("UNUSED_PARAMETER")
object OG {

    private var initialized = false
    private val dependencies = HashMap<Class<*>, Any>()

    fun setApplication(application: Application) {

        val isDebug = application.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

        // create dependency graph
        if (isDebug) {
            Fore.setDelegate(DebugDelegateDefault("foo_"))
        }

        createNavigation(application)

        val n8: NavigationModel<Location, Unit> = N8.n8<Location, Unit>()

        // add models to the dependencies map if you will need them later
        dependencies[NavigationModel::class.java] = n8
    }

    fun init() {
        if (!initialized) {
            initialized = true

            // run any necessary initialization code once object graph has been created here

        }
    }

    /**
     * This is how dependencies get injected, typically an Activity/Fragment/View will call this
     * during the onCreate()/onCreateView()/onFinishInflate() method respectively for each of the
     * dependencies it needs.
     *
     * Can use a DI library for similar behaviour using annotations
     *
     * Will return mocks if they have been set previously in putMock()
     *
     *
     * Call it like this:
     *
     * <code>
     *     yourModel = OG[YourModel::class.java]
     * </code>
     *
     * If you want to more tightly scoped object, one way is to pass a factory class here and create
     * an instance where you need it
     *
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(model: Class<T>): T = dependencies[model] as T

    fun <T> putMock(clazz: Class<T>, instance: T) {
        dependencies[clazz] = instance as Any
    }
}
