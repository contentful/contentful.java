# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## Version [10.2.0] - (TBD)
- Add `rich text` to entry types.

## Version [10.1.0] - (2018-05-22)
- Add `png8` image option for returning images in png with up to 256 colours.
- Make `sync` environment aware.

## Version [10.0.0] - (2018-04-18)
- Add `locales` endpoint
- Add `Localizer` to make localizing entries thread save.
- Update cache to store locales and not spaces
- Remove locales from `Space`
- Remove `setLocale` on resources (entries and assets). Use `localize()` instead.

## Version [9.1.1] - (2018-03-21)
- Fix: numbers in http headers default back to english.

## Version [9.1.0] - (2018-02-13)
- Add: Support for [incoming-links](https://www.contentful.com/developers/docs/references/content-delivery-api/#/reference/search-parameters/links-to-entry/query-entries/console/java) by @shm.
- Add: Support for [partial syncing](https://www.contentful.com/developers/docs/references/content-delivery-api/#/reference/synchronization/initial-synchronization-of-entries-of-a-specific-content-type) by @vbondarovich.
- Fix: Remove synthetic internal methods by @marukami.
❤ Thanks for all the awesome contributions. Keep it up!

## Version [9.0.1] - (2018-01-15)
- Fix: Use system provided `X509TrustManager` and avoid deprecation and reflection used by previous okhttp method.

## Version [9.0.0] - (2018-01-11)
- **Remove** `.useTLS12()` from [7.4.0] and replace it with automated approach.

## Version [8.0.1] - (2017-10-18)
- Change: Use `filtered` java file to create version number
- Fix: Gracefully ignore non ascii characters on HTTP header generation.
- Cleanup: Fix warnings

## Version [8.0.0] - (2017-08-16)
- New: `populateContentTypeCache`-family to pre populate the cache of ContentTypes.
- Change: Requesting a non existing resource will throw an exception! ⚡⚡
- Change: Set timeout to parsing an error body to one second.
- Polish: Update dependencies:
    - rxjava 2.1.1 (was 1.2.0)
        - Please update your code to use the following new additions:
        - `rx.schedulers.Schedulers.io()` to `io.reactivex.schedulers.Schedulers.io()`
        - `Subscriber<CDAArray>` to `DisposableSubscriber<CDAArray>`
        - and `onCompleted()` to `onComplete()`
    - retrofit 2.3.0 (was 2.2.0)
    - okhttp 3.8.1 (was 3.6.0)
    - commonsio 2.5 (was 2.4)
    - junit 4.12 (was 4.11)
    - mockito 2.8.47 (was 1.10.19)
    - truth 0.34 (was 0.27)

## Version [7.6.2] - (2017-05-24)
- Fix: Custom Header specifying Android if os is Linux.

## Version [7.6.1] - (2017-05-22)
- Fix: Custom Contentful HTTP Header are gracefully ignoring errors.

## Version [7.6.0] - (2017-05-22)
- Added: [Image API](https://www.contentful.com/developers/docs/references/images-api/#/introduction)
- Added: Custom Contentful HTTP Header.

## Version [7.5.0] - (2017-03-30)
- Added: Retrieve default call.factory from client builder.
- Fixed: Close body in error response interceptor.
- Fixed: ContentTypes are not required to query for Assets ant ContentTypes.

## Version [7.4.0] - (2017-02-13)
- Added: `useTLS12()` for creating a client, enforcing usage of TLS 1.2
- Added: More convenient way of querying: `withContentType`, `select`, `orderBy`,
    `reverseOrderBy`, `limit`, `skip`, `include` and `where` with `IsEqualTo`, `IsNotEqualTo`,
    `HasOneOf`, `HasNoneOf`, `HasAllOf`, `IsLessThan`, `IsLessThanOrEqualTo`, `IsGreaterThan`,
    `IsGreaterThanOrEqualTo`, `Exists`, `IsEarlierThan`, `IsEarlierOrAt`, `IsLaterThan`,
    `IsLaterOrAt`, `Matches`, `IsCloseTo`, `IsWithinBoundingBoxOf`, `IsWithinCircleOf`
- Changed: Remove all warnings from JavaDoc

## Version [7.3.0] - (2017-01-04)
- Added: Fallback locales.
- Added: Support for validations.
- Added: Add rate limit headers to http exceptions.

## Version [7.2.0] - (2016-11-10)
- Added: Limited sync support for Preview endpoint (Only `inital=true!`)

## Version [7.1.0] - (2016-11-01)
- Added: Clear java cache through `CMAClient`

## Version [7.0.2] - (2016-07-08)
- Fixed: requesting a sort order now gets respected by transformations

## Version [7.0.1] - (2016-05-02)
- Changed: Updating proguard rules sample file to reflect dependency changes

## Version [7.0.0] - (2016-04-15)
- Fixed: Integration tests don't expect wrong entries
- Changed: Add java and os version to user agent
- Changed: Update okhttp from `2.5.0` to `3.2.0`[OkHTTP3 Changelog for 3.2.0](https://github.com/square/okhttp/blob/master/CHANGELOG.md#version-320)
- Changed: Update retrofit from `1.9.0` to `2.0.1`[Retrofit Changelog for 2.0.0](https://github.com/square/retrofit/blob/master/CHANGELOG.md#version-201-2016-03-30)
- Changed: Update rxjava from `1.0.14` to `1.1.2`[rxjava Changelog for 1.1.2](https://github.com/ReactiveX/RxJava/releases/tag/v1.1.2)
- New: Add gson `2.6.2` (was bundled with retrofit before)
- New: Add converter-gson `2.0.1` (was bundled with retrofit before)
- New: Add adapter-rxjava `2.0.1` (was bundled with retrofit before)
- New: Use Call.Factory for http client customization

## Version [6.1.2] - (2016-02-11)
- New: Add Integration Tests
- Fixed: Add missing javadoc
- Fixed: Distribute jar with dependencies
- Fixed: Sonatype snapshots repository link

## Version [6.1.1] - (2016-01-11)
- Fixed: Wrong name of syncurl in tests

## Version [6.1.0] - (2015-12-24)
- Changed: calling sync in preview is now disabled, throwing an UnsupportedOperationException.

## Version [6.0.0] - (2015-12-18)
- New: Support custom retrofit logger
- Changed: Removed final modifier from public classes

## Version [5.0.1] - (2015-10-28)
- Fixed: ConcurrentModificationException when localized link field points to invalid entry.

## Version [5.0.0] - (2015-10-27)
- Changed: `CDAArray.items()` now contains ONLY top level resources (no linked resources). All resources are available via `CDAArray.assets()` and `CDAArray.entries()` by ID.

## Version [4.0.2] - (2015-08-26)
- Changed: `CDAField` +`Serializable`
- Changed: `CDALocale` +`Serializable`
- Changed: RxJava v1.0.14

## Version [4.0.1] - (2015-07-20)
- Fixed: NPE when processing entries with null links.

## Version [4.0.0] - (2015-07-20)
- Changed: `CDAResource` +`Serializable`
- Changed: `CDAEntry` +`final`
- Changed: Removed wildcard return types.

## Version [3.0.0] - (2015-07-15)
- New: Major performance improvements especially around array results link resolution.
- New: `getAttribute(name)` returns a `sys` attribute while inferring the return type.
- New: `getField(name)` returns a field value while inferring the return type.
- New: `CDAResource.id()` returns the resource ID.
- New: `CDAEntry.contentType()` returns the `CDAContentType` for that entry.
- New: `CDAArray` has `items()` which returns a mixture of `CDAResource` objects. `assets()` and `entries()` are mappings by resource IDs.
- New: `CDAAsset.title()` returns the title of the asset.
- New: (sync) Mapping of deleted resources via `deletedAssets()` and `deletedEntries()`.
- Changed: Replace client "modules" with a simplified `fetch()`/`observe()` syntax.
- Changed: Cleaner syntax for queries using `FetchQuery`/`ObserveQuery` and the `where()` method.
- Changed: Fallback to the default locale. Calling `setLocale(code)` and `getField(name)` - returns the value from the default locale if there isn't one for the active locale.
- Changed: Better abstractions for `CDAContentType` fields via `CDAField`.
- Changed: Resource types are represented by `CDAType` enum and available for each resource via `CDAResource.type()`.
- Changed: `CDASyncedSpace` has been renamed to `SynchronizedSpace`.
- Changed: `CDAClient.Builder` created via `CDAClient.builder()`.
- Changed: `CDAClient.Builder` now use `setSpace(id)` and `setToken(token)`.
- Changed: Package resource classes under `com.contentful.java.cda`.
- Changed: Calling `setEndpoint()` now takes a full URL.
- Changed: Asset URLs are no longer scheme prefixed, i.e. "//url.com/foo.jpg".
- Changed: Removed `noSSL()` from client builder, can be achieved via `setEndpoint()`.
- Changed: Removed `nullifyUnresolvedLinks()` and set as the default behavior.
- Changed: Removed client custom classes mapping.
- Changed: Removed custom client provider (custom client still supported).

## Version [2.0.4] - 2015-06-01
- Fixed: Default to UTF-8 charset.

## Version [2.0.3] - 2015-05-19
- Fixed: NPE for entry with no fields.

## Version [2.0.2] - 2015-05-07
- Fixed: NPE for assets in draft state with no media.
- Changed: Retrofit v1.9.0
- Changed: RxJava v1.0.5

## Version [2.0.1] - 2015-01-05
- New: Optional mode that nullifies unresolved links.

## Version [2.0.0] - 2015-12-09
- New: fetch*() can supports RxJava observables.
- New: Support custom API endpoints.
- New: Support custom callback executor.
- Changed: Remove HTTP response object From `CDACallback.onSuccess()`.
- Fixed: Sync no longer generates new `Executor` for every callback.
- Fixed: Asynchronous methods execution now works properly.

## Version [1.0.10] - 2014-11-28
- Fixed: RxJava now defers to IO thread.

## Version [1.0.9] - 2014-11-25
- Fixed: Sync paging iteration.

## Version [1.0.8] - 2014-11-24
- Fixed: Apache HttpComponents dependency is stripped from Android builds.

## Version [1.0.7] - 2014-11-21
- Fixed: Client sync methods iterate through paginated responses.

## Version [1.0.6] - 2014-10-20
- New: Client support for the Preview API.
- Changed: Retrofit v1.7.0
- Fixed: `CDASpace` instances will no longer have an empty `space` value in their sys map.
- Fixed: Assets with localized fields should now have `url` and `mimeType` attributes properly set.

## Version [1.0.5] - 2014-10-01
- Fixed: Content Types with null `displayField` are parsed correctly.

## Version [1.0.4] - 2014-09-17
- New: Support custom log level.
- New: Add `CDAResourceType` and `CDAFieldType` constants.

## Version [1.0.3] - 2014-08-29
- Fixed: Synchronization issues.

## Version [1.0.2] - 2014-08-28
- Changed: Client synchronous methods no longer catch broad exceptions.

## Version [1.0.1] - 2014-08-19
- Fixed: Automatic link resolution for nested arrays.

## Version 1.0.0 - 2014-08-13
Initial release.

[unreleased]: https://github.com/contentful/contentful.java/compare/java-sdk-10.2.0...HEAD
[10.2.0]: https://github.com/contentful/contentful.java/compare/java-sdk-10.1.0...java-sdk-10.2.0
[10.1.0]: https://github.com/contentful/contentful.java/compare/java-sdk-10.0.0...java-sdk-10.1.0
[10.0.0]: https://github.com/contentful/contentful.java/compare/java-sdk-9.1.1...java-sdk-10.0.0
[9.1.1]: https://github.com/contentful/contentful.java/compare/java-sdk-9.1.0...java-sdk-9.1.1
[9.1.0]: https://github.com/contentful/contentful.java/compare/java-sdk-9.0.1...java-sdk-9.1.0
[9.0.1]: https://github.com/contentful/contentful.java/compare/java-sdk-9.0.0...java-sdk-9.0.1
[9.0.0]: https://github.com/contentful/contentful.java/compare/java-sdk-8.0.1...java-sdk-9.0.0
[8.0.1]: https://github.com/contentful/contentful.java/compare/java-sdk-8.0.0...java-sdk-8.0.1
[8.0.0]: https://github.com/contentful/contentful.java/compare/java-sdk-7.6.3...java-sdk-8.0.0
[7.6.2]: https://github.com/contentful/contentful.java/compare/java-sdk-7.6.2...java-sdk-7.6.3
[7.6.1]: https://github.com/contentful/contentful.java/compare/java-sdk-7.6.1...java-sdk-7.6.2
[7.6.0]: https://github.com/contentful/contentful.java/compare/java-sdk-7.5.0...java-sdk-7.6.0
[7.5.0]: https://github.com/contentful/contentful.java/compare/java-sdk-7.4.0...java-sdk-7.5.0
[7.4.0]: https://github.com/contentful/contentful.java/compare/java-sdk-7.3.0...java-sdk-7.4.0
[7.3.0]: https://github.com/contentful/contentful.java/compare/java-sdk-7.2.0...java-sdk-7.3.0
[7.2.0]: https://github.com/contentful/contentful.java/compare/java-sdk-7.1.0...java-sdk-7.2.0
[7.1.0]: https://github.com/contentful/contentful.java/compare/java-sdk-7.0.2...java-sdk-7.1.0
[7.0.2]: https://github.com/contentful/contentful.java/compare/java-sdk-7.0.1...java-sdk-7.0.2
[7.0.1]: https://github.com/contentful/contentful.java/compare/java-sdk-7.0.0...java-sdk-7.0.1
[7.0.0]: https://github.com/contentful/contentful.java/compare/java-sdk-7.1.2...java-sdk-7.0.0
[6.1.2]: https://github.com/contentful/contentful.java/compare/java-sdk-6.1.1...java-sdk-6.1.2
[6.1.1]: https://github.com/contentful/contentful.java/compare/java-sdk-6.1.0...java-sdk-6.1.1
[6.1.0]: https://github.com/contentful/contentful.java/compare/java-sdk-6.0.0...java-sdk-6.1.0
[6.0.0]: https://github.com/contentful/contentful.java/compare/java-sdk-5.0.1...java-sdk-6.0.0
[5.0.1]: https://github.com/contentful/contentful.java/compare/java-sdk-5.0.0...java-sdk-5.0.1
[5.0.0]: https://github.com/contentful/contentful.java/compare/java-sdk-4.0.2...java-sdk-5.0.0
[4.0.2]: https://github.com/contentful/contentful.java/compare/java-sdk-4.0.1...java-sdk-4.0.2
[4.0.1]: https://github.com/contentful/contentful.java/compare/java-sdk-4.0.0...java-sdk-4.0.1
[4.0.0]: https://github.com/contentful/contentful.java/compare/java-sdk-3.0.0...java-sdk-4.0.0
[3.0.0]: https://github.com/contentful/contentful.java/compare/java-sdk-2.0.4...java-sdk-3.0.0
[2.0.4]: https://github.com/contentful/contentful.java/compare/2.0.3...java-sdk-2.0.4
[2.0.3]: https://github.com/contentful/contentful.java/compare/2.0.2...2.0.3
[2.0.2]: https://github.com/contentful/contentful.java/compare/2.0.1...2.0.2
[2.0.1]: https://github.com/contentful/contentful.java/compare/2.0.0...2.0.1
[2.0.0]: https://github.com/contentful/contentful.java/compare/2.0.2...2.0.3
[1.0.10]: https://github.com/contentful/contentful.java/compare/1.0.9...1.0.10
[1.0.9]: https://github.com/contentful/contentful.java/compare/1.0.8...1.0.9
[1.0.8]: https://github.com/contentful/contentful.java/compare/1.0.7...1.0.8
[1.0.7]: https://github.com/contentful/contentful.java/compare/1.0.6...1.0.7
[1.0.6]: https://github.com/contentful/contentful.java/compare/1.0.5...1.0.6
[1.0.5]: https://github.com/contentful/contentful.java/compare/1.0.4...1.0.5
[1.0.4]: https://github.com/contentful/contentful.java/compare/1.0.3...1.0.4
[1.0.3]: https://github.com/contentful/contentful.java/compare/1.0.2...1.0.3
[1.0.2]: https://github.com/contentful/contentful.java/compare/1.0.1...1.0.2
[1.0.1]: https://github.com/contentful/contentful.java/compare/1.0.0...1.0.1
