contentful.java
===============

[![Build Status](https://travis-ci.org/contentful/contentful.java.svg)](https://travis-ci.org/contentful/contentful.java/builds#)

Java SDK for [Contentful's][1] Content Delivery API.

[Contentful][1] is a content management platform for web applications, mobile apps and connected devices. It allows you to create, edit & manage content in the cloud and publish it anywhere via powerful API. Contentful offers tools for managing editorial teams and enabling cooperation between organizations.

Setup
=====

Grab via Maven:
```xml
<dependency>
  <groupId>com.contentful.java</groupId>
  <artifactId>java-sdk</artifactId>
  <version>2.0.4</version>
</dependency>
```
or Gradle:
```groovy
compile 'com.contentful.java:java-sdk:2.0.4'
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

The SDK requires at minimum Java 6 or Android 2.3.

### Default Client

The SDK uses Retrofit under the hood as a REST client, which detects [OkHttp][5] in your classpath and uses it if it's available, otherwise falls back to the default `HttpURLConnection`.

The recommended approach would be to add [OkHttp][5] as a dependency to your project, but that is completely optional.

You can also specify a custom client to be used, refer to the [official documentation][3] for instructions.

### Dependencies

In addition to Retrofit, the library depends on RxJava, the version number can be seen in the `properties` section of the [pom.xml][8] file.

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

A client can perform various operations on different types of resources (Assets, Content Types, Entries and Spaces). Every type of resource is represented by a module in the `CDAClient` class, for example:

```java
client.assets()       // returns the Assets module
client.contentTypes() // returns the Content Types module
client.entries()      // returns the Entries module
client.spaces()       // returns the Spaces module
client.sync()         // returns the Synchronization module
```

Each module contains a set of methods which can be used to perform various operations on the specified resource type. Every method has a corresponding asynchronous extension which can be accessed through the `async()` method of the module, for example:

Retrieving all entries (synchronously):

```java
try {
  CDAArray result = client.entries().fetchAll();
  // success
} catch (RetrofitError e) {
  // failure
}
```

or asynchronously:

```java
client.entries().async().fetchAll(new CDACallback<CDAArray>() {
  @Override protected void onSuccess(CDAArray result) {
    // success
  }

  @Override protected void onFailure(RetrofitError error) {
    // failure
  }
});
```

or with RxJava:

```java
client.entries().rx().fetchAll().subscribe(new Action1<CDAArray>() {
  @Override public void call(CDAArray result) {
    // success
  }
}, new Action1<Throwable>() {
  @Override public void call(Throwable throwable) {
    // failure
  }
});
```

Note that a `CDACallback` instance can be cancelled, so keep a reference to it in order to do so:

```java
CDACallback<CDAArray> cb;

client.entries().async().fetchAll(cb = new CDACallback<CDAArray>() {
    ...
});

cb.cancel(); // onSuccess and onFailure will not be invoked
```

### Default Ordering

Bear in mind that there is no default ordering included for any method which returns a `CDAArray` instance. This means that if you plan to page through more than 100 results with multiple requests, there is no guarantee that you will cover all entries. It is however possible to specify ordering for a query using the `fetchAll()` methods that take a query parameter, for instance:

```java
client.entries().async().fetchAll(new HashMap<String, String>() {{
  put("order", "-sys.createdAt");
}}, new CDACallback<CDAArray>() {
  @Override protected void onSuccess(CDAArray result) {
    // ...
  }
});
```

The above snippet will fetch all Entries, ordered by newest-to-oldest.

### Using Custom Entry Classes

You might want to subclass `CDAEntry` to store additional data alongside Entries or to decouple the rest of your app from the Contentful SDK's API. For this purpose, it is possible to register your own custom classes for specific Content Types, when creating a client:

```java
HashMap<String, Class<?>> classMap = new HashMap<String, Class<?>>();
classMap.put("content-type-id", CustomEntry.class);

CDAClient client = new Builder()
    .setSpaceKey("space-key-goes-here")
    .setAccessToken("access-token-goes-here")
    .setCustomClasses(classMap)
    .build();
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
Another important thing to note is that this method is not the most efficient in terms of performance, as it relies heavily on reflection. If you need to persist large objects consider alternate storage solutions.

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

Migration
=========

v2.0 has introduced some backward-incompatible changes, a migration guide is [available in the wiki][7].

Documentation
=============

For further information, check out our official [JavaDoc][3] site or browse the [API documentation][4].

License
=======

Copyright (c) 2015 Contentful GmbH. See [LICENSE.txt][6] for further details.


 [1]: https://www.contentful.com
 [2]: https://oss.sonatype.org/service/local/repositories/releases/content/com/contentful/java/java-sdk/2.0.4/java-sdk-2.0.4.jar
 [3]: https://contentful.github.io/contentful.java/
 [4]: https://www.contentful.com/developers/documentation/content-delivery-api/
 [5]: https://square.github.io/okhttp/
 [6]: LICENSE.txt
 [7]: https://github.com/contentful/contentful.java/wiki/2.0-Migration
 [8]: pom.xml
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/