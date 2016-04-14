package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class ContentTypeTest extends BaseTest {
  @Test
  @Enqueue("demo/content_types_cat.json")
  public void fetchContentType() throws Exception {
    CDAContentType cat = client.fetch(CDAContentType.class).one("cat");
    assertThat(cat.name()).isEqualTo("Cat");
    assertThat(cat.displayField()).isEqualTo("name");
    assertThat(cat.description()).isEqualTo("Meow.");
    assertThat(cat.fields()).hasSize(8);
  }

  @Test
  @Enqueue({ "demo/content_types_cat.json", "demo/content_types_fake.json" })
  public void manuallyFetchedContentTypeIsCached() throws Exception {
    client.fetch(CDAContentType.class).one("cat");
    assertThat(client.cache.types()).hasSize(5);
    assertThat(client.cache.types()).doesNotContainKey("fake");

    CDAContentType fake = client.fetch(CDAContentType.class).one("fake");
    assertThat(client.cache.types()).hasSize(6);
    assertThat(client.cache.types().get(fake.id())).isSameAs(fake);
  }

  @Test
  @Enqueue(defaults = {
      "cda/space.json"
  }, value = {
      "cda/content_types_foo.json",
      "cda/entries.json",
      "cda/content_types_bar.json"
  })
  public void missingContentTypeIsFetchedAndCached() throws Exception {
    assertThat(client.cache.types()).isNull();
    CDAArray array = client.fetch(CDAEntry.class).all();
    CDAEntry foo = array.entries().get("3UpazZmO8g8iI0iWAMmGMS");
    assertThat(foo).isNotNull();

    CDAEntry bar = foo.getField("link");
    assertThat(bar).isNotNull();
    assertThat(bar.getField("name")).isEqualTo("bar");

    assertThat(client.cache.types()).containsKey("3lYaFZKDgQCUwWy6uEoQYi");
  }

  @Test(expected = RuntimeException.class)
  @Enqueue({
      "demo/entries_fake.json",
      "array_empty.json"
  })
  public void badTypeMappingThrows() throws Exception {
    try {
      client.fetch(CDAEntry.class).all();
    } catch (RuntimeException e) {
      assertThat(e.getMessage()).isEqualTo("Entry 'bar' has non-existing content type mapping 'foo'.");
      throw e;
    }
  }
}
