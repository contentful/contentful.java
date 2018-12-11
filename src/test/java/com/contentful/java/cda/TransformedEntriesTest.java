package com.contentful.java.cda;

import com.contentful.java.cda.TransformQuery.ContentfulEntryModel;
import com.contentful.java.cda.TransformQuery.ContentfulField;
import com.contentful.java.cda.TransformQuery.ContentfulSystemField;
import com.contentful.java.cda.lib.Enqueue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.google.common.truth.Truth.assertThat;

public class TransformedEntriesTest extends BaseTest {
  @ContentfulEntryModel("cat")
  public static class Cat {
    @ContentfulField
    String name;

    @ContentfulField("bestFriend")
    Cat mate;

    @ContentfulSystemField
    String id;

    @ContentfulSystemField("revision")
    Object contentfulVersion;
  }

  @ContentfulEntryModel("cat")
  public static class RenamedCat {
    @ContentfulField("name")
    String m_pcName;
  }

  @Test
  @Enqueue("demo/entries_nyancat.json")
  public void fetchNyanCat() {
    final Cat nyancat = client
        .observeAndTransform(Cat.class)
        .one("nyancat")
        .blockingFirst();

    assertThat(nyancat).isNotNull();
    assertThat(nyancat.name).isEqualTo("Nyan Cat");
  }

  @Test
  @Enqueue("demo/entries.json")
  public void fetchCats() {
    final Collection<Cat> cats = client
        .observeAndTransform(Cat.class)
        .all()
        .blockingFirst();

    for (final Cat cat : cats) {
      assertThat(cat).isNotNull();
      assertThat(cat.name).isNotEmpty();
    }
  }

  @Test
  @Enqueue("demo/entries_nyancat.json")
  public void canRenameFields() {
    final RenamedCat nyancat = client
        .observeAndTransform(RenamedCat.class)
        .one("nyancat")
        .blockingFirst();

    assertThat(nyancat).isNotNull();
    assertThat(nyancat.m_pcName).isEqualTo("Nyan Cat");
  }

  @Test
  @Enqueue("demo/entries_nyancat.json")
  public void canCallCallbacksBack() throws Exception {
    final CountDownLatch latch = new CountDownLatch(1);
    final List<Cat> catsCaught = new ArrayList<>();

    client
        .observeAndTransform(Cat.class)
        .one("nyancat",
            new CDACallback<Cat>() {
              @Override protected void onSuccess(Cat result) {
                catsCaught.add(result);
                latch.countDown();
              }
            }
        );

    latch.await(2, TimeUnit.SECONDS);

    assertThat(catsCaught.size()).isEqualTo(1);
    assertThat(catsCaught.get(0)).isNotNull();
    assertThat(catsCaught.get(0).name).isEqualTo("Nyan Cat");
  }

  @Test
  @Enqueue("demo/entries.json")
  public void canCallAllCallbacksBack() throws Exception {
    final CountDownLatch latch = new CountDownLatch(1);
    final List<Cat> catsCaught = new ArrayList<>();

    client
        .observeAndTransform(Cat.class)
        .all(new CDACallback<Collection<Cat>>() {
               @Override protected void onSuccess(Collection<Cat> result) {
                 catsCaught.addAll(result);
                 latch.countDown();
               }
             }
        );

    latch.await(2, TimeUnit.SECONDS);

    assertThat(catsCaught.size()).isEqualTo(3);
    assertThat(catsCaught.stream().map(it -> it.name).toArray())
        .asList().containsAllOf("Happy Cat", "Garfield", "Nyan Cat");
  }
}
