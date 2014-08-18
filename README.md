contentful.java
===============

[![Build Status](http://img.shields.io/travis/contentful/contentful.java.svg?style=flat)](https://travis-ci.org/contentful/contentful.java/builds#)

Java SDK for [Contentful's][1] Content Delivery API.

[Contentful][1] is a content management platform for web applications, mobile apps and connected devices. It allows you to create, edit & manage content in the cloud and publish it anywhere via powerful API. Contentful offers tools for managing editorial teams and enabling cooperation between organizations.

Setup
=====

<!--Download the [latest JAR][2] or grab via Maven:-->
Grab via Maven:
```xml
<dependency>
  <groupId>com.contentful.java</groupId>
  <artifactId>java-sdk</artifactId>
  <version>1.0.0</version>
</dependency>
```
or Gradle:
```groovy
compile 'com.contentful.java:java-sdk:1.0.0'
```

### Default client

The SDK uses Retrofit(v1.6.1) under the hood as a REST client, which detects [OkHttp][5] in your classpath and uses it if it's available, otherwise falls back to the default `HttpURLConnection` (note that for Android devices running API 8 or lower, the `AndroidApacheClient` will be used).

The recommended approach would be to add [OkHttp][5] as a dependency to your project, but that is completely optional.

You can also specify a custom client to be used, refer to the [official documentation][3] for instructions.

### Proguard
```
-dontwarn rx.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keep class com.contentful.java.** { *; }
-keep class * extends com.contentful.java.model.** { *; }
-keep class com.google.gson.** { *; }
-keep class sun.misc.Unsafe { *; }
```

Usage
=====

The `CDAClient` manages all your interaction with the Contentful Delivery API.
```java
CDAClient client = new CDAClient.Builder()
        .setSpaceKey("space-key-goes-here")
        .setAccessToken("access-token-goes-here")
        .create();
```

Items can be fetched synchronously:
```java
try {
    CDAArray array = client.fetchEntriesBlocking();
    // success
} catch (Exception e) {
    // failure
}
```

Or asynchronously:
```java
client.fetchEntries(new CDACallback<CDAArray>() {
    @Override
    protected void onSuccess(CDAArray array, Response response) {
        // success
    }

    @Override
    protected void onFailure(RetrofitError retrofitError) {
        // failure
    }
});
```

Note that a `CDACallback` instance can be cancelled, so keep a reference to it in order to do so:

```java
CDACallback<CDAArray> cb;

client.fetchEntries(cb = new CDACallback<CDAArray>() {
    ...
});

cb.cancel(); // onSuccess and onFailure will not be invoked.
```

### Using Custom Entry Classes

You might want to subclass `CDAEntry` to store additional data alongside Entries or to decouple the rest of your app from the Contentful SDK's API. For this purpose, it is possible to register your own custom classes for specific Content Types, like this:

```java
client.registerCustomClass("content-type-id-goes-here", CustomEntry.class);
```

Each time, the receiver needs to create a new Entry object of the given Content Type, it will create instances of `CustomEntry`. Make sure that the class inherits from `CDAEntry`.

### Offline Support

Mobile devices will not always have a data connection, so it makes sense to cache data received from Contentful for offline use. 

The `CDAResource` base class implements the `Serializable` interface.
This means you can save / restore any kind of Resource, including `CDAArray`, via local files:

```java
// save Resource to local file:
ResourceUtils.saveResourceToFile(someEntry, new File("/path/to/save"));

// restore Resource from local file:
CDAResource resource = ResourceUtils.readResourceFromFile(new File("/path/to/restore"));
```

Note that in case you attempt to restore a previously saved Resource, if the original class does not exist the `readResourceFromFile()` method will throw a `ClassNotFoundException`. 

Documentation
=============

For further information, check out our official [JavaDoc][3] site or browse the [API documentation][4].

License
=======

Copyright (c) 2014 Contentful GmbH. See LICENSE.txt for further details.


 [1]: https://www.contentful.com
 [2]: https://oss.sonatype.org/service/local/repositories/releases/content/com/contentful/java/java-sdk/1.0.0/java-sdk-1.0.0.jar
 [3]: https://contentful.github.io/contentful.java/
 [4]: https://www.contentful.com/developers/documentation/content-delivery-api/
 [5]: https://square.github.io/okhttp/
