package foo.bar.n8

import android.app.Application
import co.early.fore.kt.core.delegate.DebugDelegateDefault
import co.early.fore.kt.core.delegate.Fore
import co.early.n8.N8
import co.early.n8.NavigationModel
import co.early.n8.NavigationState
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId
import kotlin.reflect.typeOf

/**
 * Copyright © 2015-2024 early.co. All rights reserved.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Fore.setDelegate(DebugDelegateDefault("n8_"))
        }

        val n8 = NavigationModel<Location, TabHostId>(
            homeLocation = Location.Home,
            stateKType = typeOf<NavigationState<Location, TabHostId>>(),
            dataDirectory = filesDir
        )
        N8.setNavigationModel(n8)
    }
}
