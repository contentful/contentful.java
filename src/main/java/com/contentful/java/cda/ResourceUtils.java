/*
 * Copyright (C) 2014 Contentful GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.contentful.java.cda.lib;

import com.contentful.java.cda.model.CDAResource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Resource Utilities.
 */
public class ResourceUtils {
  private ResourceUtils() {
  }

  /**
   * Serialize a resource and save it to a local file.
   * This performs file IO on the thread of the calling method.
   * It is also possible to persist {@code CDAArray} instance using this method.
   *
   * @param resource {@code CDAResource} or a subclass of it
   * @param file valid {@code File} reference with valid write permission
   * @throws java.io.IOException in case writing fails
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
   * Read a previously persisted resource and create an object out of it.
   * This performs file IO on the thread of the calling method.
   *
   * @param file {@code File} reference with valid read permission
   * @return {@code CDAResource} instance or a subclass of it, should be the same type as
   * the originally persisted object
   * @throws IOException in case reading fails
   * @throws ClassNotFoundException in case the persisted data references a class which is no
   * longer available
   */
  public static CDAResource readResourceFromFile(File file)
      throws IOException, ClassNotFoundException {
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
