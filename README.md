## n8

state based navigation library 

[![circleci](https://circleci.com/gh/erdo/n8.svg?style=svg)](https://circleci.com/gh/erdo/n8)

** WIP if you want to help, open an issue and submit a PR! **

- pure kotlin
- low config
- minimally coupled
- type safe

``` kotlin
implementation("co.early.n8:n8-core:0.1.0")
implementation("co.early.n8:n8-compose:0.1.0")
```

### Usage

(But see the little sample app and unit tests for the full picture)

It's not necessary to specify navigation routes upfront, n8 just builds the navigation graph 
as you go, ensuring that back operations always make sense. These are the main functions your code
needs to call to navigate around the app:
``` kotlin
n8.navigateTo()
n8.switchToTab()
n8.navigateBackTo()
n8.navigateBack()
```
To use n8 in your app, you don't need to implement any special interfaces on your screens, so your
UI code remains largely independent of n8 itself.

You do need to tell n8 what class you are using to keep track of your user's *Location* and your
*TabHosts* - something like a sealed class works well here, but there is nothing stopping you from
using String. If you don't have any TabHosts, just put Unit

Here's an example:

``` kotlin
@Serializable
sealed class Location {

    @Serializable
    data object Home : Location()
    
    @Serializable
    data object SignIn : Location()
    
    @Serializable
    data object Feed : Location()
    
    @Serializable
    data object Upload : Location()
    
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

Tell n8 what classes you decided on like this:

``` kotlin
val n8 = NavigationModel<Location, TabHostId>(
    homeLocation = Home,
    stateKType = typeOf<NavigationState<Location, TabHostId>>(),
    dataDirectory = application.filesDir
)
```

The navigationModel is observable so that your code can remain informed of any navigation
operations as they happen, but for Compose there is a wrapper that does this whilst handling
all the lifecycle issues for you. To use the wrapper, first set the navigation model as follows:

``` kotlin
N8.setNavigationModel(n8)

```
Then add the n8 navigation host, and your compose code will be updated whenever the navigation
state changes (i.e. your user has navigated forward or pressed back)

``` kotlin
setContent {
    AppTheme {
        ...
        N8Host<Location, TabHostId> { navigationState ->
        
            val location = navigationState.currentPage()

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

### Persistence

Whichever classes you chose to use to represent your Locations and TabHosts, make sure they are
serializable and n8 will take care of persisting the user's navigation graph for you locally

Note this line in the constructor above: ```typeOf<NavigationState<Location, TabHostId>>()```
that's how n8 can serialise and persist the navigation state across rotations or sessions, but
using the classes that you give it. It's very important, and can't be verified by the compiler,
n8 will let you know if it's wrong though, either at construction, or the first time you try to 
add a TabHost.

---

### Under the hood (advanced use)

If you want to know how all this is working, the first thing to get to grips with is probably the
underlying data structure used to represent the state of the navigation at any point in time.

The navigation graph is represented as a tree structure, when n8 logs it's state, it logs this
tree structure from top-left to bottom-right, like a file explorer. The first item is drawn at the
top-left location, and represents the
entry into the app, and as the user navigates to different locations in the app, the tree structure
grows down and right as locations are added in such a way that the user can always find their way
back to the home location and ultimately to the "exit", by continually pressing back.

The "current" location represents the screen the user is currently on and is typically towards the
bottom right of the graph.

There are a few utility functions that will let you create standalone navigation graphs for use in
unit tests or constructing deep links etc:
``` kotlin
backStackOf()
tabHostOf()
endNodeOf()
```
Mostly when using the utility functions to construct standalone navigation graphs, the compiler
will be able to work the Location/TabHost classes out for you, but if not you can specify
them like this:
``` kotlin
backStackOf<MyLocationClass, MyTabHostClass>(
    endNodeOf(Welcome)
)
```
Here's the state of a very simple linear navigation graph showing that the user entered on the
London screen, and is currently on the Tokyo screen:
``` kotlin
backStackOf<Location, Unit>(
    endNodeOf(London),     <--- home location
    endNodeOf(Paris),
    endNodeOf(Tokyo),     <--- current location
)
```
To exit the app in this case, the user would have to press back 3 times

Here's a more complicated nested navigation graph:
``` kotlin
backStackOf(
    endNodeOf(Welcome),     <--- home location
    tabsOf(
        selectedTabHistory = listOf(0,2),
        tabHostId = "TABHOST_MAIN",
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
                selectedTabHistory = listOf(0),
                tabHostId = "TABHOST_SETTINGS",
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
related to the selectedTabHistory list

Each node of the navigation graph is a Navigation item, and as you've probably noticed from the
examples, a Navigation item can be one of 3 types:

#### 1. BackStack
A list of other Navigation items. The first item is the one closest to the exit, the last item is
either: the currentItem or the first one the user will hit on their way back out of the app. A
BackStack can contain EndNodes or TabHosts (but can not directly contain other BackStacks)
#### 2. EndNode
Contains a single location only. The currentItem is always an EndNode. An EndNode is always
contained inside a BackStack.  The very top left of the Navigation graph nearest the exit is never
an unwrapped EndNode (it will always be found inside a BackStack, and sometimes that will be inside
a TabHost)
#### 3. TabHost
Contains a list of BackStacks only (each Tab is represented as a BackStack). A TabHost cannot
directly contain either EndNodes or other TabHosts

### Diagnosing issues

If some n8 behaviour is confusing, it can be helpful to print out the current state of the
navigation graph. ```navigationModel.toString(diagnostics = false)``` will give you an output
similar to the examples shown above. The outputs are deliberately formatted to be copy-pasteable
directly into your kotlin code so you can re-create the graph for further experimentation.
```navigationModel.toString(diagnostics = true)``` will display parent and child
relationships, that's useful for library developers diagnosing issues or clients
implementing custom mutations

#### TabHosts
TabHosts can be nested arbitrarily and are identified by an id. Changing tabs by specifying a
tabIndex, adding a brand new TabHost at the user's current location, optionally clearing the tab's
history when the user selects it, or breaking out of a TabHost completely and continuing on a tab
from a parent TabHost is all supported with the same functions

```navigationModel.navigateTo(location = OptionsScreen)```
// continue on whatever tab you are on (if any)

```navigationModel.navigateTo(location = EuropeScreen) { "TAB_HOST_CONTINENT" }```
// break and continue on the TabHost parent identified by TAB_HOST_CONTINENT

```navigationModel.navigateTo(location = SignOutScreen) { null }```
// break and continue on the top level navigation (which may be a TabHost or a plain BackStack)

##### Structural v Temporal
TabHosts tend to treat the back operation in one of 2 different ways. n8 calls these two modes
"Structural" and "Temporal".

By Structural we mean something akin to the old "up" operation in android. Let's say you have an
app that contains a single TabHost with 3 tabs, let's say the user has built up a history on this
TabHost by selecting all 3 tabs in turn, but hasn't navigated to any new locations within these
tabs. Structural back navigation here would mean that when the user presses back, they would
immediately exit the app

By Temporal we mean something more like a time based history. Let's take the example from above, an
app that contains a single TabHost with 3 tabs, again the user has built up a history on this
TabHost by selecting all 3 tabs in turn, but hasn't navigated to any new locations within these
tabs. Temporal back navigation here would mean that when the user presses back, they would cycle
back through the tabs they had previously visited, and only then exit the app. By default n8 only
stores each tab once in the navigation history for a tabHost so this user would need to press back
3 times in order to exit the app

##### DeepLinking
The current state of the navigation is always exportable/importable. In fact the whole state is
serialized and persisted to local storage at each navigation step. You can take this serialized
state, send the String to someone in a message, import it into their app and thus transfer an entire
navigation state to another phone.

For deep linking you probably want to construct a more simple navigation state which is easy to do
with the helper functions, for example:
``` kotlin
backStackOf<Location, Unit>(
    endNodeOf(HomeScreen),
    endNodeOf(ProductReviews),
    endNodeOf(Review(productId=7898)),
).export()
```
The default serialized state is human readable, but not that pretty, and you might want to
encode/decode it before sending it to your users, but that's outside the scope of a navigation
library

##### Custom Navigation behaviour

n8 tries to make standard navigation behaviour available to your app using basic functions by
default, but you can implement any behaviour you like by writing a custom state mutation yourself.

The n8 navigation state is immutable, but internally it also has parent / child relationships that
go in both directions and most of the mutation operations involve recursion, so it's definitely an
advance topic, but there are mutation helper functions that n8 uses internally and that are
available for client use too which should make life easier.

### Example custom mutation

//TODO

## Example App

![example app screenshot](exampleapp.png)

There is a mini android app in this repo if you want something to play around with

### Some ideas for what to do next

- improve / simplify code comments & docs

- finish implementing code functions

- review API - can it do everything a reasonable client will need it to?
- review API - can it be further simplified
- review API - what can be made internal?
- review API - what should be made public?

- finish a simple example app

- setup circle CI to run unit tests

- look for / fix bugs

- settle on solution for transition animations (before and after state - possibly keep this in the compose lib)

- kmp example app + lib changes if needed

- formalise code formatter / linter

- code coverage for tests

- write an article + sample app

- release v1.0.0


## License

    Copyright 2015-2024 early.co

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
