# Zorro: a [Zotero](https://www.zotero.org/) client for Android

<br>
<img width="140px" src="logo/logo.png">

Project status: initial development.

## License

The Zorro application and its logo are Copyright © 2019 Tomas Fiers.<br>
Its source code is made available under the [AGPLv3 license](LICENSE).

## Contributing

Contributions are welcome. Follow the recommendations in [this classic blogpost](https://www.igvita.com/2011/12/19/dont-push-your-pull-requests/) to make sure your pull request gets merged, quickly.

Does the code seem too daunting to start hacking on it? No Android development experience? See the next section.


## Understanding the code

The code is based on the
[recommended Jetpack architecture](https://developer.android.com/jetpack/docs/guide)
and uses most
[Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
(view binding, ViewModels and data binding, observable LiveData, a local database with Room,
task scheduling with WorkManager, data abstraction through a Repository, navigation
with NavController and SafeArgs, and [Kotlin](https://kotlinlang.org/) niceness such as
coroutines, extension functions, and delegated properties).

It also uses [Timber](https://github.com/JakeWharton/timber) for logging;
[Retrofit](https://square.github.io/retrofit/) as HTTP client;
[Moshi](https://github.com/square/moshi) as JSON parser;
and ConstraintLayout, RecyclerView, and [Material Design](https://material.io/) for its UI. 

💡 If you don't have experience with these tools (or with Android development in general),
**I heartily recommend the
["Android Kotlin Fundamentals" course](https://codelabs.developers.google.com/android-kotlin-fundamentals/),
which teaches all of them.**

Additionally, the following dependencies are used (these are not taught in the mentioned course):
- [OkHttp](https://square.github.io/okhttp/) (on which Retrofit is built), to insert headers
  in all HTTP requests.
- [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP), a backport of Java's new `util.time.*`,
  to be able to use this great package on older Android versions.
