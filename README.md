# Zoro: a [Zotero](https://www.zotero.org/) client for Android

Status: initial development.

The code is based on the
[recommended Jetpack architecture](https://developer.android.com/jetpack/docs/guide)
and uses most
[Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
(ViewModels and data binding, observable LiveData, local database with Room, 
task scheduling with WorkManager, data abstraction through a Repository, navigation
with NavController and SafeArgs, and Kotlin niceness such as coroutines and delegated properties).

It also uses [Timber](https://github.com/JakeWharton/timber) for logging;
[Retrofit](https://square.github.io/retrofit/) and [OkHttp](https://square.github.io/okhttp/) as HTTP client;
[Moshi](https://github.com/square/moshi) as JSON parser;
and ConstraintLayout, RecyclerView, and [Material Design](https://material.io/) for its UI. 

If you don't have experience with these (or with Android development in general), I heartily recommend the
[Android Kotlin Fundamentals Codelabs](https://codelabs.developers.google.com/android-kotlin-fundamentals/),
which teaches all of these.
