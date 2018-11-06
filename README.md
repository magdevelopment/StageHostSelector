# StageHostSelector

Tool for using custom stage host in projects with **Retrofit & OkHttp**

## How to include
*In progress*

## How to use
1. Somewhere on login screen create, init and add view:
```kotlin
if (BuildConfig.USES_DEV_FEATURES) {
    val urlView = StageHostSelectorView(requireContext())
    urlView.init(BuildConfig.API_ENDPOINT, supportFragmentManager)
    appBarLayout.addView(urlView)
}
```

2. When creating OkHttp's client add interceptor:
```kotlin
val clientBuilder = OkHttpClient.Builder()
...
if (BuildConfig.USES_DEV_FEATURES) {
    clientBuilder.addInterceptor(StageHostSelectorInterceptor(context))
}
...
clientBuilder.build()
```
