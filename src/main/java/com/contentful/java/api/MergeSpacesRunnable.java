package com.contentful.java.api;

import com.contentful.java.model.CDASyncedSpace;
import retrofit.client.Response;

/**
 * TBD
 */
class MergeSpacesRunnable implements Runnable {
    private final CDASyncedSpace originalSpace;
    private final CDASyncedSpace updatedSpace;
    private CDACallback<CDASyncedSpace> callback;
    private Response response;

    public MergeSpacesRunnable(CDASyncedSpace originalSpace,
                               CDASyncedSpace updatedSpace,
                               CDACallback<CDASyncedSpace> callback,
                               Response response) {

        this.originalSpace = originalSpace;
        this.updatedSpace = updatedSpace;
        this.callback = callback;
        this.response = response;
    }

    @Override
    public void run() {
/*
        ArrayList<CDABaseItem> items = new ArrayList<CDABaseItem>(originalSpace.items);

        for (int i = updatedSpace.items.size() - 1; i >= 0; i--) {
            CDABaseItem item = updatedSpace.items.get(i);
            Constants.CDAResourceType resourceType = Constants.CDAResourceType.valueOf(item.sys.type);

            if (Constants.CDAResourceType.DeletedAsset.equals(resourceType)) {
                item.sys.type = Constants.CDAResourceType.Asset.toString();
                items.remove(item);
            } else if (Constants.CDAResourceType.DeletedEntry.equals(resourceType)) {
                item.sys.type = Constants.CDAResourceType.Entry.toString();
                items.remove(item);
            } else if (Constants.CDAResourceType.Asset.equals(resourceType) ||
                    Constants.CDAResourceType.Entry.equals(resourceType)) {

                items.remove(item);
                items.add(0, item);
            }
        }

        updatedSpace.items = items;
        callback.success(updatedSpace, response);
*/
    }
}
