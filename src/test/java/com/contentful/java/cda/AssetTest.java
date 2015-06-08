package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class AssetTest extends BaseTest {
  @Test
  @Enqueue("assets_jake.json")
  public void fetchAsset() throws Exception {
    CDAAsset asset = client.observe(CDAAsset.class).one("jake").toBlocking().first();
    assertThat(asset.id()).isEqualTo("jake");
    assertThat(asset.title()).isEqualTo("Jake");
    assertThat(asset.mimeType()).isEqualTo("image/png");
    assertThat(asset.url()).isEqualTo("//images.contentful.com"
        + "/cfexampleapi"
        + "/4hlteQAXS8iS0YCMU6QMWg"
        + "/2a4d826144f014109364ccf5c891d2dd"
        + "/jake.png");
  }
}
