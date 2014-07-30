package com.contentful.java.api;

import com.contentful.java.model.*;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

import java.util.Map;

/**
 * An interface being used internally to create a client via
 * {@link retrofit.RestAdapter#create(Class)}.
 */
public interface CDAService {
    /**
     * Assets endpoint.
     *
     * @param space    String representing the Space key.
     * @param callback {@link retrofit.Callback} instance to be used.
     */
    @GET("/spaces/{space}/assets")
    void fetchAssets(
            @Path("space") String space,
            Callback<CDAListResult> callback
    );

    /**
     * Assets endpoint with a query.
     *
     * @param space    String representing the Space key.
     * @param query    {@link java.util.Map} instance containing keys & values for this query.
     * @param callback {@link retrofit.Callback} instance to be used.
     */
    @GET("/spaces/{space}/assets")
    void fetchAssetsMatching(
            @Path("space") String space,
            @QueryMap Map<String, String> query,
            Callback<CDAListResult> callback
    );

    /**
     * Asset endpoint with UID.
     *
     * @param space      String representing the Space key.
     * @param identifier String representing the Asset UID.
     * @param callback   {@link retrofit.Callback} instance to be used.
     */
    @GET("/spaces/{space}/assets/{identifier}")
    void fetchAssetWithIdentifier(
            @Path("space") String space,
            @Path("identifier") String identifier,
            Callback<CDAAsset> callback
    );

    /**
     * Content Types endpoint.
     *
     * @param space    String representing the Space key.
     * @param callback {@link retrofit.Callback} instance to be used.
     */
    @GET("/spaces/{space}/content_types")
    void fetchContentTypes(
            @Path("space") String space,
            Callback<CDAListResult> callback
    );

    /**
     * Content Type endpoint with UID.
     *
     * @param space      String representing the Space key.
     * @param identifier String representing the Content Type UID.
     * @param callback   {@link retrofit.Callback} instance to be used.
     */
    @GET("/spaces/{space}/content_types/{identifier}")
    void fetchContentTypeWithIdentifier(
            @Path("space") String space,
            @Path("identifier") String identifier,
            Callback<CDAContentType> callback
    );

    /**
     * Entries endpoint.
     *
     * @param space    String representing the Space key.
     * @param callback {@link retrofit.Callback} instance to be used.
     */
    @GET("/spaces/{space}/entries")
    void fetchEntries(
            @Path("space") String space,
            Callback<CDAListResult> callback
    );

    /**
     * Entries endpoint with a query.
     *
     * @param space    String representing the Space key.
     * @param query    {@link java.util.Map} instance containing keys & values for this query.
     * @param callback {@link retrofit.Callback} instance to be used.
     */
    @GET("/spaces/{space}/entries")
    void fetchEntriesMatching(
            @Path("space") String space,
            @QueryMap Map<String, String> query,
            Callback<CDAListResult> callback
    );

    /**
     * Entry endpoint with UID.
     *
     * @param space      String representing the Space key.
     * @param identifier String representing the Asset UID.
     * @param callback   {@link retrofit.Callback} instance to be used.
     */
    @GET("/spaces/{space}/entries/{identifier}")
    void fetchEntryWithIdentifier(
            @Path("space") String space,
            @Path("identifier") String identifier,
            Callback<? extends CDAEntry> callback
    );

    /**
     * Space endpoint.
     *
     * @param space    String representing the Space key.
     * @param callback {@link retrofit.Callback} instance to be used.
     */
    @GET("/spaces/{space}")
    void fetchSpace(
            @Path("space") String space,
            Callback<CDASpace> callback
    );
}
