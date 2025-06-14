## N8 [![circleci](https://circleci.com/gh/erdo/n8.svg?style=svg)](https://circleci.com/gh/erdo/n8)

![n8_logo](n8_logo_400h.png)

*Goals of N8 navigation:* _*pure kotlin, low config, minimally coupled, type safe and have an obvious API*_

(obviously it also doesn't loose the user's location on config change or process death)

⚠️help welcomed 🙏(check the issues) ⚠️

- There are two sample apps in the repo that will make things clearer: one Android, one KMP(android/ios)
- There are also a large number of unit tests which define N8 behaviour, and log navigation state to the console (which is quite useful to see what is going on)

![example app screenshot landscape view](example-android-app/screenshot-land.png)

### Quick Start

See the [dev.to launch post](https://dev.to/erdo/ive-just-open-sourced-n8-4foe) for an intro

``` kotlin
implementation("co.early.n8:n8-core:2.0.0-rc.2")
implementation("co.early.n8:n8-compose:2.0.0-rc.2")
```
GPG fingerprint (for optionally verifying the Maven packages): <strong>5B83EC7248CCAEED24076AF87D1CC9121D51BA24</strong> see repo root for the public certificate.

_Note: a legacy or hybrid android app that still uses fragments or multiple activities, can't maintain its back stack in the same stateful manner as a 100% compose app can and therefore won't get much utility from N8_

### Details

It's not necessary to specify navigation routes upfront, N8 just builds the navigation graph
as you go, ensuring that back operations always make sense (you are in complete control of the 
navigation graph and can re-write it as you wish). These are the main functions your code
needs to call to navigate around the app: ```navigateTo(), navigateBack(), navigateBackTo(), switchTab()```

``` kotlin
n8.navigateTo(Paris)
n8.navigateTo(NewYork)
n8.navigateTo(Mumbai)
n8.navigateBack() { /* with optional data */ }

n8.switchTab(MainTabs) /* add MainTabs if not yet added */
n8.navigateTo(Seoul)  /* continue in MainTabs */
n8.switchTab(2)
n8.navigateTo(Hanoi(holdBags = 2)) /* navigate to Hanoi with data */

n8.switchTab(SettingsTab) /* add SettingsTab, nested inside MainTabs */
n8.switchTab(1)  /* continue in SettingsTab, from inside tabIndex 2 of MainTabs */
n8.switchTab(MainTabs, 0)  /* switch to tabIndex 0 of MainTabs */

n8.navigateTo(London) { null } /* jump out of any nested tabhosts and continue at the top level */
n8.navigateTo(Krakow)
n8.navigateTo(Tokyo) { SettingsTab } /* continue back in SettingsTab at tabIndex 1/ */

n8.navigateBackTo(NewYork) { /* with optional data */ }

```

To use N8 in your app, you don't need to implement any special interfaces on your screens, so your
UI code remains largely independent of N8 itself.

You do need to tell N8 what class you are using to keep track of your user's *Location* and your
*TabHosts*, whatever you choose needs to have sensible equals() and hashcode() implementations for 
Kotlin and be Hashable in Swift - something like a sealed class works well here. If you don't have
any tabbed navigations you can just put Unit for the TabHost class, but read the note for KMP:

**KMP** Unit is not Hashable in Swift, but at the moment KMP translates that to KotlinUnit which is 
Hashable (If that changes you'll have to choose something more class like or implement the Hashable 
bits yourself)

Here's are some examples. "Location" and "TabHostId" are your own class and nothing to do with N8 code, you
could call them "CosmicGirl" and "Loquat" if you wanted

``` kotlin
@Serializable
sealed class Location {

    @Serializable
    data object NewYork : Location()
    
    @Serializable
    data object Tokyo : Location()
    
    @Serializable
    data object Paris : Location()
   
}
```
Or perhaps slightly more realistically:

``` kotlin
@Serializable
sealed class Location {

    @Serializable
    data object Home : Location()
    
    @Serializable
    data object SignIn : Location()
    
    @Serializable
    data class ProductPage(val productId: Int) : Location()
    
    @Serializable
    data object Feed : Location()
    
    @Serializable
    sealed class SignUpFlow : Location() {
        @Serializable
        data object Details : SignUpFlow()

        @Serializable
        data object Email : SignUpFlow()

        @Serializable
        data object EmailConf : SignUpFlow()
    }

    @Serializable
    sealed class Settings : Location() {
        @Serializable
        data object VideoSettings : Settings()

        @Serializable
        data object AudioSettings : Settings()
    }
}

@Serializable
sealed class TabHostId {

    @Serializable
    data object MainTabs : TabHostId()

    @Serializable
    data object SettingsTabs : TabHostId()

    @Serializable
    data object CustomerSupportTabs : TabHostId()
}
```

Tell N8 what classes you decided on, like this on android:

``` kotlin
val n8 = NavigationModel<Location, TabHostId>(
    homeLocation = Home,
    stateKType = typeOf<NavigationState<Location, TabHostId>>(),
    dataPath = application.filesDir.toOkioPath(),
)
```
(For KMP the dataPath differs based on platform, see the KMP example app in this repo)

The navigationModel is observable so that your code can remain informed of any navigation
operations as they happen, but for Compose there is a wrapper that does this whilst handling
all the lifecycle issues for you. To use the wrapper, first set the navigation model as follows:

``` kotlin
N8.setNavigationModel(n8)
```

Then add the N8 navigation host, and your compose code will be updated whenever the navigation
state changes (i.e. your user has navigated forward or pressed back)

``` kotlin
setContent {
    AppTheme {
        ...
        N8Host { navigationState ->
        
            val location = navigationState.currentLocation()

            // the rest of your app goes here, this code runs
            // whenever your user's location changes as a
            // result of a navigation operation
            
            ModalNavigationDrawer(
                drawerContent = ...
                content = ...
            )
        }
    }
}
```

Pass the N8 instance around the app using your choice of DI, or access it directly like this:

``` kotlin
val n8 = N8.n8()
```

In Compose style, you can also access the current navigation state from within N8Host scope:

``` kotlin
val navigationState = LocalN8HostState
```

Or like this, it's the same immutable state:

``` kotlin
val navigationState = n8.state
```

Call the navigation functions from within ClickListeners / ViewModels / ActionHandlers etc as
appropriate for your architecture

``` kotlin
onClick = {
  n8.navigateTo(Paris)
}
```

### Custom Navigation behaviour

N8 tries to make standard navigation behaviour available to your app using basic functions by
default, but you can implement any behaviour you like by writing a custom navigation mutation
yourself.

The N8 navigation state is immutable, but internally it also has parent / child relationships that
go in both directions and most of the mutation operations involve recursion, so it's definitely an
advance topic, but there are mutation helper functions that N8 uses internally and that are
available for client use too (these are the functions that start with an underscore and are marked
LowLevelApi - and they come with a warning! it's much easier to misuse these functions than the
regular API)

There is an example in the sample app in CustomNavigationExt.kt and more information about N8's
data structure below (which should be considered required reading before attempting to write a
custom navigation mutation)

### Interceptors

Custom navigation mutations are hooked in using N8's interceptor API which looks like Ktor's
plugin API so you can add or remove multiple interceptors for things like custom navigation
mutations, logging, or analytics

``` kotlin
n8.installInterceptor("someCustomNavigationBehaviour") { old, new ->
    new.copy(
        navigation = someCustomNavigationBehaviour(new.navigation),
    )
}.installInterceptor("logging") { old, new ->
    Log.i("navigation", "old backsToExit:${old.backsToExit} new backsToExit:${new.backsToExit}")
    new
}
```

### Back Handling

System back operations are intercepted at the n8-compose package level (i.e. it's not a part
of the core navigation code in n8-core). This is exposed to client apps before any back operation
is applied so that they can be blocked as required (for example to display a confirmation to the
user). The example app implements a confirmation dialog before the app is exited

``` kotlin
setContent {
    AppTheme {
        ...
        N8Host(onBack = backInterceptor() ) { navigationState ->
            ...
        }
    }
}
```

onBack() is passed the navigation state before back is applied, and is a suspend function to allow 
clients to seek user input before returning.

``` kotlin
onBack: (suspend (NavigationState<L, T>) -> Boolean)? = null, // true = handled/blocked/intercepted
```

Returning true means the back has been handled, false means it hasn't and n8 should proceed with 
the back operation as usual

### Persistence

Whichever classes you chose to use to represent your Locations and TabHosts, make sure they are
serializable and N8 will take care of persisting the user's navigation graph for you locally

Notice this line in the constructor:

``` kotlin
typeOf<NavigationState<Location, TabHostId>>()
```

that's how N8 can serialise and persist your navigation state across rotations or sessions without
knowing anything about the class you chose for Location or TabHostId in advance. That line is very
important, but it can't be verified by the compiler unfortunately. N8 will let you know if it's
wrong though, either at construction, or the first time you try to add a TabHost (if one wasn't
added during construction).

### DeepLinking

The current state of the navigation is always exportable/importable. In fact the whole state is
serialized and persisted to local storage at each navigation step. You can take this serialized
state, send the String to someone in a message, import it into their app and thus transfer an 
entire navigation state to another phone.

For deep linking you probably want to construct a custom navigation state, which is easy to do
with the helper functions, for example:

``` kotlin
n8.export(
    backStackOf<Location, Unit>(
        endNodeOf(HomeScreen),
        endNodeOf(ProductReviews),
        endNodeOf(Review(productId=7898)),
    )
)
```

The default serialized state is human readable, but not that pretty:

``` kotlin
backStackOf<com.foo.bar.Location, Unit>(endNodeOf(com.foo.bar.Location.HomeScreen),endNodeOf(com.
foo.bar.Location.ProductReviews), endNodeOf(com.foo.bar.Location.Review(productId=7898)),)
```

especially once URLEncoded:

```
backStackOf%3Ccom.foo.bar.Location%2C%20Unit%3E%28endNodeOf%28com.foo.bar.Location.HomeScreen%
29%2CendNodeOf%28com.%0Afoo.bar.Location.ProductReviews%29%2C%20endNodeOf%28com.foo.bar.Locati
on.Review%28productId%3D7898%29%29%2C%29
```

So you might want to encode/decode as you wish before sending it to your users, but that's outside
the scope of a navigation library.

Anything more than a very small navigation graph can be quite verbose and I've found that tokenizing
the serialised data before trying compression techniques like Zstd or Brotli makes a big difference.

There's a basic example of using a tokens map for this in NavigationImportExportTest.kt Having said
that most deep links have a shallow navigation hierarchy so it might be a non issue for you

### Passing data

What data which locations accept, is defined by you. Here the location Sydney takes an optional
withSunCreamFactor parameter
``` kotlin
@Serializable
data class Sydney(val withSunCreamFactor: Int? = null) : Location()
```
So if you want to navigate to the Sydney location, with factor 30 sun cream, you can just do this:
``` kotlin
navigationModel.navigateTo(Sydney(30))
```
That data will be available attached to the location. You can access it wherever you are picking up
the location changes in your code (your Compose UI usually). It will also be persisted along with
the rest of the navigation graph, so there is no way to loose it by rotating the screen or
quitting the app, it becomes part of the graph and will still be there when you navigate back.

Quite often you will want to collect some user data on a screen and then pass that data back to
a previous location, here's how to do that:

``` kotlin
navigationModel.navigateTo(Sydney())
navigationModel.navigateTo(SunCreamSelector)
navigationModel.navigateBack(
    setData = {
        when (it) {
            is Sydney -> {
                it.copy(withSunCreamFactor = 50)
            }

            else -> it
        }
    }
)
```

### Data Structure

If you want to know how all this is working, the first step is to understand the
underlying data structure used to represent the state of the navigation at any point in time.

The navigation graph is represented as an immutable tree structure, when N8 logs its state, it logs
that tree structure from top-left to bottom-right, like a file explorer.

The first item is drawn on the top-left, and represents the entry into the app. As the user navigates 
to different locations, the tree structure grows down and right as locations are added in such a way 
that the user can always find their way back to the home location and ultimately to exit the app 
by continually pressing back.

We call this first item (or the last item before exit as a user navigates back) the "home" location. 
The home location of a nav graph is not necessarily where the user entered because the graph can
be arbitrarily re-written. Or the location where the user entered may have been some kind of intro
screen and not have been kept in the navigation graph in the first place (using `willBeAddedToHistory = false`)

The "current" location represents the screen the user is currently on and is typically towards the
bottom right of the graph.

Here's the state of a very simple linear navigation graph showing that the user's home location is the
London screen, and is currently on the Tokyo screen:

``` kotlin
backStackOf<Location, Unit>(
    endNodeOf(London),     <--- home location
    endNodeOf(Paris),
    endNodeOf(Tokyo),     <--- current location
)
```

To exit the app in this case, the user would have to press back 3 times
(Tokyo -> Paris -> London -> [exit])

There are a few utility functions that will let you create standalone navigation graphs for use in
unit tests or constructing deep links etc:

``` kotlin
backStackOf()
tabsOf()
endNodeOf()
```

Mostly when using the utility functions to construct standalone navigation graphs, the compiler
will be able to work out the Location/TabHostId classes for you, but if not you can specify
them like this:

``` kotlin
backStackOf<MyLocationClass, MyTabHostIdClass>(
    endNodeOf(Welcome)
)
```

Here's a more complicated nested navigation graph:

``` kotlin
backStackOf(
    endNodeOf(Welcome),     <--- home location
    tabsOf(
        tabHistory = listOf(0,2),
        tabHostId = TABHOST_MAIN,
        backStackOf(
            endNodeOf(MyFeed),
            endNodeOf(Trending),
        ),
        backStackOf(
            endNodeOf(Subscriptions),
        ),
        backStackOf(
            endNodeOf(MyAccount),
            endNodeOf(Settings),
            tabsOf(
                tabHistory = listOf(0),
                tabHostId = TABHOST_SETTINGS,
                backStackOf(
                    endNodeOf(Audio),
                    endNodeOf(Dolby),     <--- current location
                ),
                backStackOf(
                    endNodeOf(Video),
                )
            )
        ),
    )
)
```

To exit the app in this case, the user would have to press back 7 times, can you work out why? it's
related to the tabHistory list

Each node of the navigation graph is a Navigation item, and as you've probably noticed from the
examples, a Navigation item can be one of 3 types:

#### 1. EndNode

Contains a single location only. The currentItem is always an EndNode. An EndNode is always
contained inside a BackStack. The very top left of the Navigation graph nearest the exit is never
an unwrapped EndNode (it will always be found inside a BackStack, and sometimes that will be inside
a TabHost)

``` kotlin
endNodeOf(MyAccount)
```

#### 2. BackStack

A list of other Navigation items. The _first_ item is the one closest to the exit. And for a simple
navigation graph with no TabHosts, the _last_ item is the current item. A BackStack can contain
EndNodes or TabHosts (but can not directly contain other BackStacks)

``` kotlin
backStackOf(
    endNodeOf(MyAccount),
    endNodeOf(Settings),
    tabsOf(...)
)
```

#### 3. TabHost

Contains a list of BackStacks only (each Tab is represented as a BackStack). A TabHost cannot
directly contain either EndNodes or other TabHosts

``` kotlin
tabsOf(
    tabHistory = listOf(0),
    tabHostId = TABHOST_SETTINGS,
    backStackOf(
        endNodeOf(AudioSettings),
    ),
    backStackOf(
        endNodeOf(VideoSettings),
    )
)
```

#### Logging the state

If some N8 behaviour is confusing, it can be helpful to print out the current state of the
navigation graph.

``` kotlin
n8.toString(diagnostics = false)
```

will give you an output similar to the examples shown above. The outputs are deliberately formatted
to be copy-pasteable directly into your kotlin code with only minor changes so you can re-create
the graph for further experimentation.

``` kotlin
n8.toString(diagnostics = true)
```

will display parent and child relationships, that's useful for library developers diagnosing
issues or clients implementing custom mutations

### TabHost navigation

TabHosts can be nested arbitrarily and are identified by an id. Changing tabs by specifying a
tabIndex, adding a brand new nested TabHost at the user's current location, optionally clearing 
the tab's history when the user selects that tab, or breaking out of a TabHost completely and 
continuing on a tab from a parent TabHost is all supported with the same functions:

``` kotlin
/**
 * to continue in whatever tab you are on (if any)
 *
navigationModel.navigateTo(Madrid)
```

``` kotlin
/**
 * to break out of the current TabHost (TAB_HOST_SETTINGS say) and continue in
 * the TabHost parent identified by TAB_HOST_MAIN
 *
navigationModel.navigateTo(Tokyo) { "TAB_HOST_MAIN" }
```

``` kotlin
/**
 * to break out of the current TabHost (TAB_HOST_SETTINGS say) and continue in
 * the top level navigation (which may be a TabHost or a plain BackStack)
 *
navigationModel.navigateTo(location = SignOutScreen) { null }
```

##### Structural v Temporal tabs

TabHosts tend to treat the back operation in one of 2 different ways. N8 calls these two modes
"Structural" and "Temporal".

By Structural we mean something akin to the old "up" operation in android. Let's say you have an
app that contains a single TabHost with 3 tabs, let's say the user has built up a history on this
TabHost by selecting all 3 tabs in turn, but while they have been on the current tab, has
only navigated to 2 new locations.

The navigation graph might look like this:

``` kotlin
tabsOf(
    tabHistory = listOf(2),
    tabHostId = "TABHOST_MAIN",
    backStackOf(
        endNodeOf(Houston),
        endNodeOf(Tokyo),
    ),
    backStackOf(
        endNodeOf(Paris),
        endNodeOf(Sydney),
    ),
    backStackOf(
        endNodeOf(London),
        endNodeOf(Mumbai),
        endNodeOf(Shanghai), <--- current location
    ),
)
```

**Structural** back navigation here would mean that when the
user presses back, they would visit the previously visited locations in the current tab only, and then
exit the app (so in the above example, 3 clicks back to exit: Shanghai -> Mumbai -> London -> exit)

By **Temporal** we mean something more like a time based history. Let's take the example from above, a
temporal version might look like this:

``` kotlin
tabsOf(
    tabHistory = listOf(1,0,2),
    tabHostId = "TABHOST_MAIN",
    backStackOf(
        endNodeOf(Houston),
        endNodeOf(Tokyo),
    ),
    backStackOf(
        endNodeOf(Paris),
        endNodeOf(Sydney),
    ),
    backStackOf(
        endNodeOf(London),
        endNodeOf(Mumbai),
        endNodeOf(Shanghai), <--- current location
    ),
)
```

In this case when the user presses back, they would re-trace their steps through the locations
visited while on ```tabIndex = 2```, and then do the same for ```tabIndex = 0```, and then
```tabIndex = 1```, before finally exiting the app.

So in our example, that would take 7 clicks back to exit:
Shanghai -> Mumbai -> London -> Tokyo -> Houston -> Sydney -> Paris -> [exit]

Note that N8 implements those two modes using only the **tabHistory** field.

You can set the TabBackMode via the ```switchTab()``` function. The default
is ```TabBackMode.Temporal```


### Nomenclature: Back path vs Navigation graph
**Structural** TabHosts introduce the possibility for locations to be present in the navigation
graph which are not reachable by only making back operations (they are still reachable by switching
tabs etc).

In the Structural example above, the *back path* looks like this: Shanghai -> Mumbai -> London

But the full *navigation graph* also contains the locations: Houston, Tokyo, Paris, Sydney


## License

    Copyright 2015-2025 early.co

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
