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

    /**
     * Fetch any type of resource from a Space. (BLOCKING)
     *
     * @param space String representing the Space key.
     * @param type  Type of resource to be fetched (i.e. "entries", "assets", ...).
     * @param query Map representing the query.
     * @return {@link CDAArray} result.
     */
    @GET("/spaces/{space}/{type}")
    CDAArray fetchArrayWithPathBlocking(
            @Path("space") String space,
            @Path("type") String type,
            @QueryMap Map<String, String> query
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
     * Asset endpoint with UID. (BLOCKING)
     *
     * @param space      String representing the Space key.
     * @param identifier String representing the Asset UID.
     * @return {@link CDAAsset} result.
     */
    @GET("/spaces/{space}/assets/{identifier}")
    CDAAsset fetchAssetWithIdentifierBlocking(
            @Path("space") String space,
            @Path("identifier") String identifier
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
     * Content Types endpoint. (BLOCKING)
     *
     * @param space String representing the Space key.
     * @return {@link CDAArray} result.
     */
    @GET("/spaces/{space}/content_types")
    CDAArray fetchContentTypesBlocking(
            @Path("space") String space
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
     * Content Type endpoint with UID. (BLOCKING)
     *
     * @param space      String representing the Space key.
     * @param identifier String representing the Content Type UID.
     * @return {@link CDAArray} result.
     */
    @GET("/spaces/{space}/content_types/{identifier}")
    CDAContentType fetchContentTypeWithIdentifierBlocking(
            @Path("space") String space,
            @Path("identifier") String identifier
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
     * Entry endpoint with UID. (BLOCKING)
     *
     * @param space      String representing the Space key.
     * @param identifier String representing the Asset UID.
     * @return {@link CDAEntry} result.
     */
    @GET("/spaces/{space}/entries/{identifier}")
    CDAEntry fetchEntryWithIdentifierBlocking(
            @Path("space") String space,
            @Path("identifier") String identifier
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
     * Space endpoint. (BLOCKING)
     *
     * @param space String representing the Space key.
     * @return {@link CDASpace} result.
     */
    @GET("/spaces/{space}")
    CDASpace fetchSpaceBlocking(
            @Path("space") String space
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
            SyncSpaceCallback callback
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
            SyncSpaceCallback callback);
}
