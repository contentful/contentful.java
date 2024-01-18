package com.contentful.java.cda;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    Class<?>[] additionalModelHints() default {};
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

  /**
   * This annotation marks the metadata field.
   * <p>
   * Metadata is returned in the {@link CDAEntry#metadata()} method.
   * <p>
   */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface ContentfulMetadata {
    String value() default "metadata";
  }
  private final String contentTypeId;

  private final Map<String, Object> instanceCache = new HashMap<>();
  private final Map<String, Class<?>> customClassByContentTypeIdCache = new HashMap<>();

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
        } else {
          final ContentfulMetadata metadata = field.getAnnotation(ContentfulMetadata.class);
          if (metadata != null) {
            parseMetadataAnnotation(metadata);
          }
        }
      }
    }

    createCustomClassCache(type);
  }

  private void createCustomClassCache(Class<?> seedType) {
    final ContentfulEntryModel seedAnnotation = seedType.getAnnotation(ContentfulEntryModel.class);
    if (seedAnnotation == null) {
      throw new IllegalStateException("Custom class has to be annotated with "
          + "'ContentfulEntryModel' annotation");
    }

    if (customClassByContentTypeIdCache.containsKey(seedAnnotation.value())) {
      // ignore already existing content type
      return;
    }

    for (final Class<?> model : seedAnnotation.additionalModelHints()) {
      final ContentfulEntryModel modelAnnotation = model.getAnnotation(ContentfulEntryModel.class);
      if (modelAnnotation != null) {
        customClassByContentTypeIdCache.put(modelAnnotation.value(), model);
      }
    }

    final String contentTypeId = seedAnnotation.value();
    customClassByContentTypeIdCache.put(contentTypeId, seedType);

    // loop through fields to find another custom content type
    for (final Field field : seedType.getDeclaredFields()) {
      if (isFieldACollection(field)) {
        final Class<?> itemType = getCollectionFieldEntryType(field);
        if (itemType == null) {
          // couldn't derive generic type from Collection
          continue;
        }

        final ContentfulEntryModel itemAnnotation = itemType.getAnnotation(
            ContentfulEntryModel.class
        );
        if (itemAnnotation != null) {
          createCustomClassCache(itemType);
        }
      } else {
        final ContentfulEntryModel fieldCustomAnnotation = field.getType().getAnnotation(
            ContentfulEntryModel.class
        );
        if (fieldCustomAnnotation != null) {
          createCustomClassCache(field.getType());
        }
      }
    }
  }

  private Class<?> getCollectionFieldEntryType(Field field) {
    // This method guesses the type of a generic collection by inspecting its string representation.
    // This can break with new JVM installations and in this case it is recommended to either update
    // this implementation or, even better, provide possible types in one of the top level content
    // type annotations.

    final boolean wasAccessible = field.isAccessible();
    try {
      field.setAccessible(true);
      final String genericType = field.getGenericType().toString();
      final String genericSubTypeRegex = "^[.\\p{Alpha}]+<(?<subtype>.+)>$";
      final Pattern pattern = Pattern.compile(genericSubTypeRegex);
      final Matcher matcher = pattern.matcher(genericType);
      if (matcher.matches()) {
        return this.getClass().getClassLoader().loadClass(matcher.group("subtype"));
      } else {
        return null;
      }
    } catch (Throwable t) {
      // Could not find the type of the elements of the list at "field".
      return null;
    } finally {
      field.setAccessible(wasAccessible);
    }
  }

  private boolean isFieldACollection(Field field) {
    return Collection.class.isAssignableFrom(field.getType());
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

  private void parseMetadataAnnotation(ContentfulMetadata annotation) {
    select(annotation.value());
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
            @SuppressWarnings("unchecked")
            public Transformed apply(CDAEntry entry) throws Exception {
              return (Transformed) TransformQuery.this.transform(entry);
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
              @SuppressWarnings("unchecked")
              public Collection<Transformed> apply(CDAArray array) {
                final ArrayList<Transformed> result = new ArrayList<>(array.total());

                for (final CDAResource resource : array.items) {
                  if (resource instanceof CDAEntry
                      && ((CDAEntry) resource).contentType().id().equals(contentTypeId)) {
                    result.add((Transformed) TransformQuery.this.transform((CDAEntry) resource));
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
                  @SuppressWarnings("unchecked")
                  public List<Transformed> apply(CDAArray array) {
                    final ArrayList<Transformed> result = new ArrayList<>(array.total());

                    for (final CDAResource resource : array.items) {
                      if (resource instanceof CDAEntry
                          && ((CDAEntry) resource).contentType().id().equals(contentTypeId)) {
                        result.add((Transformed) TransformQuery.this.transform(
                            (CDAEntry) resource)
                        );
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

  private Object transform(CDAEntry entry) {
    final Object result;

    if (!customClassByContentTypeIdCache.containsKey(entry.contentType().id())) {
      return entry;
    }

    final Class<?> type = customClassByContentTypeIdCache.get(entry.contentType().id());

    if (instanceCache.containsKey(entry.id())) {
      result = instanceCache.get(entry.id());
    } else {
      try {
        result = type.newInstance();
      } catch (Exception e) {
        throw new IllegalStateException("Cannot transform entry " + entry + "  to type "
            + type.getCanonicalName(), e);
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
          } else {
            final ContentfulMetadata metadata =
                    field.getAnnotation(ContentfulMetadata.class);
            if (metadata != null) {
              transformMetadataAnnotation(entry, result, field, metadata);
            }
          }
        }
      }

      for (final Field field : type.getSuperclass().getDeclaredFields()) {
        final ContentfulField annotation = field.getAnnotation(ContentfulField.class);
        if (annotation != null) {
          transformFieldAnnotation(entry, result, field, annotation);
        } else {
          final ContentfulSystemField systemField =
                  field.getAnnotation(ContentfulSystemField.class);
          if (systemField != null) {
            transformSystemFieldAnnotation(entry, result, field, systemField);
          } else {
            final ContentfulMetadata metadata =
                    field.getAnnotation(ContentfulMetadata.class);
            if (metadata != null) {
              transformMetadataAnnotation(entry, result, field, metadata);
            }
          }
        }
      }
    }

    return result;
  }

  private void transformFieldAnnotation(
      CDAEntry entry,
      Object result,
      Field field,
      ContentfulField annotation) {
    final boolean wasAccessible = field.isAccessible();
    field.setAccessible(true);

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
      if (value instanceof CDAEntry) {
        final CDAEntry fieldEntry = (CDAEntry) value;
        if (!instanceCache.containsKey(fieldEntry.id())) {
          transform(fieldEntry);
        }

        field.set(result, instanceCache.get(fieldEntry.id()));
      } else if (value instanceof Collection) {
        @SuppressWarnings("unchecked") final Collection<Object> collection = (Collection) value;

        final ArrayList<Object> transformedList = new ArrayList<>(collection.size());
        for (final Object element : collection) {
          if (element instanceof CDAEntry) {
            final CDAEntry collectionEntry = (CDAEntry) element;
            if (customClassByContentTypeIdCache.containsKey(collectionEntry.contentType().id())) {
              transformedList.add(transform(collectionEntry));
            } else {
              transformedList.add(element);
            }
          } else {
            transformedList.add(element);
          }

          field.set(result, transformedList);
        }
      } else {
        field.set(result, value);
      }
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Cannot set custom field " + key + ".");
    } finally {
      field.setAccessible(wasAccessible);
    }
  }

  private void transformSystemFieldAnnotation(CDAEntry entry, Object result, Field field,
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

  private void transformMetadataAnnotation(CDAEntry entry, Object result, Field field,
                                              ContentfulMetadata annotation) {
    if (!field.isAccessible()) {
      field.setAccessible(true);
    }

    final String key = annotation.value();

    try {
      field.set(result, entry.metadata());
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Cannot set " + key + ".");
    }
  }
}
