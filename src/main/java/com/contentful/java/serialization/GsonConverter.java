package com.contentful.java.serialization;

import com.contentful.java.annotations.CDAFields;
import com.contentful.java.lib.Constants;
import com.contentful.java.model.CDAAsset;
import com.contentful.java.model.CDABaseItem;
import com.contentful.java.model.CDAEntry;
import com.contentful.java.model.CDAListResult;
import com.contentful.java.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * A {@link Converter} which uses GSON for serialization and deserialization of entities.
 */
public class GsonConverter implements Converter {
    private final Gson gson;
    private String encoding;

    /**
     * Create an instance using the supplied {@link Gson} object for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public GsonConverter(Gson gson) {
        this(gson, "UTF-8");
    }

    /**
     * Create an instance using the supplied {@link Gson} object for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use the specified encoding.
     */
    public GsonConverter(Gson gson, String encoding) {
        this.gson = gson;
        this.encoding = encoding;
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        String charset = "UTF-8";

        if (body.mimeType() != null) {
            charset = MimeUtil.parseCharset(body.mimeType());
        }

        InputStreamReader isr = null;

        try {
            isr = new InputStreamReader(body.in(), charset);

            // parse the stream using Gson
            Object result = gson.fromJson(isr, type);

            // do any additional processing of the result
            finalizeResult(result);

            return result;
        } catch (IOException e) {
            throw new ConversionException(e);
        } catch (JsonParseException e) {
            throw new ConversionException(e);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        try {
            return new JsonTypedOutput(gson.toJson(object).getBytes(encoding), encoding);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    private static class JsonTypedOutput implements TypedOutput {
        private final byte[] jsonBytes;
        private final String mimeType;

        JsonTypedOutput(byte[] jsonBytes, String encode) {
            this.jsonBytes = jsonBytes;
            this.mimeType = "application/json; charset=" + encode;
        }

        @Override
        public String fileName() {
            return null;
        }

        @Override
        public String mimeType() {
            return mimeType;
        }

        @Override
        public long length() {
            return jsonBytes.length;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write(jsonBytes);
        }
    }

    /**
     * Resolves links for all items contained within a {@link CDAListResult} object.
     *
     * @param listResult {@link CDAListResult} instance.
     * @see #resolveLinksWithReflection
     */
    private void resolveLinksForResult(CDAListResult listResult) {
        // prepare map of IDs for Assets & Entries
        final HashMap<String, CDAAsset> assetsMap = new HashMap<String, CDAAsset>();
        final HashMap<String, CDAEntry> entriesMap = new HashMap<String, CDAEntry>();

        if (listResult.includes != null) {
            // map all Assets
            if (listResult.includes.assets != null) {
                for (CDAAsset item : listResult.includes.assets) {
                    assetsMap.put(item.sys.id, item);
                }
            }

            // map all Entries
            if (listResult.includes.entries != null) {
                for (CDAEntry item : listResult.includes.entries) {
                    entriesMap.put(item.sys.id, item);
                }
            }
        }

        // map regular items as well
        List<? extends CDABaseItem> items = listResult.items;

        if (items.size() > 0) {
            for (CDABaseItem item : items) {
                if (Utils.isAsset(item)) {
                    assetsMap.put(item.sys.id, (CDAAsset) item);
                } else if (Utils.isEntry(item)) {
                    entriesMap.put(item.sys.id, (CDAEntry) item);
                }
            }
        }

        // Resolve
        resolveLinksWithReflection(listResult, new ResolveLinksProvider() {
            @Override
            public CDAAsset getAsset(String id) {
                return assetsMap.get(id);
            }

            @Override
            public CDAEntry getEntry(String id) {
                return entriesMap.get(id);
            }
        });
    }

    /**
     * Recursively resolve links for a given list result.
     * This method should initially be called with a {@link CDAListResult} parameter as the "parent", it
     * will then recursively iterate through it's declared fields and will seek for Links.
     * <p/>
     * Whenever a Link is encountered this method will attempt to resolve it using the passed
     * {@link ResolveLinksProvider} parameter which should provide a corresponding item given an object's UID,
     * unless of course the object is unresolvable.
     * <p/>
     * Since this method uses Reflection to find links within class members during runtime, it makes use of
     * the {@link CDAFields} annotation to determine which member is the container of the object's CDA fields.
     *
     * @param parent   Object to be resolved.
     * @param provider {@link com.contentful.java.serialization.GsonConverter.ResolveLinksProvider} instance.
     */
    private void resolveLinksWithReflection(Object parent,
                                            ResolveLinksProvider provider) {

        if (parent instanceof CDAListResult) {
            CDAListResult listResult = (CDAListResult) parent;

            if (listResult.includes != null) {
                // resolve Assets
                if (listResult.includes.assets != null) {
                    for (CDABaseItem item : listResult.includes.assets) {
                        resolveLinksWithReflection(item, provider);
                    }
                }

                // resolve Entries
                if (listResult.includes.entries != null) {
                    for (CDABaseItem item : listResult.includes.entries) {
                        resolveLinksWithReflection(item, provider);
                    }
                }
            }

            // resolve regular items
            for (CDABaseItem item : listResult.items) {
                resolveLinksWithReflection(item, provider);
            }

            return;
        }

        // iterate through the object's fields and look for Links.
        Field[] fields = parent.getClass().getDeclaredFields();

        for (Field f : fields) {
            try {
                f.setAccessible(true);

                if (CDABaseItem.class.isAssignableFrom(f.getType())) {
                    CDABaseItem item = (CDABaseItem) f.get(parent);

                    if (item != null) {
                        CDABaseItem resolved = resolveCDABaseItem(item, provider);

                        if (resolved != null) {
                            // link was successfully resolved.
                            f.set(parent, resolved);
                        }
                    }
                } else if (f.isAnnotationPresent(CDAFields.class)) {
                    // fields object located, iterate through it's members.
                    resolveLinksWithReflection(f.get(parent), provider);
                } else if (List.class.isAssignableFrom(f.getType())) {
                    List list = (List) f.get(parent);

                    if (list != null) {
                        // resolve this list.
                        resolveList(list, provider);
                    }
                }
            } catch (IllegalAccessException ignore) {
            }
        }
    }

    /**
     * Helper method for {@link #resolveLinksWithReflection} for resolving a link
     * leading eventually to a {@link CDABaseItem} instance.
     *
     * @param item     {@link CDABaseItem} to be resolved.
     * @param provider {@link ResolveLinksProvider} as specified in the calling method.
     * @return A resolved {@link CDABaseItem} instance, null on failure.
     */
    private CDABaseItem resolveCDABaseItem(CDABaseItem item, ResolveLinksProvider provider) {
        CDABaseItem resolved = null;

        if (Constants.CDAType.Link.equals(Constants.CDAType.valueOf(item.sys.type))) {
            Constants.CDAType linkType = Constants.CDAType.valueOf(item.sys.linkType);

            String itemId = item.sys.id;

            if (Constants.CDAType.Asset.equals(linkType)) {
                resolved = provider.getAsset(itemId);
            } else if (Constants.CDAType.Entry.equals(linkType)) {
                resolved = provider.getEntry(itemId);
            }
        }

        return resolved;
    }

    /**
     * Helper method for {@link #resolveLinksWithReflection} for resolving a {@link java.util.List}
     * of {@link CDABaseItem} objects.
     * <p/>
     * Note this method <b>will</b> modify the {@link java.util.List} by replacing it's resolvable
     * children with resolved objects.
     *
     * @param list     {@link java.util.List} of {@link CDABaseItem} objects.
     * @param provider {@link ResolveLinksProvider} as specified in the calling method.
     */
    private void resolveList(List list, ResolveLinksProvider provider) {
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);

            if (o instanceof CDABaseItem) {
                CDABaseItem resolved = resolveCDABaseItem((CDABaseItem) o, provider);

                if (resolved != null) {
                    list.set(i, resolved); // todo tom make sure this is safe
                }
            }
        }
    }

    private void finalizeResult(Object result) {
        // resolve links for list results
        if (result instanceof CDAListResult) {
            resolveLinksForResult((CDAListResult) result);
        }
    }

    /**
     * Provider interface to be used with {@link #resolveLinksWithReflection}.
     */
    private static interface ResolveLinksProvider {
        /**
         * Look for an Asset by it's UID.
         *
         * @param id Asset UID.
         * @return {@link CDAAsset} instance, null when not found.
         */
        CDAAsset getAsset(String id);

        /**
         * Look for an Entry by it's UID.
         *
         * @param id Entry UID.
         * @return {@link CDAEntry} instance, null when not found.
         */
        CDAEntry getEntry(String id);
    }
}