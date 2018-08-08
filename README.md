<p align="center">
  <img src="assets/feature_graphic.png" alt="Contentful Java SDK"><br/>

  <a href="https://www.contentful.com/slack/">
    <img src="https://img.shields.io/badge/-Join%20Community%20Slack-2AB27B.svg?logo=slack&maxAge=31557600" alt="Join Contentful Community Slack">
  </a>
  &nbsp;
  <a href="https://www.contentfulcommunity.com/">
    <img src="https://img.shields.io/badge/-Join%20Community%20Forum-3AB2E6.svg?logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA1MiA1OSI+CiAgPHBhdGggZmlsbD0iI0Y4RTQxOCIgZD0iTTE4IDQxYTE2IDE2IDAgMCAxIDAtMjMgNiA2IDAgMCAwLTktOSAyOSAyOSAwIDAgMCAwIDQxIDYgNiAwIDEgMCA5LTkiIG1hc2s9InVybCgjYikiLz4KICA8cGF0aCBmaWxsPSIjNTZBRUQyIiBkPSJNMTggMThhMTYgMTYgMCAwIDEgMjMgMCA2IDYgMCAxIDAgOS05QTI5IDI5IDAgMCAwIDkgOWE2IDYgMCAwIDAgOSA5Ii8+CiAgPHBhdGggZmlsbD0iI0UwNTM0RSIgZD0iTTQxIDQxYTE2IDE2IDAgMCAxLTIzIDAgNiA2IDAgMSAwLTkgOSAyOSAyOSAwIDAgMCA0MSAwIDYgNiAwIDAgMC05LTkiLz4KICA8cGF0aCBmaWxsPSIjMUQ3OEE0IiBkPSJNMTggMThhNiA2IDAgMSAxLTktOSA2IDYgMCAwIDEgOSA5Ii8+CiAgPHBhdGggZmlsbD0iI0JFNDMzQiIgZD0iTTE4IDUwYTYgNiAwIDEgMS05LTkgNiA2IDAgMCAxIDkgOSIvPgo8L3N2Zz4K&maxAge=31557600" alt="Join Contentful Community Forum">
  </a>
</p>

contentful.java - Contentful Java Delivery SDK
==============================================
[![Build Status](https://travis-ci.org/contentful/contentful.java.svg)](https://travis-ci.org/contentful/contentful.java/builds#)
[![codecov](https://codecov.io/gh/contentful/contentful.java/branch/master/graph/badge.svg)](https://codecov.io/gh/contentful/contentful.java)

> Java SDK for [Content Delivery API](https://www.contentful.com/developers/docs/references/content-delivery-api/) and [Content Preview API](https://www.contentful.com/developers/docs/references/content-preview-api/). It helps you to easily access your content stored in Contentful with your Java applications.


What is Contentful?
-------------------
[Contentful](https://www.contentful.com) provides a content infrastructure for digital teams to power content in websites, apps, and devices. Unlike a CMS, Contentful was built to integrate with the modern software stack. It offers a central hub for structured content, powerful management and delivery APIs, and a customizable web app that enable developers and content creators to ship digital products faster.

<details open>
<summary>Table of contents</summary>

<!-- TOC -->

- [Setup](#setup)
  - [Snapshots](#snapshots)
  - [Proguard](#proguard)
- [Usage](#usage)
  - [Fetching Resources](#fetching-resources)
  - [Calls in Parralel](#calls-in-parallel)
  - [Paging](#paging)
  - [Preview Mode](#preview)
  - [Sync](#sync)
- [Documentation](#documentation)
- [License](#license)

</details>

<!-- /TOC -->

Setup
=====

Install the Contentful dependency:

* _Maven_
```xml
<dependency>
  <groupId>com.contentful.java</groupId>
  <artifactId>java-sdk</artifactId>
  <version>10.0.0</version>
</dependency>
```

* _Gradle_

```groovy
compile 'com.contentful.java:java-sdk:10.0.0'
```

This SDK needs at least Java 7 or Android 5.

Snapshots
---------

Development versions of this SDK are available through 

* [Sonatype's `snapshots` repository](https://oss.sonatype.org/content/repositories/snapshots/com/contentful/java/java-sdk/):

```groovy
maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
compile 'com.contentful.java:java-sdk:10.0.0-SNAPSHOT'
```

* [jitpack.io](https://jitpack.io/#contentful/contentful.java/master-SNAPSHOT):

```groovy
maven { url 'https://jitpack.io' }
compile 'com.github.contentful:contentful.java:java-sdk-10.0.0-SNAPSHOT'
```

Proguard
--------

The [ProGuard configuration file](proguard-cda.cfg) is used for minifying Android Apps using this SDK.

Usage
=====

The `CDAClient` manages all interactions with the _Content Delivery API_.

```java
CDAClient client = CDAClient.builder()
    .setSpace("{space-key-goes-here}")
    .setToken("{access-token-goes-here}")
    .build();
```

Fetching Resources
------------------

Fetching is achieved by calling the `CDAClient.fetch()`-method. It fetches all _Resources_<sup><a href="https://www.contentful.com/developers/docs/references/content-delivery-api/#/introduction/common-resource-attributes" title="Everything stored in Contentful is a Resource."/>i</a></sup> from a _Space_<sup><a href="https://www.contentful.com/developers/docs/references/content-delivery-api/#/reference/spaces" title="A Space is a container for all Resources."/>i</a></sup>. The following code fetches all _Entries_<sup><a href="https://www.contentful.com/developers/docs/references/content-delivery-api/#/reference/entries" title="An Entry stores content in a user defined structure."/>i</a></sup>:

```java
// Fetch entries
CDAArray array = 
    client
        .fetch(CDAEntry.class)
        .all();
```

Filtering of these Resources can be done by chaining method calls after the fetch. Using `.one()` and a Resource id retrieves only the specifyied Resource:

```java
// Fetch an Entry with a specific id
CDAEntry entry =
    client
        .fetch(CDAEntry.class)
        .one("{entry-id-goes-here}");
```

Fetching only Entries of a specific _ContentType_<sup><a href="https://www.contentful.com/developers/docs/references/content-delivery-api/#/reference/content-types" title="A ContentType defines the structure of an Entries field."/>i</a></sup> is done by adding a `.withContentType({id})` call to the chain:

```java
// Fetch entries with custom query
CDAArray result = 
    client
        .fetch(CDAEntry.class)
        .withContentType("{content-type-id-goes-here}")
        .orderBy("{some-field-id-to-order-by-goes-here}")
        .all();
```

Lastly fetching _Assets_<sup><a href="https://www.contentful.com/developers/docs/references/content-delivery-api/#/reference/assets" title="All external binary data stored in Contentful. Images, videos, pdf, etc"/>i</a></sup> follows the same principles:

```java
// Fetch an Asset with a specific id
CDAEntry entry =
    client
        .fetch(CDAEntry.class)
        .one("{asset-id-goes-here}");
```

Calls in Parallel
-----------------

All of the above examples are synchronous. In order to request asynchronously, provide a callback to `.all(…)` or `.one(…)`:

```java
client
    .fetch(CDAAsset.class)
    .all(new CDACallback<CDAArray>() {
  @Override protected void onSuccess(CDAArray result) {
    // ...
  }
});
```

> Note: The return value for any asynchronous methods is the Callback itself, making sure keeping a reference to it and clearing it according to its host lifecycle events is adviced. 

If _RxJava_<sup><a href="https://github.com/ReactiveX/RxJava" title="a library for composing asynchronous and event-based programs using observable sequences for the Java VM."/>i</a></sup> is wanted instead, the `.observe()` method can be used to get an `Observable` instance:

```java
client
    .observe(CDAAsset.class)
    .one("jake")
    .subscribe(System.out::println);
```

Paging
------

If more then _100_ Resources are in the Space, `.fetchAll()` will only return the first _100_. If more Resources are needed, specify the limit with the `.limit(X)` like so:

```java
CDAArray result = 
  client
    .fetch(CDAEntry.class)
    .limit(1000)
    .all();
```

The maximum number of Resources to be requested is _1000_. 

For more then _1000_ Resources `.skip(N)`, `.limit(L)` and `.orderBy(F)` methods are needed. By using `.skip(N)`, the first _N_ Resources are ignored and _L_, from `.limit(L)`, items are returned. 

To guarantee ordering, the use of the `.orderBy` method is required: It enforces the _Array_<sup><a href="https://www.contentful.com/developers/docs/references/content-delivery-api/#/introduction/collection-resources-and-pagination" title="A collection of Resources from Contentful. Containts meta information about number and limits of the Resources it contains."/>i</a></sup> to be in a predictable order. 

The following code can be used to request all Entries:

```java
// get the amount of Entries, without fetching the actual content
final int amountOfResourcesInContentful = 
  client
    .fetch(CDAEntry.class)
    .limit(0)
    .all()
    .total();

// create storage for the Entries
final List<CDAResource> resources = new ArrayList<CDAResource>(amountOfResourcesInContentful);

// use page size, based on usecase
final int PAGE_SIZE = 2;

// loop through all pages and store results
for(int page = 0; page * PAGE_SIZE < amountOfResourcesInContentful; ++page) {
  final CDAArray currentPagedItems = client
      .fetch(CDAEntry.class)
      .skip(page * PAGE_SIZE)
      .limit(PAGE_SIZE)
      .orderBy("sys.createdAt")
      .all();

  // add to current list of Entries
  resources.addAll(currentPagedItems.items());
}
```

Using the `.reverseOrderBy()` method reverses the order:

```java
CDAArray result = 
    client
        .fetch(CDAEntry.class)
        .limit(23)
        .reverseOrderBy("sys.createdAt")
        .all();
```

The above snippet will fetch the first _23_ Entries, ordered by newest-to-oldest creation day.

[Sync](#sync) is used to fetch all entries in one call and to get only changed Resources in following calls.

Preview
-------

The _Content Delivery API_ only returns _published_ Entries. The _Content Preview API_<sup><a href="https://www.contentful.com/developers/docs/references/content-preview-api/" title="Shortened to Preview from now on."/>i</a></sup> will return _all_ Entries, even not published ones:

```java
CDAClient client = 
    CDAClient.builder()
        .setSpace("space-key-goes-here")
        .setToken("access-token-goes-here")
        .preview()
        .build();
```

The _Preview Access Token_<sup><a href="https://www.contentful.com/developers/docs/references/content-preview-api/#/introduction/preview-api-authentication" title="A password for this specific API."/>i</a></sup> is exposed on the [Contentful Web App](https://app.contentful.com/deeplink?link=api). 

> Note: In Preview Resources can be invalid since no validation is performed prior to publishing.

Sync
----

Fetching all Resources and retrieving only changes on subsequent calls is accomplished by using the `.sync()`-methods:

```java
SynchronizedSpace space = client.sync().fetch();
```

The _SynchronizedSpace_ will contain _all_ _published_ Resources. If `.preview()` ([see Preview](#preview)) is used it'll also contain all unpublished Resources.

If at a later point in time changes should be fetched, calling `sync()` again using the given SynchronizedSpace as a parameter is needed:

```java
SynchronizedSpace later = client.sync(space).fetch();
```

If an Entry got deleted, its _id_<sup><a href="https://www.contentful.com/developers/docs/references/content-delivery-api/#/introduction/common-resource-attributes" title="Every Resource has a unique id."/>i</a></sup> is returned in the`SynchronizedSpace.deletedEntries()` set. Same is true for the deleted Assets through `SynchronizedSpace.deletedAssets()`.

Documentation
=============

See
* [JavaDoc](https://contentful.github.io/contentful.java/) 
* [API documentation](https://www.contentful.com/developers/documentation/content-delivery-api/)

License
=======

> Copyright (c) 2018 Contentful GmbH. See [LICENSE.txt](LICENSE.txt) for further details.
