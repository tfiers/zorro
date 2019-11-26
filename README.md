# Zoro: a [Zotero](https://www.zotero.org/) client for Android

Status: initial development.

## Contributing
Contributions are welcome.
To start hacking on the code, you first need a basic understanding of it:

## Understanding the code

The code is based on the
[recommended Jetpack architecture](https://developer.android.com/jetpack/docs/guide)
and uses most
[Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
(view binding, ViewModels and data binding, observable LiveData, a local database with Room,
task scheduling with WorkManager, data abstraction through a Repository, navigation
with NavController and SafeArgs, and [Kotlin](https://kotlinlang.org/) niceness such as
coroutines and delegated properties).

It also uses [Timber](https://github.com/JakeWharton/timber) for logging;
[Retrofit](https://square.github.io/retrofit/) as HTTP client;
[Moshi](https://github.com/square/moshi) as JSON parser;
and ConstraintLayout, RecyclerView, and [Material Design](https://material.io/) for its UI. 

If you don't have experience with these (or with Android development in general), **I heartily recommend the
[Android Kotlin Fundamentals Course](https://codelabs.developers.google.com/android-kotlin-fundamentals/),
which teaches all of these.**

Additionally, the following dependencies are used (these are not taught in the mentioned course):
- [OkHttp](https://square.github.io/okhttp/) (on which Retrofit is built), to insert headers
  in all HTTP requests.
- [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP), a backport of Java's new `util.time.*`,
  to support older Android versions.
