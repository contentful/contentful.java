package com.contentful.java.lib;

import com.contentful.java.model.CDAResource;

import java.io.*;

/**
 * Resource Utilities.
 */
public class ResourceUtils {
    /**
     * Serialize a resource and save it to a local file.
     * This method will perform file IO on the thread of the calling method.
     * This can also be used to save arrays.
     *
     * @param resource {@link com.contentful.java.model.CDAResource} or a subclass of it.
     * @param file     Valid {@link java.io.File} reference with valid write permission.
     * @throws java.io.IOException in case writing fails.
     */
    public static void saveResourceToFile(CDAResource resource, File file) throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);

            oos.writeObject(resource);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Read a previously saved serialized object and parse it.
     * This method will perform file IO on the thread of the calling method.
     *
     * @param file {@link java.io.File} reference with valid read permission.
     * @return {@link CDAResource} instance or a subclass of it, should be the same type as
     * the original object.
     * @throws IOException            in case reading fails.
     * @throws ClassNotFoundException in case the persisted data references a class which is no longer available.
     */
    public static CDAResource readResourceFromFile(File file) throws IOException, ClassNotFoundException {
        FileInputStream fis = null;
        ObjectInputStream oos = null;
        CDAResource result = null;

        try {
            fis = new FileInputStream(file);
            oos = new ObjectInputStream(fis);

            result = (CDAResource) oos.readObject();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
