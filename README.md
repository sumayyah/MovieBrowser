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

## Focus and strategy

I prefer to work in the Viewmodel-Repository-data layer because it supports critical functionality,
and because making it as clean as possible means the codebase is easy to work in now and in the future.

I aim to make this layer as robust as possible so that new features, views, etc can be added (or existing
ones changed) with minimal changes. For example, if we want to add a Detail view, we can leverage the
existing Dagger setup and domain models, the same Repository, any in-memory data cache, etc.


## Improvements I would have made with more time

1) I would have added the detail view by creating a BottomSheetDialogFragment to render the view, 
passing in the id of the clicked Movie via navArgs. It would render UIStates defined by a DetailViewModel.
The DetailViewModel would have relied on the Repository's in-memory cache to instantly access Movie object by id. 
Navigation components would manage the transitions between MainFragment and DetailFragment.

2) I would have made the GridAdapter's spanCount flexible, show we can show more columns in different screen sizes

3) I would have built a more robust data management and caching layer. 
Ideally there would be an in-memory cache (ie a DAO class), a disk caching strategy, and a DataSource class that will handle: 
data synchronization between network response and cache, returning the correct data for online/offline cases, 
transforming api response to any domain model, etc. The Repository would take the DataSource class as a 
dependency and map the responses as StateFlows for any observers. 


