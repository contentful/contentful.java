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
  <version>7.0.0</version>
</dependency>
```
or Gradle:
```groovy
compile 'com.contentful.java:java-sdk:7.0.0'
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

The SDK requires at minimum Java 6 or Android 2.3.

### Default Client

The SDK uses Retrofit under the hood as a REST client, which detects [OkHttp][5] in your classpath and uses it if it's available, otherwise falls back to the default `HttpURLConnection`.

The recommended approach would be to add [okhttp-urlconnection][5] as a dependency to your project (okhttp already included), but that is completely optional.

You can also specify a custom client to be used, refer to the [official documentation][3] for instructions.

### Proguard

Grab the [ProGuard configuration file][proguard] and apply to your project.

Usage
=====

The `CDAClient` manages all your interaction with the Contentful Delivery API.
```java
CDAClient client = CDAClient.builder()
    .setSpace("space-key-goes-here")
    .setToken("access-token-goes-here")
    .build();
```

In order to fetch resources use the `CDAClient.fetch()` method, and provide the type of resource(s) you want to fetch:
```java
// Fetch entries
CDAArray array = client.fetch(CDAEntry.class).all();

// Fetch an entry matching a specific id
CDAEntry entry = client.fetch(CDAEntry.class).one("entry-id");

// Fetch entries with custom query
CDAArray result = client.fetch(CDAEntry.class)
    .where("content_type", "cat")
    .where("order", "sys.updatedAt")
    .all();
```

All of the above examples are synchronous. In order to invoke the request asynchronously, it is possible to provide a callback:
```java
client.fetch(CDAAsset.class).all(new CDACallback<CDAArray>() {
  @Override protected void onSuccess(CDAArray result) {
    // ...
  }
});
```

Note that the return value for any asynchronous methods is the callback itself, so make sure to keep a reference to it and clear it according to its host lifecycle events. 

If you want to use RxJava instead, call the `observe()` method to get an `Observable` instance:
```java
client.observe(CDAAsset.class)
    .one("jake")
    .subscribe(System.out::println);
```

### Default Ordering

Bear in mind that there is no default ordering included for any method which returns a `CDAArray` instance. This means that if you plan to page through more than 100 results with multiple requests, there is no guarantee that you will cover all entries. It is however possible to specify custom ordering:

```java
CDAArray result = client.fetch(CDAEntry.class)
    .where("order", "-sys.createdAt")
    .all();
```

The above snippet will fetch all Entries, ordered by newest-to-oldest.

### Preview Mode

The Content Delivery API only returns published Entries. However, you might want to preview content in your app before making it public for your users. For this, you can use the preview mode, which will return **all** Entries, regardless of their published status:

```java
CDAClient client = CDAClient.builder()
    .setSpace("space-key-goes-here")
    .setToken("access-token-goes-here")
    .preview()
    .build();
```

Apart from the configuration option, you can use the SDK without modifications with one exception: you need to obtain a preview access token, which you can get in the "API" tab of the Contentful app. In preview mode, data can be invalid, because no validation is performed on unpublished entries. Your app needs to deal with that. Be aware that the access token is read-write and should in no case be shipped with a production app.

Migration
=========

Migration guide to 3.x is [available in the wiki][7].

Documentation
=============

For further information, check out our official [JavaDoc][3] site or browse the [API documentation][4].

License
=======

Copyright (c) 2016 Contentful GmbH. See [LICENSE.txt][6] for further details.


 [1]: https://www.contentful.com
 [2]: https://oss.sonatype.org/service/local/repositories/releases/content/com/contentful/java/java-sdk/7.0.0/java-sdk-7.0.0.jar
 [3]: https://contentful.github.io/contentful.java/
 [4]: https://www.contentful.com/developers/documentation/content-delivery-api/
 [5]: https://square.github.io/okhttp/
 [6]: LICENSE.txt
 [7]: https://github.com/contentful/contentful.java/wiki/3.0-Migration
 [proguard]: proguard-cda.cfg  
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/com/contentful/java/java-sdk/
