package com.contentful.java.cda;

import com.contentful.java.cda.lib.Enqueue;
import com.contentful.java.cda.rich.CDARichBlock;
import com.contentful.java.cda.rich.CDARichDocument;
import com.contentful.java.cda.rich.CDARichEmbeddedBlock;
import com.contentful.java.cda.rich.CDARichEmbeddedInline;
import com.contentful.java.cda.rich.CDARichHeading;
import com.contentful.java.cda.rich.CDARichHorizontalRule;
import com.contentful.java.cda.rich.CDARichHyperLink;
import com.contentful.java.cda.rich.CDARichMark.CDARichMarkBold;
import com.contentful.java.cda.rich.CDARichMark.CDARichMarkCode;
import com.contentful.java.cda.rich.CDARichMark.CDARichMarkItalic;
import com.contentful.java.cda.rich.CDARichMark.CDARichMarkUnderline;
import com.contentful.java.cda.rich.CDARichOrderedList;
import com.contentful.java.cda.rich.CDARichParagraph;
import com.contentful.java.cda.rich.CDARichQuote;
import com.contentful.java.cda.rich.CDARichText;
import com.contentful.java.cda.rich.CDARichUnorderedList;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class RichTextTest extends BaseTest {
  @Test
  @Enqueue(value = "rich_text/simple_headline_1.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_headline_1_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(2);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichHeading.class);
    final CDARichHeading heading = (CDARichHeading) rich.getContent().get(0);

    assertThat(heading.getLevel()).isEqualTo(1);
    assertThat(heading.getContent()).hasSize(1);
    assertThat(heading.getContent().get(0)).isInstanceOf(CDARichText.class);
    final CDARichText text = (CDARichText) heading.getContent().get(0);
    assertThat(text.getText()).isEqualTo("This is a level one headline");
  }

  @Test
  @Enqueue(value = "rich_text/simple_headline_2.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_headline_2_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(2);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichHeading.class);
    final CDARichHeading heading = (CDARichHeading) rich.getContent().get(0);

    assertThat(heading.getLevel()).isEqualTo(2);
    assertThat(heading.getContent()).hasSize(1);
    assertThat(heading.getContent().get(0)).isInstanceOf(CDARichText.class);
    final CDARichText text = (CDARichText) heading.getContent().get(0);
    assertThat(text.getText()).isEqualTo("This is headline level 2");
  }

  @Test
  @Enqueue(value = "rich_text/simple_headline_3.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_headline_3_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(2);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichHeading.class);
    final CDARichHeading heading = (CDARichHeading) rich.getContent().get(0);

    assertThat(heading.getLevel()).isEqualTo(3);
    assertThat(heading.getContent()).hasSize(1);
    assertThat(heading.getContent().get(0)).isInstanceOf(CDARichText.class);
    final CDARichText text = (CDARichText) heading.getContent().get(0);
    assertThat(text.getText()).isEqualTo("This is heading level 3");
  }

  @Test
  @Enqueue(value = "rich_text/simple_headline_4.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_headline_4_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(2);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichHeading.class);
    final CDARichHeading heading = (CDARichHeading) rich.getContent().get(0);

    assertThat(heading.getLevel()).isEqualTo(4);
    assertThat(heading.getContent()).hasSize(1);
    assertThat(heading.getContent().get(0)).isInstanceOf(CDARichText.class);
    final CDARichText text = (CDARichText) heading.getContent().get(0);
    assertThat(text.getText()).isEqualTo("Heading 4 looks like this");
  }

  @Test
  @Enqueue(value = "rich_text/simple_headline_5.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_headline_5_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(2);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichHeading.class);
    final CDARichHeading heading = (CDARichHeading) rich.getContent().get(0);

    assertThat(heading.getLevel()).isEqualTo(5);
    assertThat(heading.getContent()).hasSize(1);
    assertThat(heading.getContent().get(0)).isInstanceOf(CDARichText.class);
    final CDARichText text = (CDARichText) heading.getContent().get(0);
    assertThat(text.getText()).isEqualTo("Headline of level 5.");
  }

  @Test
  @Enqueue(value = "rich_text/simple_headline_6.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_headline_6_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(2);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichHeading.class);
    final CDARichHeading heading = (CDARichHeading) rich.getContent().get(0);

    assertThat(heading.getLevel()).isEqualTo(6);
    assertThat(heading.getContent()).hasSize(1);
    assertThat(heading.getContent().get(0)).isInstanceOf(CDARichText.class);
    final CDARichText text = (CDARichText) heading.getContent().get(0);
    assertThat(text.getText()).isEqualTo("This is heading is level 6.");
  }

  @Test
  @Enqueue(value = "rich_text/simple_horizontal_rule.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_horizontal_rule_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(2);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichHorizontalRule.class);
  }

  @Test
  @Enqueue(value = "rich_text/simple_ordered_list.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_ordered_list_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(2);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichOrderedList.class);

    final CDARichOrderedList list = (CDARichOrderedList) rich.getContent().get(0);
    assertThat(list.getContent()).hasSize(6);

    final CDARichBlock item = (CDARichBlock) list.getContent().get(0);
    assertThat(item.getContent()).hasSize(1);

    final CDARichBlock block = (CDARichBlock) item.getContent().get(0);
    assertThat(block.getContent().get(0)).isInstanceOf(CDARichText.class);

    final CDARichText text = (CDARichText) block.getContent().get(0);
    assertThat(text.getText()).isEqualTo("This ");
  }

  @Test
  @Enqueue(value = "rich_text/simple_blockquote.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_quote_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(2);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichQuote.class);
    final CDARichQuote quote = (CDARichQuote) rich.getContent().get(0);

    assertThat(quote.getContent()).hasSize(1);
    assertThat(quote.getContent().get(0)).isInstanceOf(CDARichBlock.class);

    final CDARichBlock block = (CDARichBlock) quote.getContent().get(0);
    assertThat(block.getContent()).hasSize(1);

    final CDARichText text = (CDARichText) block.getContent().get(0);
    assertThat(text.getText()).isEqualTo("This is a blockquote");
  }

  @Test
  @Enqueue(value = "rich_text/simple_text.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_text_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(1);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichBlock.class);
    final CDARichBlock block = (CDARichBlock) rich.getContent().get(0);

    assertThat(block.getContent()).hasSize(1);
    assertThat(block.getContent().get(0)).isInstanceOf(CDARichText.class);
    final CDARichText text = (CDARichText) block.getContent().get(0);
    assertThat(text.getText()).isEqualTo("This is some simple text");
  }

  @Test
  @Enqueue(value = "rich_text/simple_text_bold.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_text_bold_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(1);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichBlock.class);
    final CDARichBlock block = (CDARichBlock) rich.getContent().get(0);

    assertThat(block.getContent()).hasSize(1);
    assertThat(block.getContent().get(0)).isInstanceOf(CDARichText.class);

    final CDARichText text = (CDARichText) block.getContent().get(0);
    assertThat(text.getMarks()).hasSize(1);
    assertThat(text.getMarks().get(0)).isInstanceOf(CDARichMarkBold.class);
    assertThat(text.getText()).isEqualTo("This is bold text");
  }

  @Test
  @Enqueue(value = "rich_text/simple_text_code.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_text_code_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(1);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichBlock.class);
    final CDARichBlock block = (CDARichBlock) rich.getContent().get(0);

    assertThat(block.getContent()).hasSize(1);
    assertThat(block.getContent().get(0)).isInstanceOf(CDARichText.class);

    final CDARichText text = (CDARichText) block.getContent().get(0);
    assertThat(text.getMarks()).hasSize(1);
    assertThat(text.getMarks().get(0)).isInstanceOf(CDARichMarkCode.class);
    assertThat(text.getText()).isEqualTo("This is code");
  }

  @Test
  @Enqueue(value = "rich_text/simple_text_italic.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_text_italic_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(1);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichBlock.class);
    final CDARichBlock block = (CDARichBlock) rich.getContent().get(0);

    assertThat(block.getContent()).hasSize(1);
    assertThat(block.getContent().get(0)).isInstanceOf(CDARichText.class);

    final CDARichText text = (CDARichText) block.getContent().get(0);
    assertThat(text.getMarks()).hasSize(1);
    assertThat(text.getMarks().get(0)).isInstanceOf(CDARichMarkItalic.class);
    assertThat(text.getText()).isEqualTo("This is italic text");
  }

  @Test
  @Enqueue(value = "rich_text/simple_text_underline.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_text_underline_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(1);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichBlock.class);
    final CDARichBlock block = (CDARichBlock) rich.getContent().get(0);

    assertThat(block.getContent()).hasSize(1);
    assertThat(block.getContent().get(0)).isInstanceOf(CDARichText.class);

    final CDARichText text = (CDARichText) block.getContent().get(0);
    assertThat(text.getMarks()).hasSize(1);
    assertThat(text.getMarks().get(0)).isInstanceOf(CDARichMarkUnderline.class);
    assertThat(text.getText()).isEqualTo("This is some underlined text");
  }

  @Test
  @Enqueue(value = "rich_text/simple_text_mixed_bold_italic_underline_code_all.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_text_mixed_bold_italic_underline_code_all_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(1);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichBlock.class);
    final CDARichBlock block = (CDARichBlock) rich.getContent().get(0);

    assertThat(block.getContent()).hasSize(9);

    final CDARichText first = (CDARichText) block.getContent().get(0);
    assertThat(first.getText()).isEqualTo("bold");
    assertThat(first.getMarks()).hasSize(1);
    assertThat(first.getMarks().get(0)).isInstanceOf(CDARichMarkBold.class);

    final CDARichText second = (CDARichText) block.getContent().get(1);
    assertThat(second.getText()).isEqualTo(" ");
    assertThat(second.getMarks()).hasSize(0);

    final CDARichText third = (CDARichText) block.getContent().get(2);
    assertThat(third.getText()).isEqualTo("italic");
    assertThat(third.getMarks()).hasSize(1);
    assertThat(third.getMarks().get(0)).isInstanceOf(CDARichMarkItalic.class);

    final CDARichText fourth = (CDARichText) block.getContent().get(3);
    assertThat(fourth.getText()).isEqualTo(" ");
    assertThat(fourth.getMarks()).hasSize(0);

    final CDARichText fifth = (CDARichText) block.getContent().get(4);
    assertThat(fifth.getText()).isEqualTo("underline");
    assertThat(fifth.getMarks()).hasSize(1);
    assertThat(fifth.getMarks().get(0)).isInstanceOf(CDARichMarkUnderline.class);

    final CDARichText sixth = (CDARichText) block.getContent().get(5);
    assertThat(sixth.getText()).isEqualTo(" ");
    assertThat(sixth.getMarks()).hasSize(0);

    final CDARichText senventh = (CDARichText) block.getContent().get(6);
    assertThat(senventh.getText()).isEqualTo("code");
    assertThat(senventh.getMarks()).hasSize(1);
    assertThat(senventh.getMarks().get(0)).isInstanceOf(CDARichMarkCode.class);

    final CDARichText eights = (CDARichText) block.getContent().get(7);
    assertThat(eights.getText()).isEqualTo(" ");
    assertThat(eights.getMarks()).hasSize(0);

    final CDARichText ninth = (CDARichText) block.getContent().get(8);
    assertThat(ninth.getText()).isEqualTo("all");
    assertThat(ninth.getMarks()).hasSize(4);
    assertThat(ninth.getMarks().get(0)).isInstanceOf(CDARichMarkBold.class);
    assertThat(ninth.getMarks().get(1)).isInstanceOf(CDARichMarkItalic.class);
    assertThat(ninth.getMarks().get(2)).isInstanceOf(CDARichMarkUnderline.class);
    assertThat(ninth.getMarks().get(3)).isInstanceOf(CDARichMarkCode.class);
  }

  @Test
  @Enqueue(value = "rich_text/simple_text_embedded.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_text_embedded_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(2);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichEmbeddedBlock.class);
    final CDARichEmbeddedBlock embedded = (CDARichEmbeddedBlock) rich.getContent().get(0);

    assertThat(embedded.getData()).isInstanceOf(CDAEntry.class);
    final CDAEntry cdaEntry = (CDAEntry) embedded.getData();

    assertThat(cdaEntry.<String>getField("name")).isEqualTo("simple_text");

    assertThat(embedded.getContent().get(0)).isInstanceOf(CDARichText.class);
    final CDARichText text = (CDARichText) embedded.getContent().get(0);
    assertThat(text.getText()).isEqualTo("");
  }

  @Test
  @Enqueue(value = "rich_text/simple_text_with_link.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_text_with_link_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(1);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichBlock.class);
    final CDARichBlock block = (CDARichBlock) rich.getContent().get(0);

    assertThat(block.getContent()).hasSize(3);
    assertThat(block.getContent().get(0)).isInstanceOf(CDARichText.class);
    assertThat(block.getContent().get(1)).isInstanceOf(CDARichHyperLink.class);
    assertThat(block.getContent().get(2)).isInstanceOf(CDARichText.class);

    final CDARichHyperLink link = (CDARichHyperLink) block.getContent().get(1);
    assertThat(link.getData()).isEqualTo("https://www.contentful.com");
    assertThat(link.getContent().get(0)).isInstanceOf(CDARichText.class);

    final CDARichText text = (CDARichText) link.getContent().get(0);
    assertThat(text.getText()).isEqualTo("This is a text with linking to contentful.com");
  }

  @Test
  @Enqueue(value = "rich_text/simple_unordered_list.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void simple_unordered_list_test() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(2);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichUnorderedList.class);

    final CDARichUnorderedList list = (CDARichUnorderedList) rich.getContent().get(0);
    assertThat(list.getDecoration()).isEqualTo("*");
    assertThat(list.getContent()).hasSize(5);

    final CDARichBlock item = (CDARichBlock) list.getContent().get(0);
    assertThat(item.getContent()).hasSize(1);

    final CDARichBlock block = (CDARichBlock) item.getContent().get(0);
    assertThat(block.getContent().get(0)).isInstanceOf(CDARichText.class);

    final CDARichText text = (CDARichText) block.getContent().get(0);
    assertThat(text.getText()).isEqualTo("This");
  }

  @Test
  @Enqueue(value = "rich_text/inline_hyperlink_integration.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void hyperlink_integration() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(1);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichParagraph.class);

    final CDARichParagraph paragraph = (CDARichParagraph) rich.getContent().get(0);

    assertThat(paragraph.getContent()).hasSize(9);

    assertThat(paragraph.getContent().get(1)).isInstanceOf(CDARichHyperLink.class);
    final CDARichHyperLink simpleUrlLink = (CDARichHyperLink) paragraph.getContent().get(1);
    assertThat(simpleUrlLink.getData()).isEqualTo("https://www.example.com/");

    assertThat(paragraph.getContent().get(3)).isInstanceOf(CDARichHyperLink.class);
    final CDARichHyperLink entryLink = (CDARichHyperLink) paragraph.getContent().get(3);
    assertThat(entryLink.getData()).isInstanceOf(CDAEntry.class);

    assertThat(paragraph.getContent().get(5)).isInstanceOf(CDARichHyperLink.class);
    final CDARichHyperLink assetLink = (CDARichHyperLink) paragraph.getContent().get(5);
    assertThat(assetLink.getData()).isInstanceOf(CDAAsset.class);
  }

  @Test
  @Enqueue(value = "rich_text/simple_entry_inline.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void hyperlink_entry_block() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(1);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichParagraph.class);
    final CDARichParagraph paragraph = (CDARichParagraph) rich.getContent().get(0);

    assertThat(paragraph.getContent()).hasSize(3);
    assertThat(paragraph.getContent().get(1)).isInstanceOf(CDARichEmbeddedInline.class);

    final CDARichEmbeddedInline link = (CDARichEmbeddedInline) paragraph.getContent().get(1);
    assertThat(link.getData()).isInstanceOf(CDAEntry.class);
  }

  @Test
  @Enqueue(value = "rich_text/simple_entry_hyperlink.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void hyperlink_entry_link() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(1);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichParagraph.class);
    final CDARichParagraph paragraph = (CDARichParagraph) rich.getContent().get(0);

    assertThat(paragraph.getContent()).hasSize(3);
    assertThat(paragraph.getContent().get(1)).isInstanceOf(CDARichHyperLink.class);

    final CDARichHyperLink link = (CDARichHyperLink) paragraph.getContent().get(1);
    assertThat(link.getData()).isInstanceOf(CDAEntry.class);
  }

  @Test
  @Enqueue(value = "rich_text/simple_asset_block.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void hyperlink_asset_block() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(2);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichEmbeddedBlock.class);

    final CDARichEmbeddedBlock link = (CDARichEmbeddedBlock) rich.getContent().get(0);
    assertThat(link.getData()).isInstanceOf(CDAAsset.class);
  }

  @Test
  @Enqueue(value = "rich_text/simple_asset_hyperlink.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void hyperlink_asset_link() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(1);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichParagraph.class);
    final CDARichParagraph paragraph = (CDARichParagraph) rich.getContent().get(0);

    assertThat(paragraph.getContent()).hasSize(3);
    assertThat(paragraph.getContent().get(1)).isInstanceOf(CDARichHyperLink.class);

    final CDARichHyperLink link = (CDARichHyperLink) paragraph.getContent().get(1);
    assertThat(link.getData()).isInstanceOf(CDAAsset.class);
  }

  @Test
  @Enqueue(value = "rich_text/simple_hyperlink.json", defaults = {"rich_text/locales.json", "rich_text/content_types.json"})
  public void hyperlink() {
    final CDAEntry entry = (CDAEntry) client.fetch(CDAEntry.class).all().items().get(0);
    final CDARichDocument rich = entry.getField("rich");
    assertThat(rich).isNotNull();

    assertThat(rich.getContent()).hasSize(1);
    assertThat(rich.getContent().get(0)).isInstanceOf(CDARichParagraph.class);
    final CDARichParagraph paragraph = (CDARichParagraph) rich.getContent().get(0);

    assertThat(paragraph.getContent()).hasSize(3);
    assertThat(paragraph.getContent().get(1)).isInstanceOf(CDARichHyperLink.class);

    final CDARichHyperLink link = (CDARichHyperLink) paragraph.getContent().get(1);
    assertThat(link.getData()).isEqualTo("https://www.example.com/");
  }
}
