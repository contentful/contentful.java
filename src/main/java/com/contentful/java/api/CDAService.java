package com.contentful.java.api;

import com.contentful.java.model.*;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

import java.util.Map;

/**
 * An interface being used internally to create a client via
 * {@link retrofit.RestAdapter#create}.
 */
interface CDAService {
    /**
     * Assets endpoint.
     *
     * @param space    String representing the Space key.
     * @param callback {@link CDACallback} instance to be used.
     */
    @GET("/spaces/{space}/assets")
    void fetchAssets(
            @Path("space") String space,
            CDACallback<CDAArray> callback
    );

    /**
     * Assets endpoint with a query.
     *
     * @param space    String representing the Space key.
     * @param query    Map representing the query.
     * @param callback {@link CDACallback} instance to be used.
     */
    @GET("/spaces/{space}/assets")
    void fetchAssetsMatching(
            @Path("space") String space,
            @QueryMap Map<String, String> query,
            CDACallback<CDAArray> callback
    );

    /**
     * Asset endpoint with UID.
     *
     * @param space      String representing the Space key.
     * @param identifier String representing the Asset UID.
     * @param callback   {@link CDACallback} instance to be used.
     */
    @GET("/spaces/{space}/assets/{identifier}")
    void fetchAssetWithIdentifier(
            @Path("space") String space,
            @Path("identifier") String identifier,
            CDACallback<CDAAsset> callback
    );

    /**
     * Content Types endpoint.
     *
     * @param space    String representing the Space key.
     * @param callback {@link CDACallback} instance to be used.
     */
    @GET("/spaces/{space}/content_types")
    void fetchContentTypes(
            @Path("space") String space,
            CDACallback<CDAArray> callback
    );

    /**
     * Content Type endpoint with UID.
     *
     * @param space      String representing the Space key.
     * @param identifier String representing the Content Type UID.
     * @param callback   {@link CDACallback} instance to be used.
     */
    @GET("/spaces/{space}/content_types/{identifier}")
    void fetchContentTypeWithIdentifier(
            @Path("space") String space,
            @Path("identifier") String identifier,
            CDACallback<CDAContentType> callback
    );

    /**
     * Entries endpoint.
     *
     * @param space    String representing the Space key.
     * @param callback {@link CDACallback} instance to be used.
     */
    @GET("/spaces/{space}/entries")
    void fetchEntries(
            @Path("space") String space,
            CDACallback<CDAArray> callback
    );

    /**
     * Entries endpoint with a query.
     *
     * @param space    String representing the Space key.
     * @param query    Map representing the query.
     * @param callback {@link CDACallback} instance to be used.
     */
    @GET("/spaces/{space}/entries")
    void fetchEntriesMatching(
            @Path("space") String space,
            @QueryMap Map<String, String> query,
            CDACallback<CDAArray> callback
    );

    /**
     * Entry endpoint with UID.
     *
     * @param space      String representing the Space key.
     * @param identifier String representing the Asset UID.
     * @param callback   {@link CDACallback} instance to be used.
     */
    @GET("/spaces/{space}/entries/{identifier}")
    void fetchEntryWithIdentifier(
            @Path("space") String space,
            @Path("identifier") String identifier,
            CDACallback<? extends CDAEntry> callback
    );

    /**
     * Space endpoint.
     *
     * @param space    String representing the Space key.
     * @param callback {@link CDACallback} instance to be used.
     */
    @GET("/spaces/{space}")
    void fetchSpace(
            @Path("space") String space,
            CDACallback<CDASpace> callback
    );

    /**
     * Space Sync endpoint.
     *
     * @param space    String representing the Space key.
     * @param initial  Boolean indicating whether this is the initial sync request or not.
     * @param callback {@link CDACallback} instance to be used.
     */
    @GET("/spaces/{space}/sync")
    void performSynchronization(
            @Path("space") String space,
            @Query("initial") Boolean initial,
            CDACallback<CDASyncedSpace> callback
    );

    /**
     * Execute a request using a path determined at runtime.
     *
     * @param dynamicPath String representing the path.
     * @param callback    {@link CDACallback} instance to be used.
     */
    @GET("/{dynamic_path}")
    void fetchSyncedSpaceWithPath(
            @Path("dynamic_path") String dynamicPath,
            CDACallback<CDASyncedSpace> callback);

    /**
     * Fetch any type of resource from a Space.
     * This can be useful for when the type of resource to be fetched is determined at runtime.
     *
     * @param space    String representing the Space key.
     * @param type     Type of resource to be fetched (i.e. "entries", "assets", ...).
     * @param query    Map representing the query.
     * @param callback {@link CDACallback} instance to be used.
     */
    @GET("/spaces/{space}/{type}")
    void fetchArrayWithPath(
            @Path("space") String space,
            @Path("type") String type,
            @QueryMap Map<String, String> query,
            CDACallback<CDAArray> callback
    );
}
