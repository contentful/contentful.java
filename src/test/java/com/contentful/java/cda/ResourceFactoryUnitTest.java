package com.contentful.java.cda;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

import retrofit2.Response;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceFactoryUnitTest {

  CDAClient client;

  @Mock
  public Cache cache;

  @Before public void setup() {
    when(cache.defaultLocale()).thenReturn(mock(CDALocale.class));

    client = new CDAClient(
        cache,
        mock(Executor.class),
        mock(CDAService.class),
        mock(CDAClient.Builder.class));
  }

  @Test
  public void testPreserveOrderOfElements() {
    final CDAArray inputArray = new CDAArray();
    inputArray.items = new ArrayList<>();
    for (int i = 0; i < 23; ++i) {
      inputArray.items().add(createCdaEntry(String.format("%03d", i)));
    }

    final Response<CDAArray> inputArrayResponse = Response.success(inputArray);
    final CDAArray outputArray = ResourceFactory.array(inputArrayResponse, client);

    assertThat(outputArray.entries().values()).containsAllIn(inputArray.items);
  }

  private CDAEntry createCdaEntry(String id) {
    final CDAEntry entry = new CDAEntry();
    entry.fields = new HashMap<>();
    entry.attrs = new HashMap<>();
    entry.attrs.put("type", CDAType.ENTRY.name());
    entry.attrs.put("id", id);
    entry.setContentType(new CDAContentType());
    entry.contentType().fields = new ArrayList<>();
    return entry;
  }
}