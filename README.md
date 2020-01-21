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
1. Add in application onCreate:
```kotlin
if (BuildConfig.USES_DEV_FEATURES) {
    StageHostSelector.init(
        context = this,
        defaultHostUrl = BuildConfig.API_ENDPOINT,
        suggestedUrls = setOf(
            "http://example.com/alternative/",
            "http://172.21.19.123:3500/",
            "http://example.com/alternative/first",
            "http://example.com:8080/alternative/first"
        )
    )
}
```

2. Somewhere on login screen create and add view:
```kotlin
val view = StageHostSelector.createView(this)
// if you are not initialized StageHostSelector, function StageHostSelector.createView(this) return null
if (view != null) appBarLayout.addView(view)
```

3. When creating OkHttp's client add interceptor:
```kotlin
import com.magdv.stagehostselector.addStageHostSelectorInterceptor

val clientBuilder = OkHttpClient.Builder()
...
clientBuilder.addStageHostSelectorInterceptor()
...
clientBuilder.build()
```
