package com.contentful.java.cda.rich;

import com.contentful.java.cda.ArrayResource;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.contentful.java.cda.ResourceUtils.ensureContentType;

/**
 * This factory will be used in order to create the {@see CDARichTextNode}-graph representation of
 * the Contentful data returned by a rich text - field.
 */
@SuppressWarnings("unchecked")
public class RichTextFactory {
  private static final int HEADING_LEVEL_1 = 1;
  private static final int HEADING_LEVEL_2 = 2;
  private static final int HEADING_LEVEL_3 = 3;
  private static final int HEADING_LEVEL_4 = 4;
  private static final int HEADING_LEVEL_5 = 5;
  private static final int HEADING_LEVEL_6 = 6;

  /**
   * Interface for resolving the type of a node by its raw representation.
   */
  private interface Resolver {
    CDARichNode resolve(Map<String, Object> raw);
  }

  /**
   * Resolves a block of rich text
   *
   * @param <T> a block to be resolved.
   */
  private static class BlockResolver<T extends CDARichBlock> implements Resolver {
    final Supplier<T> supplier;

    /**
     * Create a block resolver based on its given supplier.
     *
     * @param supplier an object to create more objects of type T.
     */
    BlockResolver(Supplier<T> supplier) {
      this.supplier = supplier;
    }

    /**
     * This method is called in order to try to create  rich text block node from a raw map
     * representation.
     *
     * @param raw representation of the block node coming from Contentful.
     * @return the rich node if resolving was successful.
     */
    @Override public CDARichNode resolve(Map<String, Object> raw) {
      final T resolved = getCDAType(raw);

      final List<Map<String, Object>> contents = (List<Map<String, Object>>) raw.get("content");
      for (final Map<String, Object> rawNode : contents) {
        final CDARichNode resolvedNode = resolveRichNode(rawNode);
        if (resolvedNode != null) {
          resolved.content.add(resolvedNode);
        }
      }
      return resolved;
    }

    /**
     * Convenience method to try and find out the type of the given raw map representation.
     *
     * @param raw a map coming from Contentful, parsed from the json response.
     * @return a new node based on the type of T.
     */
    T getCDAType(Map<String, Object> raw) {
      return supplier.get();
    }
  }

  /**
   * Resolve only headings from Contentful.
   */
  private static class HeadingResolver extends BlockResolver<CDARichHeading> {
    final int level;

    /**
     * Create resolver using the given heading.
     *
     * @param level the level of the headings nesting. Should be positive and less then 7.
     */
    HeadingResolver(int level) {
      super(() -> new CDARichHeading(level));
      this.level = level;
    }
  }

  /**
   * Simple interface for providing an instance based on a type.
   *
   * @param <T> the type an instance should be created for.
   */
  private interface SupplierWithData<T> {
    /**
     * Create an object of type T.
     *
     * @param data the initialization data needed.
     * @return An instance of type T.
     */
    T get(Object data);
  }

  /**
   * Resolves a block containing more data.
   *
   * @param <T> Which type should the block be?
   */
  private static class BlockAndDataResolver<T extends CDARichBlock>
      extends BlockResolver<T> {
    final SupplierWithData<T> supplier;
    final String dataFieldKey;

    /**
     * Create the resolver.
     *
     * @param supplier     how to generate an object of type T?
     * @param dataFieldKey what other keys to be filtered?
     */
    BlockAndDataResolver(SupplierWithData<T> supplier, String dataFieldKey) {
      super(null);
      this.supplier = supplier;
      this.dataFieldKey = dataFieldKey;
    }

    /**
     * Create an object of T
     *
     * @param raw a map coming from Contentful, parsed from the json response.
     * @return an object of Type T.
     */
    @Override
    T getCDAType(Map<String, Object> raw) {
      return supplier.get(raw.get(dataFieldKey));
    }
  }

  private static final Map<String, Resolver> RESOLVER_MAP = new HashMap<>();

  static {
    // add leafs
    RESOLVER_MAP.put("text", raw -> new CDARichText(
        (CharSequence) raw.get("value"),
        resolveMarks((List<Map<String, Object>>) raw.get("marks"))
    ));
    RESOLVER_MAP.put("hr", raw -> new CDARichHorizontalRule());

    // add blocks
    RESOLVER_MAP.put("blockquote", new BlockResolver<>(CDARichQuote::new));
    RESOLVER_MAP.put("paragraph", new BlockResolver<>(CDARichParagraph::new));
    RESOLVER_MAP.put("document", new BlockResolver<>(CDARichDocument::new));
    RESOLVER_MAP.put("list-item", new BlockResolver<>(CDARichListItem::new));
    RESOLVER_MAP.put("ordered-list", new BlockResolver<>(CDARichOrderedList::new));
    RESOLVER_MAP.put("unordered-list", new BlockResolver<>(CDARichUnorderedList::new));
    RESOLVER_MAP.put("hyperlink", new BlockAndDataResolver<>(CDARichHyperLink::new, "data"));
    RESOLVER_MAP.put("embedded-entry-block",
        new BlockAndDataResolver<>(CDARichEmbeddedLink::new, "data"));
    RESOLVER_MAP.put("heading-1", new HeadingResolver(HEADING_LEVEL_1));
    RESOLVER_MAP.put("heading-2", new HeadingResolver(HEADING_LEVEL_2));
    RESOLVER_MAP.put("heading-3", new HeadingResolver(HEADING_LEVEL_3));
    RESOLVER_MAP.put("heading-4", new HeadingResolver(HEADING_LEVEL_4));
    RESOLVER_MAP.put("heading-5", new HeadingResolver(HEADING_LEVEL_5));
    RESOLVER_MAP.put("heading-6", new HeadingResolver(HEADING_LEVEL_6));
  }

  /**
   * Walk through the given array and resolve all rich text fields.
   *
   * @param array  the array to be walked.
   * @param client the client to be used if updating of types is needed.
   */
  public static void resolveRichTextField(ArrayResource array, CDAClient client) {
    for (CDAEntry entry : array.entries().values()) {
      ensureContentType(entry, client);
      for (CDAField field : entry.contentType().fields()) {
        if ("RichText".equals(field.type())) {
          resolveRichDocument(entry, field);
          resolveRichLink(array, entry, field);
        }
      }
    }
  }

  /**
   * Resolve all children of the top most document block.
   *
   * @param entry the entry to contain the field to be walked
   * @param field the id of the field to be walked.
   */
  @SuppressWarnings("unchecked")
  private static void resolveRichDocument(CDAEntry entry, CDAField field) {
    final Map<String, Object> rawValue = (Map<String, Object>) entry.rawFields().get(field.id());
    if (rawValue == null) {
      return;
    }

    for (final String locale : rawValue.keySet()) {
      final Map<String, Object> raw = (Map<String, Object>) rawValue.get(locale);
      if (raw == null) {
        continue;
      }

      entry.setField(locale, field.id(), RESOLVER_MAP.get("document").resolve(raw));
    }
  }

  /**
   * Specific method for resolving rich text marks.
   *
   * @param rawMarks the json responded map from Contentful
   * @return objectified and parsed objects.
   */
  static List<CDARichMark> resolveMarks(List<Map<String, Object>> rawMarks) {
    final List<CDARichMark> marks = new ArrayList<>(rawMarks.size());
    for (final Map<String, Object> rawMark : rawMarks) {
      final String type = (String) rawMark.get("type");
      if ("bold".equals(type)) {
        marks.add(new CDARichMark.CDARichMarkBold());
      } else if ("italic".equals(type)) {
        marks.add(new CDARichMark.CDARichMarkItalic());
      } else if ("underline".equals(type)) {
        marks.add(new CDARichMark.CDARichMarkUnderline());
      } else if ("code".equals(type)) {
        marks.add(new CDARichMark.CDARichMarkCode());
      } else {
        marks.add(new CDARichMark.CDARichMarkCustom(type));
      }
    }
    return marks;
  }

  /**
   * Resolve one node.
   *
   * @param rawNode the map response from Contentful
   * @return a CDARichNode from this SDK.
   */
  static CDARichNode resolveRichNode(Map<String, Object> rawNode) {
    final String type = (String) rawNode.get("nodeType");
    if (RESOLVER_MAP.containsKey(type)) {
      return RESOLVER_MAP.get(type).resolve(rawNode);
    } else {
      return null;
    }
  }

  /**
   * Resolve all links if possible. If linked to entry is not found, null it's field.
   *
   * @param array the array containing the complete response
   * @param entry the entry to be completed.
   * @param field the field pointing to a link.
   */
  private static void resolveRichLink(ArrayResource array, CDAEntry entry, CDAField field) {
    final Map<String, Object> rawValue = (Map<String, Object>) entry.rawFields().get(field.id());
    if (rawValue == null) {
      return;
    }

    for (final String locale : rawValue.keySet()) {
      final CDARichDocument document = entry.getField(locale, field.id());
      for (final CDARichNode node : document.getContent()) {
        resolveOneLink(array, field, locale, node);
      }
    }
  }

  /**
   * Link found, resolve it.
   *
   * @param array  the complete response from Contentful.
   * @param field  the field containing a link.
   * @param locale the locale of the link to be updated.
   * @param node   the node build from the response.
   */
  private static void resolveOneLink(ArrayResource array, CDAField field, String locale,
                                     CDARichNode node) {
    if (node instanceof CDARichHyperLink) {
      final CDARichHyperLink link = (CDARichHyperLink) node;
      final Map<String, Object> data = (Map<String, Object>) link.data;
      final Object target = data.get("target");

      if (target instanceof Map) {
        if (isLink(target)) {
          final Map<String, Object> map = (Map<String, Object>) target;
          final Map<String, Object> sys = (Map<String, Object>) map.get("sys");
          final String linkType = (String) sys.get("linkType");
          final String id = (String) sys.get("id");

          if ("Asset".equals(linkType)) {
            link.data = array.assets().get(id);
          } else if ("Entry".equals(linkType)) {
            link.data = array.entries().get(id);
          }

        } else {
          throw new IllegalStateException("Could not parse content of data field '"
              + field.id() + "' for locale '" + locale + "' at node '" + node
              + "'. Please check your content type model.");
        }
      } else if (target == null && data.containsKey("uri")) {
        link.data = data.get("uri");
      }
    } else if (node instanceof CDARichParagraph) {
      for (final CDARichNode child : ((CDARichParagraph) node).getContent()) {
        resolveOneLink(array, field, locale, child);
      }
    }
  }

  /**
   * Is the give object a link of any kind?
   */
  private static boolean isLink(Object data) {
    try {
      final Map<String, Object> map = (Map<String, Object>) data;
      final Map<String, Object> sys = (Map<String, Object>) map.get("sys");
      final String type = (String) sys.get("type");
      final String linkType = (String) sys.get("linkType");
      final String id = (String) sys.get("id");

      if ("Link".equals(type)
          && ("Entry".equals(linkType) || "Asset".equals(linkType)
          && id != null)) {
        return true;
      }
    } catch (ClassCastException cast) {
      return false;
    }
    return false;
  }
}