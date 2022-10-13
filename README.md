#MovieBrowser

## Build tools & versions used
I used Gradle version 4.2.1 and Kotlin 1.6.1

Other dependencies:
* Android Architecture Components + Jetpack (Navigation Components, Lifecycle, etc)
* Kotlin Coroutines + Flow
* Dagger
* Retrofit + Gson + Okhttp
* Glide

Import in Android Studio Chipmunk > Build > Run in either emulator or device

## Main components of app

My strengths are currently in architecture/infrastructure work.
That's why I tried to keep my code clean, extensible, reusable, and manage dependencies and abstractions.

Elements of my strategy:

* MVVM architecture and Kotlin coroutines for lifecycle management and scoping
* MVI/Redux-style immutable view states to prevent "bad" states
* Repository class to manage api calls and data transformations, caching, etc.
* Dagger to manage dependencies
* Glide to manage image loading and potentially image caching in the future

Important Classes:

MainViewModel:
* emits UIStates with all data necessary to render the state, and consumes user events
* observes data stream emitted by Repository class, then maps it to LiveData for views
* scopes all network calls to its lifecycle to prevent memory leaks
* maintains in-memory livedata of user data so that configuration change or app background/foreground doesn't reload data
* is scoped to the lifecyle of MainFragment instead of Activity
(right now only MainFragment needs access to data)

MainFragment:
* Consumes MainViewModel's state and emits user events
* Manages the adapter

There is unidirectional data flow between the ViewModel (which manages business logic) and the
MainFragment (which renders views and manages UI events)

MainActivity:
* Hosts NavHostFragment

MovieRepository:
* Gets configuration information so that movie objects can construct the image url they need at runtime
* Manages the network fetch for movies and search results
* Exposes StateFlow of api response for any observers (in this case, MainViewModel)

