package com.contentful.java.cda;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import retrofit2.Response;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceFactoryUnitTest {

  CDAClient client;

  @Mock
  public CDASpace space;

  @Mock
  public Map<String, CDAContentType> types;

  @Before public void setup() {
    final Cache cache = new Cache();
    cache.setSpace(space);
    cache.setTypes(types);

    when(space.defaultLocale()).thenReturn(mock(CDALocale.class));

    client = new CDAClient(
        cache,
        mock(Executor.class),
        mock(CDAService.class),
        mock(CDAClient.Builder.class));
  }

  @Test
  public void testPreserveOrderOfElements() throws Exception {
    final CDAArray inputArray = new CDAArray();
    inputArray.items = new ArrayList<CDAResource>();
    for (int i = 0; i < 23; ++i) {
      inputArray.items().add(createCdaEntry(String.format("%03d", i)));
    }

    final Response<CDAArray> inputArrayResponse = Response.success(inputArray);
    final CDAArray outputArray = ResourceFactory.array(inputArrayResponse, client);

    assertThat(outputArray.entries().values()).containsAllIn(inputArray.items);
  }

  private CDAEntry createCdaEntry(String id) {
    final CDAEntry entry = new CDAEntry();
    entry.fields = new HashMap<String, Object>();
    entry.attrs = new HashMap<String, Object>();
    entry.attrs.put("type", CDAType.ENTRY.name());
    entry.attrs.put("id", id);
    entry.setContentType(new CDAContentType());
    entry.contentType().fields = new ArrayList<CDAField>();
    return entry;
  }
}