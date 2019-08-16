# StageHostSelector
[![](https://jitpack.io/v/magdevelopment/StageHostSelector.svg)](https://jitpack.io/#magdevelopment/StageHostSelector)

Tool for using custom stage host in projects with **Retrofit & OkHttp**

## How to include
```gradle
dependencies {
    implementation 'com.github.magdevelopment:StageHostSelector:{latest_version}'
}
```

## How to use
1. Somewhere on login screen create, init and add view:
```kotlin
if (BuildConfig.USES_DEV_FEATURES) {
    val view = StageHostSelectorView(this)  
    view.defaultHostUrl = BuildConfig.API_ENDPOINT
    rootLayout.addView(view)
}
```

2. When creating OkHttp's client add interceptor:
```kotlin
val clientBuilder = OkHttpClient.Builder()
...
if (BuildConfig.USES_DEV_FEATURES) {
    clientBuilder.addInterceptor(StageHostSelectorInterceptor(appContext))
}
...
clientBuilder.build()
```
