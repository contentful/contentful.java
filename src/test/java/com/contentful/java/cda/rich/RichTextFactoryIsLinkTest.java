package com.contentful.java.cda.rich;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

/**
 * Regression tests for {@link RichTextFactory}'s private {@code isLink(Object)} method.
 * <p>
 * A prior operator-precedence defect (missing parentheses around a {@code ||} sub-expression)
 * meant {@code id != null} only guarded the "Asset" branch, so an "Entry" link with a null id
 * incorrectly returned {@code true}, later causing {@code link.data} to be silently set to
 * {@code null} and crashing any renderer that dereferenced it as a {@code CDAEntry}.
 * <p>
 * Uses reflection to reach the private method directly, since it is not otherwise exposed.
 */
public class RichTextFactoryIsLinkTest {

  private boolean invokeIsLink(Map<String, Object> data) throws Exception {
    Method method = RichTextFactory.class.getDeclaredMethod("isLink", Object.class);
    method.setAccessible(true);
    return (boolean) method.invoke(null, data);
  }

  private Map<String, Object> link(String linkType, String id) {
    Map<String, Object> sys = new HashMap<>();
    sys.put("type", "Link");
    sys.put("linkType", linkType);
    if (id != null) {
      sys.put("id", id);
    }
    Map<String, Object> data = new HashMap<>();
    data.put("sys", sys);
    return data;
  }

  @Test
  public void entryLinkWithNullIdIsRejected() throws Exception {
    assertThat(invokeIsLink(link("Entry", null))).isFalse();
  }

  @Test
  public void assetLinkWithNullIdIsRejected() throws Exception {
    assertThat(invokeIsLink(link("Asset", null))).isFalse();
  }

  @Test
  public void entryLinkWithValidIdIsAccepted() throws Exception {
    assertThat(invokeIsLink(link("Entry", "someEntryId"))).isTrue();
  }

  @Test
  public void assetLinkWithValidIdIsAccepted() throws Exception {
    assertThat(invokeIsLink(link("Asset", "someAssetId"))).isTrue();
  }

  @Test
  public void unknownLinkTypeIsRejected() throws Exception {
    assertThat(invokeIsLink(link("Space", "someId"))).isFalse();
  }
}
