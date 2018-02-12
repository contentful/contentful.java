package com.contentful.java.cda;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class SyncTypeTest {

  @Test
  public void testEntries() {
    final SyncType subject = SyncType.allEntries();

    assertThat(subject).isNotNull();
    assertThat(subject.getName()).isEqualTo("Entry");
    assertThat(subject.getContentType()).isNull();
  }

  @Test
  public void testAssets() {
    final SyncType subject = SyncType.allAssets();

    assertThat(subject).isNotNull();
    assertThat(subject.getName()).isEqualTo("Asset");
    assertThat(subject.getContentType()).isNull();
  }

  @Test
  public void testDeleted() {
    final SyncType subject = SyncType.onlyDeletion();

    assertThat(subject).isNotNull();
    assertThat(subject.getName()).isEqualTo("Deletion");
    assertThat(subject.getContentType()).isNull();
  }

  @Test
  public void testDeletedEntries() {
    final SyncType subject = SyncType.onlyDeletedEntries();

    assertThat(subject).isNotNull();
    assertThat(subject.getName()).isEqualTo("DeletedEntry");
    assertThat(subject.getContentType()).isNull();
  }

  @Test
  public void testDeletedAssets() {
    final SyncType subject = SyncType.onlyDeletedAssets();

    assertThat(subject).isNotNull();
    assertThat(subject.getName()).isEqualTo("DeletedAsset");
    assertThat(subject.getContentType()).isNull();
  }

  @Test
  public void testEntriesWithType() {
    final SyncType subject = SyncType.onlyEntriesOfType("cat");

    assertThat(subject).isNotNull();
    assertThat(subject.getName()).isEqualTo("Entry");
    assertThat(subject.getContentType()).isEqualTo("cat");
  }

}