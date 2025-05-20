package com.kmpfoo.ui.navigation

import co.early.fore.core.delegate.Fore
import co.early.fore.core.logging.MultiplatformLogger
import co.early.n8.N8
import co.early.n8.NavigationModel
import co.early.n8.NavigationState
import okio.Path
import kotlin.reflect.typeOf

/**
 * Copyright © 2015-2025 early.co. All rights reserved.
 */
fun createNavigation(application: Any? = null) {
    val n8 = NavigationModel<Location, TabHost>(
        homeLocation = Location.Bangkok,
        stateKType = typeOf<NavigationState<Location, Unit>>(),
        dataPath = dataPath(application),
        logger = MultiplatformLogger("n8"),
        clearPreviousNavGraph = true
    ).installInterceptor("logger") { old, new ->
        Fore.getLogger().i(
            tag = "N8-INTERCEPT",
            message = "old backsToExit:${old.backsToExit} new backsToExit:${new.backsToExit} comingFrom:${new.comingFrom} current:${new.currentLocation}"
        )
        new
    }

    N8.setNavigationModel(n8)
}

fun getNavigation(): NavigationModel<Location, TabHost> {
    return N8.n8<Location, TabHost>()
}

expect fun dataPath(application: Any? = null): Path