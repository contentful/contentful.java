package com.contentful.java;

import com.contentful.java.lib.MockClient;
import com.contentful.java.lib.TestCallback;
import com.contentful.java.lib.TestClientFactory;
import com.contentful.java.model.CDAClient;
import com.contentful.java.model.CDASpace;
import com.contentful.java.model.Locale;
import retrofit.client.Response;

import java.util.concurrent.CountDownLatch;

/**
 * Created by tomxor on 30/07/14.
 */
public class SpacesTest extends AbsTestCase {
    public void testFetchSpace() throws Exception {
        CDAClient customClient = TestClientFactory.newInstanceWithClient(
                new MockClient("result_test_fetch_space.json"));

        CountDownLatch cdl = new CountDownLatch(1);

        final CDASpace[] result = new CDASpace[]{null};

        customClient.fetchSpace(new TestCallback<CDASpace>(cdl) {
            @Override
            protected void onSuccess(CDASpace cdaSpace, Response response) {
                result[0] = cdaSpace;
                super.onSuccess(cdaSpace, response);
            }
        });

        cdl.await();

        assertNotNull(result[0]);
        assertEquals("Contentful Example API", result[0].name);
        assertEquals("Space", result[0].sys.type);
        assertEquals("cfexampleapi", result[0].sys.id);
        assertEquals(2, result[0].locales.size());

        // English
        Locale locale = result[0].locales.get(0);
        assertEquals("en-US", locale.code);
        assertEquals("English", locale.name);
        assertTrue(locale.isDefault);

        // Klingon
        locale = result[0].locales.get(1);
        assertEquals("tlh", locale.code);
        assertEquals("Klingon", locale.name);
        assertFalse(locale.isDefault);
    }
}
