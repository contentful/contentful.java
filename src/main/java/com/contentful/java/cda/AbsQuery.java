package com.contentful.java.cda;

import java.util.HashMap;
import java.util.Map;

import static com.contentful.java.cda.Util.checkNotEmpty;
import static com.contentful.java.cda.Util.checkNotNull;
import static com.contentful.java.cda.Util.resourcePath;
import static java.lang.String.format;

/**
 * Root of all queries.
 * <p>
 * This class includes options to query for entries, limit the amount of
 * responses and more.
 *
 * @param <Resource> The type of the resource to be returned by this query.
 * @param <Query>    The query type to be returned on chaining to avoid casting on client side.
 */
public abstract class AbsQuery<
    Resource extends CDAResource,
    Query extends AbsQuery<Resource, Query>
    > {
  private static final String PARAMETER_CONTENT_TYPE = "content_type";
  private static final String PARAMETER_SELECT = "select";
  private static final String PARAMETER_ORDER = "order";
  private static final String PARAMETER_LIMIT = "limit";
  private static final String PARAMETER_SKIP = "skip";
  private static final String PARAMETER_INCLUDE = "include";
  private static final String PARAMETER_LINKS_TO_ENTRY = "links_to_entry";
  private static final String PARAMETER_LINKS_TO_ASSET = "links_to_asset";
  private static final int MAX_LIMIT = 1000;

  final Class<Resource> type;

  final CDAClient client;

  final Map<String, String> params = new HashMap<>();

  AbsQuery(Class<Resource> type, CDAClient client) {
    this.type = type;
    this.client = client;
  }

  /**
   * Requesting a specific content type.
   * <p>
   * The content type is especially useful if you want to limit the result of this query to only one
   * content model type.
   * <p>
   * You must specify a content type <b>before</b> querying a specific <b>field</b> on a query, an
   * exception will be thrown otherwise.
   *
   * @param contentType the content type to be used.
   * @return the calling query for chaining.
   * @throws IllegalArgumentException if contentType is null.
   * @throws IllegalArgumentException if contentType is empty.
   * @throws IllegalStateException    if contentType was set before.
   */
  @SuppressWarnings("unchecked")
  public Query withContentType(String contentType) {
    checkNotEmpty(contentType, "ContentType must not be empty.");

    if (hasContentTypeSet()) {
      throw new IllegalStateException(
          format("ContentType \"%s\" is already present in query.", contentType)
      );
    } else {
      params.put(PARAMETER_CONTENT_TYPE, contentType);
    }

    return (Query) this;
  }

  /**
   * Limit response to only selected properties.
   * <p>
   * Returns an object in which fields not specified will be <b>null</b>, resulting in potentially
   * smaller response from Contentful.
   * <p>
   * The complete <b>sys</b> object will always be returned.
   *
   * @param selection to be used. Should be 'fields.name' or similar.
   * @return the calling query for chaining.
   * @throws NullPointerException     if selection is null.
   * @throws IllegalArgumentException if selection is empty.
   * @throws IllegalStateException    if no content type was queried for before.
   * @throws IllegalArgumentException if tried to request deeper then the name of a selection.
   */
  @SuppressWarnings("unchecked")
  public Query select(String selection) {
    checkNotEmpty(selection, "Selection must not be empty.");

    if (countDots(selection) >= 2) {
      throw new IllegalArgumentException("Cannot request children of fields. "
          + "('fields.author'(✔) vs. 'fields.author.name'(✖))");
    }

    if (selection.startsWith("fields.") && !hasContentTypeSet()) {
      throw new IllegalStateException("Cannot use field selection without "
          + "specifying a content type first. Use '.withContentType(\"{typeid}\")' first.");
    }

    if (selection.startsWith("sys.") || "sys".equals(selection)) {
      if (params.containsKey(PARAMETER_SELECT)) {
        // nothing to be done here, a select is already present
      } else {
        params.put(PARAMETER_SELECT, "sys");
      }
    } else if (params.containsKey(PARAMETER_SELECT)) {
      params.put(PARAMETER_SELECT, params.get(PARAMETER_SELECT) + "," + selection);
    } else {
      params.put(PARAMETER_SELECT, "sys," + selection);
    }

    return (Query) this;
  }


  /**
   * Request entries that link to given entryId.
   *
   * @param  entryId to be used.
   * @return the calling query for chaining.
   * @throws NullPointerException     if entryId is null.
   * @throws IllegalArgumentException if entryId is empty.
   */
  @SuppressWarnings("unchecked")
  public Query linksToEntryId(String entryId) {
    checkNotEmpty(entryId, "entryId must not be empty.");

    params.put(PARAMETER_LINKS_TO_ENTRY, entryId);

    return (Query) this;
  }

  /**
   * Request entries that link to given entryId.
   *
   * @param  assetId to be used.
   * @return the calling query for chaining.
   * @throws NullPointerException     if entryId is null.
   * @throws IllegalArgumentException if entryId is empty.
   */
  @SuppressWarnings("unchecked")
  public Query linksToAssetId(String assetId) {
    checkNotEmpty(assetId, "assetId must not be empty.");

    params.put(PARAMETER_LINKS_TO_ASSET, assetId);

    return (Query) this;
  }

  /**
   * Convenient method for chaining several select queries together.
   * <p>
   * This method makes it easier to select multiple properties from one method call. It calls
   * select for all of its arguments.
   *
   * @param selections field names to be requested.
   * @return the calling query for chaining.
   * @throws NullPointerException     if a field is null.
   * @throws IllegalArgumentException if a field is of zero length, aka empty.
   * @throws IllegalStateException    if no contentType was queried for before.
   * @throws IllegalArgumentException if tried to request deeper then the name of a field.
   * @throws IllegalArgumentException if no selections were requested.
   * @see #select(String)
   */
  @SuppressWarnings("unchecked")
  public Query select(String... selections) {
    checkNotNull(selections, "Selections cannot be null. Please specify at least one.");

    if (selections.length == 0) {
      throw new IllegalArgumentException("Please provide a selection to be selected.");
    }

    for (int i = 0; i < selections.length; i++) {
      try {
        select(selections[i]);
      } catch (IllegalStateException stateException) {
        throw new IllegalStateException(stateException);
      } catch (IllegalArgumentException argumentException) {
        throw new IllegalArgumentException(
            format("Could not select %d. field (\"%s\").", i, selections[i]),
            argumentException);
      }
    }

    return (Query) this;
  }

  /**
   * Complex where query.
   * <p>
   * Use this for a more controlled and versatile way of doing specialized search requests.
   *
   * @param <T>            value type the operation uses.
   * @param name           which attribute should be checked?
   * @param queryOperation specify the queryOperation here.
   * @param values         a list of values to be checked.
   * @return the calling query for chaining.
   * @throws IllegalArgumentException if name is empty or null.
   * @throws IllegalArgumentException if queryOperation is not set.
   * @throws IllegalArgumentException if values is not set.
   * @throws IllegalArgumentException if values does not contain valid values.
   * @throws IllegalArgumentException if one value was null or empty.
   * @throws IllegalStateException    if no content type was set first, but a field was requested.
   * @throws IllegalArgumentException if name does not start with either sys or field.
   * @see QueryOperation
   */
  @SuppressWarnings("unchecked")
  public <T> Query where(String name, QueryOperation<T> queryOperation, T... values) {

    checkNotEmpty(name, "Name cannot be empty/null, please specify a name to apply operations on.");
    checkNotNull(queryOperation, "QueryOperation cannot be null.");
    checkNotNull(values, "Values to be compared with need to be set to something.");
    if (values.length == 0 && !queryOperation.hasDefaultValue()) {
      throw new IllegalArgumentException("Please specify at least one value to be searched for.");
    }

    for (int i = 0; i < values.length; ++i) {
      final T value = values[i];
      checkNotNull(value, "Value at position %d must not be null.", i);

      if (value instanceof CharSequence) {
        checkNotEmpty(value.toString(), "Value at position %d must not be empty.", i);
      }
    }

    if ((!name.startsWith("sys.") && !name.startsWith("fields."))
        && !CDAContentType.class.isAssignableFrom(type)) {
      throw new IllegalArgumentException("Please specify either a \"sys.\" or a \"fields.\" "
          + "attribute to be searched for. (Remember to specify a ContentType for \"fields.\" "
          + "searches when querying entries.)");
    }

    if (name.startsWith("fields.") && !hasContentTypeSet()) {
      throw new IllegalStateException("Cannot request fields of an entry without having a "
          + "content type set first.");
    }

    if (values.length == 0) {
      params.put(name + queryOperation.operator, queryOperation.defaultValue.toString());
    } else {
      params.put(name + queryOperation.operator, join(values));
    }

    return (Query) this;
  }

  /**
   * Simple `where` query.
   * <p>
   * This query will be used if there are not specialized queries available. Please use the more
   * concrete methods in order to gain type safety and early exceptions, without requesting the API.
   *
   * @param key   the key to be added to the query.
   * @param value the value to be added.
   * @return the calling query for chaining.
   */
  @SuppressWarnings("unchecked")
  public Query where(String key, String value) {
    params.put(key, value);
    return (Query) this;
  }

  /**
   * Order result by the given key.
   * <p>
   * Please do not forget to include the content type if you are requesting to order
   * by a field.
   *
   * @param key the key to be ordered by.
   * @return the calling query for chaining.
   * @throws IllegalArgumentException if key is null.
   * @throws IllegalArgumentException if key is empty.
   * @throws IllegalStateException    if key requests a field, but no content type is requested.
   * @see #withContentType(String)
   */
  @SuppressWarnings("unchecked")
  public Query orderBy(String key) {
    checkNotEmpty(key, "Key to order by must not be empty.");

    if (key.startsWith("fields.") && !hasContentTypeSet()) {
      throw new IllegalStateException("\"fields.\" cannot be used without setting a content type "
          + "first.");
    }

    this.params.put(PARAMETER_ORDER, key);
    return (Query) this;
  }

  /**
   * Order result by the multiple keys.
   * <p>
   * Please do not forget to include the content type if you are requesting to order
   * by a field.
   *
   * @param keys the keys to be ordered by.
   * @return the calling query for chaining.
   * @throws IllegalArgumentException if keys is null.
   * @throws IllegalArgumentException if keys is empty.
   * @throws IllegalArgumentException if one key is null.
   * @throws IllegalArgumentException if one key is empty.
   * @throws IllegalStateException    if one key requests a field, but no content type is requested.
   * @see #withContentType(String)
   * @see #orderBy(String)
   */
  @SuppressWarnings("unchecked")
  public Query orderBy(String... keys) {
    checkNotNull(keys, "Keys should not be null.");
    if (keys.length == 0) {
      throw new IllegalArgumentException("Cannot have an empty keys array.");
    }

    for (int i = 0; i < keys.length; ++i) {
      final String key = keys[i];
      checkNotEmpty(key, "Key at %d to order by must not be empty.", i);

      if (key.startsWith("fields.") && !hasContentTypeSet()) {
        throw new IllegalStateException(format("Key at %d uses \"fields.\" but cannot be "
            + "used without setting a content type first.", i));
      }
    }

    this.params.put(PARAMETER_ORDER, join(keys));
    return (Query) this;
  }

  /**
   * Order result by the given key, reversing the order.
   * <p>
   * Please do not forget to include the content type if you are requesting to order
   * by a field.
   *
   * @param key the key to be reversely ordered by.
   * @return the calling query for chaining.
   * @throws IllegalArgumentException if key is null.
   * @throws IllegalArgumentException if key is empty.
   * @throws IllegalStateException    if key requests a field, but no content type is requested.
   * @see #withContentType(String)
   */
  @SuppressWarnings("unchecked")
  public Query reverseOrderBy(String key) {
    checkNotEmpty(key, "Key to order by must not be empty");

    if (key.startsWith("fields.") && !hasContentTypeSet()) {
      throw new IllegalStateException("\"fields.\" cannot be used without setting a content type "
          + "first.");
    }

    this.params.put(PARAMETER_ORDER, "-" + key);
    return (Query) this;
  }

  /**
   * Limits the amount of elements to a given number.
   * <p>
   * If more then the number given elements are present, you can use {@link #skip(int)} and
   * {@see #limit(int)} for pagination.
   *
   * @param limit a non negative number less than 1001 to include elements.
   * @return the calling query for chaining.
   * @see #skip(int)
   */
  @SuppressWarnings("unchecked")
  public Query limit(int limit) {
    if (limit < 0) {
      throw new IllegalArgumentException(format("Limit of %d is negative.", limit));
    }

    if (limit > MAX_LIMIT) {
      throw new IllegalArgumentException(
          format("Limit of %d is greater than %d.", limit, MAX_LIMIT)
      );
    }

    params.put(PARAMETER_LIMIT, Integer.toString(limit));

    return (Query) this;
  }

  /**
   * Skips the first elements of a response.
   * <p>
   * If more limit(int) elements are present, you can use skip(int) to simulate pagination.
   *
   * @param skip a non negative number to exclude the first elements.
   * @return the calling query for chaining.
   * @see #limit(int)
   */
  @SuppressWarnings("unchecked")
  public Query skip(int skip) {
    if (skip < 0) {
      throw new IllegalArgumentException(format("Limit of %d is negative.", skip));
    }

    params.put(PARAMETER_SKIP, Integer.toString(skip));

    return (Query) this;
  }

  /**
   * Include references entries and their entries up to the given level.
   * <p>
   * A level of inclusion of 0 means, do not include references referenced, but not requested.
   * Please note also, that more then 10 include levels cannot be specified.
   *
   * @param level the number of recursion of inclusion to be used.
   * @return the calling query for chaining.
   */
  @SuppressWarnings("unchecked")
  public Query include(int level) {
    if (level < 0) {
      throw new IllegalArgumentException(format("Include level of %d is negative.", level));
    }
    if (level > 10) {
      throw new IllegalArgumentException(format("Include level of %d is to high.", level));
    }

    params.put(PARAMETER_INCLUDE, Integer.toString(level));

    return (Query) this;
  }

  @SuppressWarnings("unchecked")
  Query where(Map<String, String> params) {
    this.params.clear();
    this.params.putAll(params);
    return (Query) this;
  }

  String path() {
    return resourcePath(type);
  }

  private boolean hasContentTypeSet() {
    if (CDAAsset.class.isAssignableFrom(type)) {
      return true;
    } else {
      return params.containsKey(PARAMETER_CONTENT_TYPE);
    }
  }

  private <T> String join(T[] values) {
    final StringBuilder builder = new StringBuilder();
    String separator = "";
    for (final T value : values) {
      builder.append(separator);
      separator = ",";

      builder.append(value);
    }
    return builder.toString();
  }

  private int countDots(String text) {
    int count = 0;
    for (int i = 0; i < text.length(); ++i) {
      if (text.charAt(i) == '.') {
        count++;
      }
    }
    return count;
  }
}
