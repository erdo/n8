package com.kmpfoo.android

import android.app.Application
import android.content.pm.ApplicationInfo
import co.early.fore.core.delegate.DebugDelegateDefault
import co.early.fore.core.delegate.Fore
import co.early.n8.N8
import co.early.n8.NavigationModel
import com.kmpfoo.ui.navigation.Location
import com.kmpfoo.ui.navigation.TabHostId
import com.kmpfoo.ui.navigation.createNavigation
import kotlin.reflect.KClass

/**
 * Copyright © 2015-2023 early.co. All rights reserved.
 *
 * simple manual DI for the sample, but you do you
 */
@Suppress("UNUSED_PARAMETER")
object OG {

    private var initialized = false
    private val dependencies = HashMap<KClass<*>, Any>()

    fun setApplication(application: Application) {

        val isDebug = application.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

        // create dependency graph
        if (isDebug) {
            Fore.setDelegate(DebugDelegateDefault("foo_"))
        }

        createNavigation(application)

        val n8: NavigationModel<Location, TabHostId> = N8.n8<Location, TabHostId>()

        // add models to the dependencies map if you will need them later
        dependencies[NavigationModel::class] = n8
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
     *     yourModel = OG[YourModel::class]
     * </code>
     *
     * If you want to more tightly scoped object, one way is to pass a factory class here and create
     * an instance where you need it
     *
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(model: KClass<T>): T = dependencies[model] as T

    fun <T : Any> putMock(clazz: KClass<T>, instance: T) {
        dependencies[clazz] = instance as Any
    }
}
