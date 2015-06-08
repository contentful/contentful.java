package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;
import java.util.List;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class EntryTest extends BaseTest {
  @Test
  @Enqueue("entries_nyancat.json")
  public void fetchEntry() throws Exception {
    assertNyanCat(client.observe(CDAEntry.class).one("nyancat").toBlocking().first());
  }

  @Test
  @Enqueue("entries.json")
  public void fetchAllEntries() throws Exception {
    CDAArray array = client.observe(CDAEntry.class).all().toBlocking().first();
    assertThat(array.items()).hasSize(15);
    assertThat(array.assets()).hasSize(4);
    assertThat(array.entries()).hasSize(11);

    assertThat(array.total()).isEqualTo(11);
    assertThat(array.skip()).isEqualTo(0);
    assertThat(array.limit()).isEqualTo(100);

    CDAEntry nyanCat = array.entries().get("nyancat");
    assertThat(nyanCat).isNotNull();
    assertNyanCat(nyanCat);
  }

  private void assertNyanCat(CDAEntry entry) {
    assertThat(entry.id()).isEqualTo("nyancat");
    assertThat(entry.getField("name")).isEqualTo("Nyan Cat");
    assertThat(entry.getField("color")).isEqualTo("rainbow");
    assertThat(entry.getField("birthday")).isEqualTo("2011-04-04T22:00:00+00:00");
    assertThat(entry.getField("lives")).isEqualTo(1337.0);

    List likes = entry.getField("likes");
    assertThat(likes).containsExactly("rainbows", "fish");

    Object bestFriend = entry.getField("bestFriend");
    assertThat(bestFriend).isInstanceOf(CDAEntry.class);
    assertThat(entry).isSameAs(((CDAEntry) bestFriend).getField("bestFriend"));
  }
}
