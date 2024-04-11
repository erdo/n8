package co.early.n8

class N8 {
    companion object {
        private lateinit var navigationModel: NavigationModel<*, *>
        fun <L : Any, T : Any> setNavigationModel(n8: NavigationModel<L, T>) {
            this.navigationModel = n8
        }

        @Suppress("UNCHECKED_CAST")
        fun <L : Any, T : Any> n8(): NavigationModel<L, T> {
            return navigationModel as NavigationModel<L, T>
        }
    }
}
