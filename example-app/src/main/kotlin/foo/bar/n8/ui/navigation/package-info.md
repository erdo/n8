AppTheme {

    /**
     * anything in here has access to the theme:  LocalColors.current or MaterialTheme.typography
     * etc, entire block will be recomposed if theme changes (e.g. dark mode turned on)
     */

    WindowSize {

        /**
         * anything in here has access to Fore's WindowSize classes: LocalWindowSize.current
         * if the window size changes, this entire block will be recomposed
         */

        SplashScreen {

            /**
             * this block controls the splash screen, it's observing the state of the InitModel
             * once the initState is Ready, the contents will be displayed
             */

            NavHost {

                /**
                 * this block is observing the state of the NavigationModel, any code inside here
                 * will have access to the current location and the navigation graph, we can
                 * navigate by calling navigationModel.navigateTo(SettingsScreen) from anywhere in
                 * the app, but typically we access this functionality by sending an action, UDF
                 * style: actionHandler.handle(Act.ToSettingsScreen)
                 */

                AppNavigation {

                    /**
                     * this block will be recomposed according to the state passed to it from the
                     * layers above, e.g. if the current Location changes, the menu and content
                     * will be redraw accordingly.
                     *
                     * here we specify:
                     *
                     * mainContent: @Composable (PaddingValues) -> Unit
                     * startDrawerItems: List<NavigationItem>
                     * actionItems: List<NavigationItem>
                     * bottomBarItems: List<NavigationItem>
                     * userActionHandler: ActionHandler<T>
                     */

                    ModalNavigationDrawer {
                        ModalDrawerSheet {

                            /**
                             * startDrawerItems for the slide out menu,
                             * the item matching the current location is highlighted
                             */

                        }
                        ContentScaffold {
                            Scaffold {
                                AdaptiveTopAppBar {

                                    /**
                                     * TopAppBar, adaptive according to WindowSize, includes a
                                     * list of actionItems specific to the current Location
                                     */

                                }

                                {
                                    /**
                                     * mainContent selected based on current Location
                                     **/
                                }

                                BottomNavBar {

                                    /**
                                     * NavigationBar with a list of bottomBarItems, this is
                                     * dependant on the current Location, and may not be
                                     * present at all for some locations
                                     */

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
