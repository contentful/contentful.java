package com.contentful.java.cda;

import static com.contentful.java.cda.SyncType.Type.Asset;
import static com.contentful.java.cda.SyncType.Type.DeletedAsset;
import static com.contentful.java.cda.SyncType.Type.DeletedEntry;
import static com.contentful.java.cda.SyncType.Type.Deletion;
import static com.contentful.java.cda.SyncType.Type.Entry;

/**
 * The type of syncing.
 * <p>
 * If you want to specify to sync only specific parts of content, please use this class in
 * conjunction with {@link CDAClient#sync(SyncType)} and one of the static methods defined here.
 */
public class SyncType {
  /**
   * What should be synced?
   */
  public enum Type {
    /**
     * Only include new and changed assets.
     */
    Asset,
    /**
     * Only include new and changed entries.
     */
    Entry,
    /**
     * Only include deletions of assets and entries.
     */
    Deletion,
    /**
     * Only include deletions of assets.
     */
    DeletedAsset,
    /**
     * Only include deletions of entries.
     */
    DeletedEntry
  }

  final Type type;
  final String contentType;

  SyncType(Type type, String contentType) {
    this.type = type;
    this.contentType = contentType;
  }

  /**
   * @return a type syncing all assets.
   */
  public static SyncType allAssets() {
    return new SyncType(Asset, null);
  }

  /**
   * @return a type syncing all entries.
   */
  public static SyncType allEntries() {
    return new SyncType(Entry, null);
  }

  //BEGIN TO LONG CODE LINES

  /**
   * Sync all entries of a specific content type.
   *
   * @return a type syncing all entries of a specific type.
   * @see <a href=https://www.contentful.com/developers/docs/references/content-delivery-api/#/reference/synchronization/initial-synchronization-of-entries-of-a-specific-content-type>Contentful Docs</a>
   */
  //END TO LONG CODE LINES
  public static SyncType onlyEntriesOfType(String type) {
    return new SyncType(Entry, type);
  }

  /**
   * @return a type syncing all deleted resources, including assets and entries.
   */
  public static SyncType onlyDeletion() {
    return new SyncType(Deletion, null);
  }

  /**
   * @return a type syncing all deleted assets.
   */
  public static SyncType onlyDeletedAssets() {
    return new SyncType(DeletedAsset, null);
  }

  /**
   * @return a type syncing all deleted entries.
   */
  public static SyncType onlyDeletedEntries() {
    return new SyncType(DeletedEntry, null);
  }

  /**
   * @return a string representation of the type's name
   */
  public String getName() {
    return type.name();
  }

  /**
   * @return the content type name if present.
   */
  public String getContentType() {
    return contentType;
  }
}
