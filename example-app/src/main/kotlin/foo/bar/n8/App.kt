@file:OptIn(LowLevelApi::class)

package foo.bar.n8

import android.app.Application
import co.early.fore.kt.core.delegate.DebugDelegateDefault
import co.early.fore.kt.core.delegate.Fore
import co.early.n8.N8
import co.early.n8.Navigation
import co.early.n8.NavigationModel
import co.early.n8.NavigationState
import co.early.n8.RestrictedNavigation
import co.early.n8.lowlevel.LowLevelApi
import co.early.n8.lowlevel._mutateNavigation
import co.early.n8.notEndNode
import foo.bar.n8.feature.ViewStateFlagModel
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId
import java.util.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

/**
 * Copyright © 2015-2024 early.co. All rights reserved.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Fore.setDelegate(DebugDelegateDefault("N8_"))
        }


        val n8 = NavigationModel<Location, TabHostId>(
            homeLocation = Location.Home,
            stateKType = typeOf<NavigationState<Location, TabHostId>>(),
            dataDirectory = filesDir
        )
        // example custom mutation
        n8.installInterceptor("limitNavGraphSizeTo5Backs") { _, new ->
            if (new.backsToExit > 5) {
                when (new.navigation.topItem().child!!) { // top item is never an EndNode, so the child of the top item is never nul
                    is RestrictedNavigation.NotEndNode.IsBackStack -> {
                        _mutateNavigation(
                            oldItem =,
                            newItem =
                        )
                    }
here
                    is RestrictedNavigation.NotEndNode.IsTabHost -> {
                        _mutateNavigation(
                            oldItem =,
                            newItem =
                        )
                    }
                }
            } else {
                new
            }
        }.installInterceptor("logger") { old, new ->
            Fore.getLogger().i(
                tag = "N8-INTERCEPT",
                message = "old backsToExit:${old.backsToExit} new backsToExit:${new.backsToExit}"
            )
            new
        }

        N8.setNavigationModel(n8)


        val viewStateFlagModel = ViewStateFlagModel()
        // or use your DI of choice
        dependencies[ViewStateFlagModel::class] = viewStateFlagModel
    }


    companion object {

        private val dependencies = HashMap<KClass<*>, Any>()

        @Suppress("UNCHECKED_CAST")
        operator fun <T : Any> get(model: KClass<T>): T = dependencies[model] as T
    }
}
