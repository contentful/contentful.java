Change Log
==========

<!--Version 1.0.6 *(2014-XX-XX)*
----------------------------

 * New: Client can be configured to use the Preview API.
 * New: Updated Retrofit version to 1.7.0.
 * New: Apply CheckStyle.
 * Fix: `CDASpace` instances will no longer have an empty `space` value in their sys map.
 * Fix: Assets with localized fields should now have `url` and `mimeType` attributes properly set.

-->
Version 1.0.5 *(2014-10-01)*
----------------------------

 * Fix: `ResourceTypeAdapter` parses Content Types without a `displayField` correctly.


Version 1.0.4 *(2014-09-17)*
----------------------------

 * New: Expose log level configuration to client class.
 * New: Add `CDAResourceType` and `CDAFieldType` enums to `Constants` class.


Version 1.0.3 *(2014-08-29)*
----------------------------

 * Fix: Synchronization now works properly.


Version 1.0.2 *(2014-08-28)*
----------------------------

 * Fix: Client synchronous methods no longer catch broad exceptions.


Version 1.0.1 *(2014-08-19)*
----------------------------

 * Fix: Automatic link resolving in nested arrays no longer fails.


Version 1.0.0 *(2014-08-13)*
----------------------------

Initial release.