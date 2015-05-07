package com.contentful.java.cda.lib

import org.apache.commons.io.FileUtils
import java.io.File
import com.squareup.okhttp.mockwebserver.RecordedRequest
import org.apache.commons.io.IOUtils

/**
 * Utils.
 */
class TestUtils {
    companion object {
        fun fileToString(fileName: String): String =
                FileUtils.readFileToString(File("src/test/resources/${fileName}"), "UTF-8")
    }
}
