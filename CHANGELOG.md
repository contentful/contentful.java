# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## Version [3.0.1][unreleased] - (in development)
- TBD

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

[unreleased]: https://github.com/contentful/contentful.java/compare/java-sdk-3.0.0...HEAD
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
