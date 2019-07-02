package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;
import com.contentful.java.cda.lib.TestCallback;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class EntryTest extends BaseTest {
  @Test(expected = CDAResourceNotFoundException.class)
  @Enqueue("array_empty.json")
  public void fetchNonExistingReturnsNull() {
    try {
      client.fetch(CDAEntry.class).one("foo");
    } catch (NullPointerException e) {
      assertThat(e.getMessage()).isEqualTo("Could not find id 'foo' of type 'CDAEntry'.");
      throw e;
    }
  }

  @Test
  @Enqueue("array_empty.json")
  public void fetchNonExistingEntryThrowsError() throws InterruptedException {
    final Object result[] = new Object[]{new Object()};
    final Object error[] = new Object[]{new Object()};
    final CountDownLatch latch = new CountDownLatch(1);

    client.observe(CDAEntry.class)
        .one("foo")
        .subscribe(
            entry -> {
              result[0] = entry;
              error[0] = null;

              latch.countDown();
            }, throwable -> {
              result[0] = null;
              error[0] = throwable;

              latch.countDown();
            }
        );

    latch.await(1, TimeUnit.SECONDS);

    assertThat(result[0]).isNull();
    assertThat(error[0]).isNotNull();
    assertThat(error[0]).isInstanceOf(CDAResourceNotFoundException.class);
    final CDAResourceNotFoundException notFoundException = (CDAResourceNotFoundException) error[0];
    final String message = notFoundException.getMessage();
    assertThat(message).isEqualTo("Could not find id 'foo' of type 'CDAEntry'.");
  }

  @Test
  @Enqueue("array_empty.json")
  public void fetchNonExistingEntryInvokesSuccessWithNull() throws InterruptedException {
    TestCallback<CDAEntry> callback = client.fetch(CDAEntry.class)
        .one("foo", new TestCallback<>())
        .await();

    assertThat(callback.error()).isNotNull();
    final String message = callback.error().getMessage();
    assertThat(message).isEqualTo("Could not find id 'foo' of type 'CDAEntry'.");

    assertThat(callback.result()).isNull();
  }

  @Test
  @Enqueue("demo/entries_nofields.json")
  public void entryNoFields() {
    CDAEntry foo = client.fetch(CDAEntry.class).one("foo");
    assertThat(foo).isNotNull();
    assertThat(foo.fields).isEmpty();
  }

  @Test
  @Enqueue("demo/entries_nyancat.json")
  public void entryContentType() {
    CDAEntry entry = client.fetch(CDAEntry.class).one("nyancat");
    assertThat(entry.contentType()).isNotNull();
  }

  @Test
  @Enqueue("demo/entries_nyancat.json")
  public void fetchEntry() {
    assertNyanCat(client.fetch(CDAEntry.class).one("nyancat"));
  }

  @Test
  @Enqueue("demo/entries_nyancat.json")
  public void fetchEntryAsync() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final CDAEntry[] result = {null};

    client.fetch(CDAEntry.class).one("nyancat", new CDACallback<CDAEntry>() {
      @Override protected void onSuccess(CDAEntry entry) {
        result[0] = entry;
        latch.countDown();
      }

      @Override protected void onFailure(Throwable error) {
        latch.countDown();
      }
    });

    latch.await(1, TimeUnit.SECONDS);
    assertThat(result[0]).isNotNull();
    assertNyanCat(result[0]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void populationThrowsOnZeroLimit() {
    client.populateContentTypeCache(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void populationThrowsOnThought() {
    client.populateContentTypeCache(1001);
  }

  @Test(expected = IllegalArgumentException.class)
  public void observePopulationThrowsOnZeroLimit() {
    client.observeContentTypeCachePopulation(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void observePopulationThrowsOnThought() {
    client.observeContentTypeCachePopulation(1001);
  }

  @Test
  @Enqueue(
      defaults = {
          "demo/locales.json",
          "content_types/populate_cache_simple.json"
      },
      value = {
          "content_types/populate_cache_simple.json"
      }
  )
  public void populateAllContentTypesSinglePage() {
    final int contentTypesCached = client.populateContentTypeCache();

    assertThat(contentTypesCached).isEqualTo(3);

    final Map<String, CDAContentType> types = client.cache.types();
    assertThat(types.size()).isEqualTo(3);

    final CDAContentType first = types.get("001");
    assertThat(first.fields.size()).isEqualTo(3);
    assertThat(first.fields.get(0).id).isEqualTo("first");
    assertThat(first.fields.get(0).type).isEqualTo("Symbol");

    final CDAContentType second = types.get("002");
    assertThat(second.fields.size()).isEqualTo(3);
    assertThat(second.fields.get(0).id).isEqualTo("first");
    assertThat(second.fields.get(0).type).isEqualTo("Symbol");

    final CDAContentType third = types.get("003");
    assertThat(third.fields.size()).isEqualTo(3);
    assertThat(third.fields.get(0).id).isEqualTo("first");
    assertThat(third.fields.get(0).type).isEqualTo("Symbol");
  }

  @Test
  @Enqueue(
      defaults = {
          "demo/locales.json",
          "content_types/populate_cache_simple.json"
      },
      value = {
          "content_types/populate_cache_complex_p1.json",
          "content_types/populate_cache_complex_p2.json",
          "content_types/populate_cache_complex_p3.json"
      }
  )
  public void populateAllContentTypesMultiplePages() {
    int numberOfContentTypes = client.populateContentTypeCache(60);

    assertThat(numberOfContentTypes).isEqualTo(151);
    assertThat(client.cache.types().size()).isEqualTo(151);

    final Map<String, CDAContentType> types = client.cache.types();
    final CDAContentType first = types.get("001");
    assertThat(first.fields.size()).isEqualTo(3);
    assertThat(first.fields.get(0).id).isEqualTo("first");
    assertThat(first.fields.get(0).type).isEqualTo("Symbol");

    final CDAContentType second = types.get("079");
    assertThat(second.fields.size()).isEqualTo(3);
    assertThat(second.fields.get(0).id).isEqualTo("first");
    assertThat(second.fields.get(0).type).isEqualTo("Symbol");

    final CDAContentType third = types.get("151");
    assertThat(third.fields.size()).isEqualTo(3);
    assertThat(third.fields.get(0).id).isEqualTo("first");
    assertThat(third.fields.get(0).type).isEqualTo("Symbol");
  }

  @Test
  @Enqueue(
      defaults = {
          "demo/locales.json",
          "content_types/populate_cache_simple.json"
      },
      value = {
          "content_types/populate_cache_complex_p1.json",
          "content_types/populate_cache_complex_p2.json",
          "content_types/populate_cache_complex_p3.json",
          "content_types/populate_cache_last_entry.json"
      }
  )
  public void aPopulatedContentTypeCacheDoesNotToFetchContentTypes() {
    client.populateContentTypeCache(60);

    assertThat(client.cache.types().size()).isEqualTo(151);

    final CDAContentType lastContentType = client.cache.types().get("151");
    assertThat(lastContentType.fields.size()).isEqualTo(3);
    assertThat(lastContentType.fields.get(0).id).isEqualTo("first");
    assertThat(lastContentType.fields.get(0).type).isEqualTo("Symbol");

    final CDAEntry lastEntry = client.fetch(CDAEntry.class).one("151");

    assertThat(lastEntry).isNotNull();
    assertThat(lastEntry.contentType().id()).isEqualTo(lastContentType.id());
  }

  @Test
  @Enqueue("demo/entries.json")
  public void fetchAllEntries() {
    CDAArray array = client.fetch(CDAEntry.class).all();
    assertThat(array.items()).hasSize(11);
    assertThat(array.assets()).hasSize(4);
    assertThat(array.entries()).hasSize(11);

    assertThat(array.total()).isEqualTo(11);
    assertThat(array.skip()).isEqualTo(0);
    assertThat(array.limit()).isEqualTo(100);

    for (CDAEntry entry : array.entries().values()) {
      assertThat(entry.contentType()).isNotNull();
    }

    CDAEntry nyanCat = array.entries().get("nyancat");
    assertThat(nyanCat).isNotNull();
    assertNyanCat(nyanCat);
  }

  private void assertNyanCat(CDAEntry entry) {
    assertThat(entry.id()).isEqualTo("nyancat");
    assertThat(entry.<String>getField("name")).isEqualTo("Nyan Cat");
    assertThat(entry.<String>getField("color")).isEqualTo("rainbow");
    assertThat(entry.<String>getField("birthday")).isEqualTo("2011-04-04T22:00:00+00:00");
    assertThat(entry.<Double>getField("lives")).isEqualTo(1337.0);

    List<String> likes = entry.getField("likes");
    assertThat(likes).containsExactly("rainbows", "fish");

    Object bestFriend = entry.getField("bestFriend");
    assertThat(bestFriend).isInstanceOf(CDAEntry.class);
    assertThat(entry).isSameAs(((CDAEntry) bestFriend).getField("bestFriend"));

    // Localization
    assertThat(entry.defaultLocale).isEqualTo("en-US");
    assertThat(entry.<String>getField("color")).isEqualTo("rainbow");
    assertThat(entry.<Object>getField("non-existing-does-not-throw")).isNull();
  }

  @Test
  @Enqueue(
      defaults = {"arrays/locales.json", "arrays/content_types.json"},
      value = "arrays/entries.json"
  )
  public void arrayItemsContainOnlyTopLevelEntries() {
    CDAArray array =
        client.fetch(CDAEntry.class).where("content_type", "Jm9AuzgH8OyocaMQSMwKC").all();

    assertThat(array.items()).hasSize(1);
    assertThat(array.entries()).hasSize(2);
    assertThat(array.entries().values()).containsAllIn(array.items());

    CDAEntry parent = array.entries().get("7Avw18DWveMI60a0WWwyCi");
    assertThat(parent).isNotNull();

    CDAEntry child = parent.getField("child");
    assertThat(child).isNotNull();

    assertThat(array.items()).containsExactly(parent);
    assertThat(array.entries().values()).containsExactly(parent, child);
  }

  @Test
  @Enqueue(defaults = {
	  "cda/locales.json"
  }, value = {
	  "cda/content_types_foo.json",
	  "cda/entries_with_errors.json",
	  "cda/content_types_bar.json"
  })
  public void getAllEntriesWithErrors() {
	  CDAArray entries = client.fetch(CDAEntry.class).all();

	  assertThat(entries).isNotNull();
	  assertThat(entries.getErrors()).isNotNull();
	  assertThat(entries.getErrors()).hasSize(2);

	  entries.getErrors().forEach(error -> {
	    assertThat(error.getSys()).isNotNull();
	    assertThat(error.getSys().get("id")).isEqualTo("notResolvable");
	    assertThat(error.getSys().get("type")).isEqualTo("error");
	    assertThat(error.getDetails()).isNotNull();
	    assertThat(error.getDetails().get("type")).isEqualTo("Link");
	    assertThat(error.getDetails().get("linkType")).isEqualTo("ContentType");

	    int index = entries.getErrors().indexOf(error);
	    String itemId = entries.items().get(index).id();
	    assertThat(error.getDetails().get("id")).isEqualTo(itemId);
	  });
  }

  @Test
  @Enqueue(defaults = {
	  "cda/locales.json"
  }, value = {
	  "cda/content_types_foo.json",
	  "cda/content_types_bar.json"
  })
  public void setAllEntriesWithErrors() {
	  CDAArray entries = client.fetch(CDAEntry.class).all();
    List<CDAError> errors = Arrays.asList(new CDAError());

    entries.setErrors(errors);
    assertThat(entries.getErrors()).hasSize(1);
  }
}
