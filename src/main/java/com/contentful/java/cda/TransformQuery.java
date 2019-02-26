package com.contentful.java.cda;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This query will tranform an incoming contentful entry to a custom type.
 * <p>
 * Please make sure to {@link ContentfulEntryModel} and {@link ContentfulField} annotations, to
 * highlight classes and fields to be transformed
 *
 * @param <Transformed> A type annotated to be used as a target of transformation from CDAEntry.
 */
public class TransformQuery<Transformed>
    extends AbsQuery<Transformed, TransformQuery<Transformed>> {

  /**
   * Annotation to mark a model class to be a "ContentfulEntryModel"
   * <p>
   * This model will be used in the {@link TransformQuery}-query to identify models CDAEntries can
   * be transformed into.
   * <p>
   * The value given will be the contentTypeId of the content type, identifying this model.
   */
  @Retention(RetentionPolicy.RUNTIME)
  public @interface ContentfulEntryModel {
    String value();
  }

  /**
   * This annotation marks a field of a {@link ContentfulEntryModel} to be part of the parsing.
   * <p>
   * Once this filed is encountered, it's locale and value (the name of the field in Contentful)
   * will be used to fill the custom type. Leaving value empty, or not stating it will mean the
   * name of the field marked with this annotation will be used. Using the value, makes overwriting
   * of different naming schemes in models and Contentful possible.
   * <p>
   * The locale attribute can be used to specify which language this entry should be used in.
   */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface ContentfulField {
    String value() default "";

    String locale() default "";
  }

  /**
   * This annotation marks a system field.
   * <p>
   * System fields are returned in the {@link CDAEntry#getAttribute(String)} method. The parameter
   * will be the name of this attribute. You can define it by either overwriting the annotations
   * value, or naming the annotated field accordingly.
   * <p>
   * If a space, or other non primary data type from the attributes is requested, please use a
   * {@link java.util.Map} to capture all the fields.
   */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface ContentfulSystemField {
    String value() default "";
  }

  private final String contentTypeId;

  private final Map<String, Transformed> instanceCache = new HashMap<>();

  /**
   * Construct a transform query.
   *
   * @param type   the type to transform to
   * @param client the client for underlying calls.
   */
  TransformQuery(Class<Transformed> type, CDAClient client) {
    super(type, client);

    ContentfulEntryModel model = type.getAnnotation(ContentfulEntryModel.class);
    if (model == null) {
      throw new IllegalArgumentException("Cannot transform a class without ContentfulEntryModel "
          + "annotation.");
    }

    try {
      type.newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException("Cannot create new instance of custom model.", e);
    }

    contentTypeId = model.value();
    withContentType(contentTypeId);

    for (final Field field : type.getDeclaredFields()) {
      final ContentfulField annotation = field.getAnnotation(ContentfulField.class);
      if (annotation != null) {
        parseFieldAnnotation(field, annotation);
      } else {
        final ContentfulSystemField systemField = field.getAnnotation(ContentfulSystemField.class);
        if (systemField != null) {
          parseSystemFieldAnnotation(field, systemField);
        }
      }
    }
  }

  private void parseFieldAnnotation(Field field, ContentfulField annotation) {
    final String name;
    if (annotation.value().isEmpty()) {
      name = field.getName();
    } else {
      name = annotation.value();
    }

    select("fields." + name);
  }

  private void parseSystemFieldAnnotation(Field field, ContentfulSystemField annotation) {
    final String name;
    if (annotation.value().isEmpty()) {
      name = field.getName();
    } else {
      name = annotation.value();
    }

    select("sys." + name);
  }

  /**
   * Retrieve the transformed entry from Contentful.
   *
   * @param id the id of the entry of type Transformed.
   * @return the Transformed entry.
   * @throws CDAResourceNotFoundException if no such resource was found.
   * @throws IllegalStateException        if the transformed class could not be created.
   * @throws IllegalStateException        if the transformed class could not be accessed.
   */
  public Flowable<Transformed> one(String id) {
    try {
      return baseQuery()
          .one(id)
          .filter(new Predicate<CDAEntry>() {
            @Override
            public boolean test(CDAEntry entry) {
              return entry.contentType().id()
                  .equals(contentTypeId);
            }
          })
          .map(new Function<CDAEntry, Transformed>() {
            @Override
            public Transformed apply(CDAEntry entry) throws Exception {
              return TransformQuery.this.transform(entry);
            }
          });
    } catch (NullPointerException e) {
      throw new CDAResourceNotFoundException(CDAEntry.class, id);
    }
  }

  /**
   * Retrieve the transformed entry from Contentful by using the given callback.
   *
   * @param id the id of the entry of type transformed.
   * @return the input callback for chaining.
   * @throws CDAResourceNotFoundException if no such resource was found.
   * @throws IllegalStateException        if the transformed class could not be created.
   * @throws IllegalStateException        if the transformed class could not be accessed.
   */
  public CDACallback<Transformed> one(String id, CDACallback<Transformed> callback) {
    return Callbacks.subscribeAsync(
        baseQuery()
            .one(id)
            .filter(new Predicate<CDAEntry>() {
              @Override
              public boolean test(CDAEntry entry) {
                return entry.contentType().id().equals(contentTypeId);
              }
            })
            .map(this::transform),
        callback,
        client);
  }

  /**
   * Retrieve all transformed entries from Contentful.
   *
   * @return a collection of transformed entry.
   * @throws CDAResourceNotFoundException if no such resource was found.
   * @throws IllegalStateException        if the transformed class could not be created.
   * @throws IllegalStateException        if the transformed class could not be accessed.
   */
  public Flowable<Collection<Transformed>> all() {
    return baseQuery()
        .all()
        .map(
            new Function<CDAArray, Collection<Transformed>>() {
              @Override
              public Collection<Transformed> apply(CDAArray array) {
                final ArrayList<Transformed> result = new ArrayList<>(array.total());

                for (final CDAResource resource : array.items) {
                  if (resource instanceof CDAEntry
                      && ((CDAEntry) resource).contentType().id().equals(contentTypeId)) {
                    result.add(TransformQuery.this.transform((CDAEntry) resource));
                  }
                }
                return result;
              }
            }
        );
  }

  /**
   * Retrieve all transformed entries from Contentful by the use of a callback.
   *
   * @return a callback containing a collection of transformed entries.
   * @throws CDAResourceNotFoundException if no such resource was found.
   * @throws IllegalStateException        if the transformed class could not be created.
   * @throws IllegalStateException        if the transformed class could not be accessed.
   */
  public CDACallback<Collection<Transformed>> all(CDACallback<Collection<Transformed>> callback) {
    return Callbacks.subscribeAsync(
        baseQuery()
            .all()
            .map(
                new Function<CDAArray, List<Transformed>>() {
                  @Override
                  public List<Transformed> apply(CDAArray array) {
                    final ArrayList<Transformed> result = new ArrayList<>(array.total());

                    for (final CDAResource resource : array.items) {
                      if (resource instanceof CDAEntry
                          && ((CDAEntry) resource).contentType().id().equals(contentTypeId)) {
                        result.add(TransformQuery.this.transform((CDAEntry) resource));
                      }
                    }
                    return result;
                  }
                }
            ),
        callback,
        client);
  }

  private ObserveQuery<CDAEntry> baseQuery() {
    return client.observe(CDAEntry.class).where(params);
  }

  private Transformed transform(CDAEntry entry) {
    final Transformed result;

    if (instanceCache.containsKey(entry.id())) {
      result = instanceCache.get(entry.id());
    } else {
      try {
        result = type.newInstance();
      } catch (Exception e) {
        throw new IllegalStateException("Cannot transform entry " + entry + "  to type "
            + type.getCanonicalName());
      }

      instanceCache.put(entry.id(), result);

      for (final Field field : type.getDeclaredFields()) {
        final ContentfulField annotation = field.getAnnotation(ContentfulField.class);
        if (annotation != null) {
          transformFieldAnnotation(entry, result, field, annotation);
        } else {
          final ContentfulSystemField systemField =
              field.getAnnotation(ContentfulSystemField.class);
          if (systemField != null) {
            transformSystemFieldAnnotation(entry, result, field, systemField);
          }
        }
      }
    }

    return result;
  }

  private void transformFieldAnnotation(CDAEntry entry, Transformed result, Field field,
                                        ContentfulField annotation) {
    if (!field.isAccessible()) {
      field.setAccessible(true);
    }

    final String key;
    if (annotation.value().isEmpty()) {
      key = field.getName();
    } else {
      key = annotation.value();
    }

    final String locale;
    if (annotation.locale().isEmpty()) {
      locale = entry.defaultLocale;
    } else {
      locale = annotation.locale();
    }

    try {
      final Object value = entry.getField(locale, key);
      if (value instanceof CDAEntry
          && ((CDAEntry) value).contentType().id().equals(contentTypeId)) {
        final CDAEntry fieldEntry = (CDAEntry) value;
        if (!instanceCache.containsKey(fieldEntry.id())) {
          transform(fieldEntry);
        }

        field.set(result, instanceCache.get(fieldEntry.id()));
      } else {
        field.set(result, value);
      }
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Cannot set custom field " + key + ".");
    }
  }

  private void transformSystemFieldAnnotation(CDAEntry entry, Transformed result, Field field,
                                              ContentfulSystemField annotation) {
    if (!field.isAccessible()) {
      field.setAccessible(true);
    }

    final String key;
    if (annotation.value().isEmpty()) {
      key = field.getName();
    } else {
      key = annotation.value();
    }

    try {
      field.set(result, entry.getAttribute(key));
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Cannot set custom system field " + key + ".");
    }
  }
}
