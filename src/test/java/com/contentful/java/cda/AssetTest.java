package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;

import org.junit.Test;

import static com.contentful.java.cda.image.ImageOption.Focus.top;
import static com.contentful.java.cda.image.ImageOption.Format.jpg;
import static com.contentful.java.cda.image.ImageOption.Resize.crop;
import static com.contentful.java.cda.image.ImageOption.backgroundColorOf;
import static com.contentful.java.cda.image.ImageOption.blackBackgroundColor;
import static com.contentful.java.cda.image.ImageOption.fitOf;
import static com.contentful.java.cda.image.ImageOption.focusOn;
import static com.contentful.java.cda.image.ImageOption.formatOf;
import static com.contentful.java.cda.image.ImageOption.heightOf;
import static com.contentful.java.cda.image.ImageOption.http;
import static com.contentful.java.cda.image.ImageOption.https;
import static com.contentful.java.cda.image.ImageOption.jpegQualityOf;
import static com.contentful.java.cda.image.ImageOption.roundedCornerRadiusOf;
import static com.contentful.java.cda.image.ImageOption.widthOf;
import static com.google.common.truth.Truth.assertThat;

public class AssetTest extends BaseTest {
  @Test
  @Enqueue("demo/assets_jake.json")
  public void fetchAsset() throws Exception {
    final CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    assertThat(asset.id()).isEqualTo("jake");
    assertThat(asset.title()).isEqualTo("Jake");
    assertThat(asset.mimeType()).isEqualTo("image/png");
    assertThat(asset.url()).isEqualTo("//images.contentful.com"
        + "/cfexampleapi"
        + "/4hlteQAXS8iS0YCMU6QMWg"
        + "/2a4d826144f014109364ccf5c891d2dd"
        + "/jake.png");
  }

  @Test
  @Enqueue("demo/assets_jake.json")
  public void augmentUrlSimple() throws Exception {
    final CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    assertThat(asset.urlForImageWith(http(), widthOf(10)))
        .isEqualTo("http://images.contentful.com"
            + "/cfexampleapi"
            + "/4hlteQAXS8iS0YCMU6QMWg"
            + "/2a4d826144f014109364ccf5c891d2dd"
            + "/jake.png?w=10");
  }

  @Test
  @Enqueue("demo/assets_jake.json")
  public void augmentUrlWithSameOperationUsesLastOption() throws Exception {
    final CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    assertThat(
        asset.urlForImageWith(widthOf(12), widthOf(34)))
        .isEqualTo("//images.contentful.com"
            + "/cfexampleapi"
            + "/4hlteQAXS8iS0YCMU6QMWg"
            + "/2a4d826144f014109364ccf5c891d2dd"
            + "/jake.png"
            + "?w=34"
        );
  }

  @Test
  @Enqueue("demo/assets_jake.json")
  public void augmentUrlComplete() throws Exception {
    final CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    assertThat(
        asset.urlForImageWith(
            formatOf(jpg),
            jpegQualityOf(89),
            widthOf(100),
            heightOf(100),
            fitOf(crop),
            focusOn(top),
            roundedCornerRadiusOf(10),
            backgroundColorOf(0xFFFFFF),
            backgroundColorOf(0x80, 0x80, 0x80),
            blackBackgroundColor(),
            https(),
            http()
        )
    ).isEqualTo(
        "https://images.contentful.com"
            + "/cfexampleapi"
            + "/4hlteQAXS8iS0YCMU6QMWg"
            + "/2a4d826144f014109364ccf5c891d2dd"
            + "/jake.png"
            + "?fmt=jpg"
            + "&q=89"
            + "&w=100"
            + "&h=100"
            + "&fit=crop"
            + "&f=top"
            + "&r=10.0"
            + "&bg=rgb:000000"
    );
  }

  @Test(expected = IllegalStateException.class)
  @Enqueue("demo/assets_no_image.json")
  public void expectThrowIfNotImageAsset() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("6OpsQmtnl6uYyeUmkOiYYq");

    asset.urlForImageWith(http());
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfWidthIsNegative() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith(widthOf(-1));
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfHeightIsNegative() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith(heightOf(-1));
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfRadiusIsNegative() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith(roundedCornerRadiusOf(-1));
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfColorIsNegative() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith(backgroundColorOf(-1));
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfColorIsToLarge() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith(backgroundColorOf(0xFFFFFF + 1));
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfRedColorComponentIsNegative() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith(backgroundColorOf(-1, 0, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfRedColorComponentIsToLarge() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith(backgroundColorOf(0xFF + 1, 0, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfGreenColorComponentIsNegative() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith(backgroundColorOf(0, -1, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfGreenColorComponentIsToLarge() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith(backgroundColorOf(0, 0xFF + 1, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfBlueColorComponentIsNegative() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith(backgroundColorOf(0, 0, -1));
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfBlueColorComponentIsToLarge() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith(backgroundColorOf(0, 0, 0xFF + 1));
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfQualityIsToSmall() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith(jpegQualityOf(-1));
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfQualityIsToHigh() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith(jpegQualityOf(0xFFFFFF + 1));
  }

  @Test(expected = IllegalArgumentException.class)
  @Enqueue("demo/assets_jake.json")
  public void expectThrowIfEmptyOption() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    asset.urlForImageWith();
  }

  @Test
  @Enqueue("demo/assets_jake.json")
  public void firstHttpOptionSurvives() throws Exception {
    CDAAsset asset = client.fetch(CDAAsset.class).one("jake");

    assertThat(asset.urlForImageWith(http(), https())).startsWith("http:");
    assertThat(asset.urlForImageWith(https(), http())).startsWith("https:");
  }
}
