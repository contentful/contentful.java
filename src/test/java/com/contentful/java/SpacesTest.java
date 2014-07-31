package com.contentful.java;

import com.contentful.java.api.CDAClient;
import com.contentful.java.lib.*;
import com.contentful.java.model.CDASpace;
import com.contentful.java.model.Locale;

/**
 * Test for fetching a Space.
 */
public class SpacesTest extends AbsTestCase {
    public void testFetchSpace() throws Exception {
        TestClientResult<CDASpace> result = new TestClientResult<CDASpace>();

        CDAClient customClient = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_fetch_space.json"));

        customClient.fetchSpace(new TestCallback<CDASpace>(result));

        result.cdl.await();
        verifyResultNotEmpty(result);

        assertEquals("Contentful Example API", result.value.name);
        assertEquals("Space", result.value.sys.type);
        assertEquals("cfexampleapi", result.value.sys.id);
        assertEquals(2, result.value.locales.size());

        // English
        Locale locale = result.value.locales.get(0);
        assertEquals(Constants.DEFAULT_LOCALE, locale.code);
        assertEquals("English", locale.name);
        assertTrue(locale.isDefault);

        // Klingon
        locale = result.value.locales.get(1);
        assertEquals("tlh", locale.code);
        assertEquals("Klingon", locale.name);
        assertFalse(locale.isDefault);
    }
}
