# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## Version [3.0.0][unreleased] - (in development)
- TBD

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

[unreleased]: https://github.com/contentful/contentful.java/compare/java-sdk-2.0.4...HEAD
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