contentful.java
===============

[![Build Status](https://travis-ci.org/contentful/contentful.java.svg)](https://travis-ci.org/contentful/contentful.java/builds#) [![Coverage Status](https://img.shields.io/coveralls/contentful/contentful.java.svg)](https://coveralls.io/r/contentful/contentful.java?branch=master)

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
  <version>2.0.0</version>
</dependency>
```
or Gradle:
```groovy
compile 'com.contentful.java:java-sdk:2.0.0'
```

The SDK requires at minimum Java 6 or Android 2.3.

### Default Client

The SDK uses Retrofit under the hood as a REST client, which detects [OkHttp][5] in your classpath and uses it if it's available, otherwise falls back to the default `HttpURLConnection`.

The recommended approach would be to add [OkHttp][5] as a dependency to your project, but that is completely optional.

You can also specify a custom client to be used, refer to the [official documentation][3] for instructions.

### Proguard
```
-keepattributes Signature
-dontwarn rx.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keep class com.contentful.java.cda.** { *; }
-keep class * extends com.contentful.java.cda.model.** { *; }
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
        .build();
```

Items can be fetched synchronously:
```java
try {
    CDAArray array = client.fetchEntriesBlocking();
    // success
} catch (RetrofitError e) {
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

### Default Ordering

Bear in mind that there is no default ordering included for any method which returns a `CDAArray` instance. This means that if you plan to page through more than 100 results with multiple requests, there is no guarantee that you will cover all entries. It is however possible to specify ordering for a query with any of the `fetch*Matching*` methods, for instance:

```java
client.fetchEntriesMatching(new HashMap<String, String>() {{
    put("order", "-sys.createdAt");
}}, new CDACallback<CDAArray>() {
    @Override
    protected void onSuccess(CDAArray array, Response response) {
      // ...
    }
});    
```

The above snippet will fetch all Entries, ordered by newest-to-oldest. Another important thing is that if you use the `fetchArrayNextPage()` method with a `CDAArray` instance which was fetched with specific ordering, the original request parameters will be used, hence that specified ordering will be preserved.

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
ResourceUtils.saveResourceToFile(someResource, new File("/path/to/save"));

// restore Resource from local file:
CDAResource resource = ResourceUtils.readResourceFromFile(new File("/path/to/restore"));
```

Note that in case you attempt to restore a previously saved Resource, if the original class does not exist the `readResourceFromFile()` method will throw a `ClassNotFoundException`. 

### Preview Mode

The Content Delivery API only returns published Entries. However, you might want to preview content in your app before making it public for your users. For this, you can use the preview mode, which will return **all** Entries, regardless of their published status:

```java
CDAClient client = new CDAClient.Builder()
        .setSpaceKey("space-key-goes-here")
        .setAccessToken("access-token-goes-here")
        .preview()
        .build();
```

Apart from the configuration option, you can use the SDK without modifications with one exception: you need to obtain a preview access token, which you can get in the "API" tab of the Contentful app. In preview mode, data can be invalid, because no validation is performed on unpublished entries. Your app needs to deal with that. Be aware that the access token is read-write and should in no case be shipped with a production app.

Documentation
=============

For further information, check out our official [JavaDoc][3] site or browse the [API documentation][4].

License
=======

Copyright (c) 2014 Contentful GmbH. See [LICENSE.txt][6] for further details.


 [1]: https://www.contentful.com
 [2]: https://oss.sonatype.org/service/local/repositories/releases/content/com/contentful/java/java-sdk/2.0.0/java-sdk-2.0.0.jar
 [3]: https://contentful.github.io/contentful.java/
 [4]: https://www.contentful.com/developers/documentation/content-delivery-api/
 [5]: https://square.github.io/okhttp/
 [6]: LICENSE.txt
