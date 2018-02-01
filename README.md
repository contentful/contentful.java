Contentful Java
===============

[![Build Status](https://travis-ci.org/contentful/contentful.java.svg)](https://travis-ci.org/contentful/contentful.java/builds#)
[![codecov](https://codecov.io/gh/contentful/contentful.java/branch/master/graph/badge.svg)](https://codecov.io/gh/contentful/contentful.java)

Java SDK for [Contentful's][1] Content Delivery API.

[Contentful][1] provides a content infrastructure for digital teams to power content in websites, apps, and devices. Unlike a CMS, Contentful was built to integrate with the modern software stack. It offers a central hub for structured content, powerful management and delivery APIs, and a customizable web app that enable developers and content creators to ship digital products faster.

Setup
=====

Grab via Maven:
```xml
<dependency>
  <groupId>com.contentful.java</groupId>
  <artifactId>java-sdk</artifactId>
  <version>9.1.0</version>
</dependency>
```
or Gradle:
```groovy
compile 'com.contentful.java:java-sdk:9.1.0'
```

The SDK requires at minimum Java 6 or Android 2.3.

#### Snapshots

Snapshots of the development version are available through [Sonatype's `snapshots` repository][snap]

```groovy
maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
compile 'com.contentful.java:java-sdk:9.1.0-SNAPSHOT'
```

and through [jitpack.io][jitpack]:

```groovy
maven { url 'https://jitpack.io' }
compile 'com.github.contentful:contentful.java:java-sdk-9.1.0-SNAPSHOT'
```

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
CDAArray array = 
    client
        .fetch(CDAEntry.class)
        .all();

// Fetch an entry matching a specific id
CDAEntry entry =
    client
        .fetch(CDAEntry.class)
        .one("entry-id");

// Fetch entries with custom query
CDAArray result = 
    client
        .fetch(CDAEntry.class)
        .withContentType("cat")
        .orderBy("name")
        .all();
```

All of the above examples are synchronous. In order to invoke the request asynchronously, it is possible to provide a callback:
```java
client
    .fetch(CDAAsset.class)
    .all(new CDACallback<CDAArray>() {
  @Override protected void onSuccess(CDAArray result) {
    // ...
  }
});
```

Note that the return value for any asynchronous methods is the callback itself, so make sure to keep a reference to it and clear it according to its host lifecycle events. 

If you want to use RxJava instead, call the `observe()` method to get an `Observable` instance:
```java
client
    .observe(CDAAsset.class)
    .one("jake")
    .subscribe(System.out::println);
```

### Default Ordering

Bear in mind that there is no default ordering included for any method which returns a `CDAArray` instance. This means that if you plan to page through more than 100 results with multiple requests, there is no guarantee that you will cover all entries. It is however possible to specify custom ordering:

```java
CDAArray result = 
    client
        .fetch(CDAEntry.class)
        .reverseOrderBy("sys.createdAt")
        .all();
```

The above snippet will fetch all Entries, ordered by newest-to-oldest.

### Preview Mode

The Content Delivery API only returns published Entries. However, you might want to preview content in your app before making it public for your users. For this, you can use the preview mode, which will return **all** Entries, regardless of their published status:

```java
CDAClient client = 
    CDAClient.builder()
        .setSpace("space-key-goes-here")
        .setToken("access-token-goes-here")
        .preview()
        .build();
```

Apart from the configuration option, you can use the SDK without modifications with one exception: you need to obtain a preview access token, which you can get in the "API" tab of the Contentful app. In preview mode, data can be invalid, because no validation is performed on unpublished entries. Your app needs to deal with that. Be aware that the access token is read-write and should in no case be shipped with a production app.

Documentation
=============

For further information, check out our official [JavaDoc][3] site or browse the [API documentation][4].

License
=======

Copyright (c) 2017 Contentful GmbH. See [LICENSE.txt][6] for further details.


 [1]: https://www.contentful.com
 [2]: https://oss.sonatype.org/service/local/repositories/releases/content/com/contentful/java/java-sdk/9.1.0/java-sdk-9.1.0.jar
 [3]: https://contentful.github.io/contentful.java/
 [4]: https://www.contentful.com/developers/documentation/content-delivery-api/
 [6]: LICENSE.txt
 [7]: https://github.com/contentful/contentful.java/wiki/3.0-Migration
 [proguard]: proguard-cda.cfg  
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/com/contentful/java/java-sdk/
 [jitpack]: https://jitpack.io/#contentful/contentful.java/master-SNAPSHOT
