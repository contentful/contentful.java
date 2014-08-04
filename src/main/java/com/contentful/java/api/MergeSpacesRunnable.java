package com.contentful.java.api;

import com.contentful.java.model.CDAResource;
import com.contentful.java.model.CDASpace;
import com.contentful.java.model.CDASyncedSpace;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import static com.contentful.java.lib.Constants.CDAResourceType;

/**
 * TBD
 */
class MergeSpacesRunnable implements Callable<CDASyncedSpace> {
    private final CDASyncedSpace originalSpace;
    private final CDASyncedSpace updatedSpace;
    private CDACallback<CDASyncedSpace> callback;
    private Response response;
    private CDASpace space;

    public MergeSpacesRunnable(CDASyncedSpace originalSpace,
                               CDASyncedSpace updatedSpace,
                               CDACallback<CDASyncedSpace> callback,
                               Response response, CDASpace space) {

        this.originalSpace = originalSpace;
        this.updatedSpace = updatedSpace;
        this.callback = callback;
        this.response = response;
        this.space = space;
    }

    @Override
    public CDASyncedSpace call() throws Exception {
        if (originalSpace != null) {
            ArrayList<CDAResource> originalItems = new ArrayList<CDAResource>(originalSpace.getItems());
            ArrayList<CDAResource> updatedItems = updatedSpace.getItems();

            for (int i = updatedItems.size() - 1; i >= 0; i--) {
                CDAResource item = updatedItems.get(i);
                CDAResourceType resourceType = CDAResourceType.valueOf((String) item.getSys().get("type"));

                if (CDAResourceType.DeletedAsset.equals(resourceType)) {
                    item.getSys().put("type", CDAResourceType.Asset.toString());
                    originalItems.remove(item);
                } else if (CDAResourceType.DeletedEntry.equals(resourceType)) {
                    item.getSys().put("type", CDAResourceType.Entry.toString());
                    originalItems.remove(item);
                } else if (CDAResourceType.Asset.equals(resourceType) ||
                        CDAResourceType.Entry.equals(resourceType)) {

                    originalItems.remove(item);
                    originalItems.add(0, item);
                }
            }

            updatedItems.clear();
            updatedItems.addAll(originalItems);
        }

        CDASyncedSpace result = null;

        try {
            result = new ArrayParser<CDASyncedSpace>(updatedSpace, space).call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (callback != null && !callback.isCancelled()) {
            callback.success(result, response);
        }

        return result;
    }
}
