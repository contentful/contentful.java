package com.contentful.java.cda;

import com.contentful.java.cda.QueryOperation.BoundingBox;
import com.contentful.java.cda.QueryOperation.BoundingCircle;
import com.contentful.java.cda.QueryOperation.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.contentful.java.cda.QueryOperation.Exists;
import static com.contentful.java.cda.QueryOperation.HasAllOf;
import static com.contentful.java.cda.QueryOperation.HasNoneOf;
import static com.contentful.java.cda.QueryOperation.HasOneOf;
import static com.contentful.java.cda.QueryOperation.IsCloseTo;
import static com.contentful.java.cda.QueryOperation.IsEarlierOrAt;
import static com.contentful.java.cda.QueryOperation.IsEarlierThan;
import static com.contentful.java.cda.QueryOperation.IsEqualTo;
import static com.contentful.java.cda.QueryOperation.IsGreaterThan;
import static com.contentful.java.cda.QueryOperation.IsGreaterThanOrEqualTo;
import static com.contentful.java.cda.QueryOperation.IsLaterOrAt;
import static com.contentful.java.cda.QueryOperation.IsLaterThan;
import static com.contentful.java.cda.QueryOperation.IsLessThan;
import static com.contentful.java.cda.QueryOperation.IsLessThanOrEqualTo;
import static com.contentful.java.cda.QueryOperation.IsNotEqualTo;
import static com.contentful.java.cda.QueryOperation.IsWithinBoundingBoxOf;
import static com.contentful.java.cda.QueryOperation.IsWithinCircleOf;
import static com.contentful.java.cda.QueryOperation.Matches;
import static com.google.common.truth.Truth.assertThat;

/**
 * Test if query generation works as expected
 */
@RunWith(MockitoJUnitRunner.class)
public class AbsQueryTest {

  @Mock
  public CDAClient client;

  public FetchQuery<CDAResource> query;

  @Before
  public void setup() {
    query = new FetchQuery<>(CDAResource.class, client);
  }

  @Test
  public void contentType() {
    query.withContentType("foo");

    assertThat(query.params).containsEntry("content_type", "foo");
  }

  @Test(expected = IllegalStateException.class)
  public void settingContentTypeTwiceThrows() {
    query.withContentType("foo");
    query.withContentType("bar");
  }

  @Test
  public void locale() {
    query.withLocale("en");

    assertThat(query.params).containsEntry("locale", "en");
  }

  @Test(expected = IllegalStateException.class)
  public void settingLocaleTypeTwiceThrowsError() {
    query.withLocale("en");
    query.withLocale("de");
  }

  @Test
  public void select() {
    query.withContentType("foo");
    query.select("fields.bar");

    assertThat(query.params).containsEntry("select", "sys,fields.bar");
  }

  @Test(expected = IllegalStateException.class)
  public void selectWithoutContentTypeThrows() {
    query.select("fields.bar");
  }

  @Test
  public void selectWithSys() {
    query.select("sys");

    assertThat(query.params).containsEntry("select", "sys");
  }

  @Test
  public void selectWithSysAndFields() {
    query.withContentType("foo");
    query.select("sys");
    query.select("fields.bar");

    assertThat(query.params).containsEntry("select", "sys,fields.bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptySelectThrows() {
    query.withContentType("foo");
    query.select();
  }

  @Test(expected = IllegalArgumentException.class)
  public void selectWithEmptyNameThrows() {
    query.select("");
  }

  @Test
  public void selectDifferentFieldNames() {
    query.withContentType("foo");
    query.select("fields.bar");
    query.select("fields.bar2");

    assertThat(query.params).containsEntry("select", "sys,fields.bar,fields.bar2");
  }

  @Test(expected = IllegalArgumentException.class)
  public void selectWithToManyLevelsThrows() {
    query.withContentType("foo");
    query.select("fields.baz.bar");
  }

  @Test
  public void selects() {
    query.withContentType("foo");
    query.select("fields.bar", "fields.bar2");

    assertThat(query.params).containsEntry("select", "sys,fields.bar,fields.bar2");
  }

  @Test
  public void linksToEntryId() {
    query.linksToEntryId("nyancat");

    assertThat(query.params).containsEntry("links_to_entry", "nyancat");
  }

  @Test
  public void linksToAssetId() {
      query.linksToAssetId("nyancat");

      assertThat(query.params).containsEntry("links_to_asset", "nyancat");
  }

  @Test(expected = IllegalStateException.class)
  public void selectsWithoutTypeThrows() {
    query.select("fields.bar", "fields.bar2");
  }

  @Test(expected = IllegalArgumentException.class)
  public void selectsWithoutFieldNames() {
    query.withContentType("foo");
    query.select();
  }

  @Test(expected = IllegalArgumentException.class)
  public void selectsWithoutNullEntry() {
    query.withContentType("foo");
    query.select("bar", null);
  }

  @Test
  public void equals() {
    query.withContentType("foo");
    query.where("fields.bar", IsEqualTo, "baz");

    assertThat(query.params).containsEntry("fields.bar", "baz");
  }

  @Test
  public void equalsWithSys() {
    query.where("sys.id", IsEqualTo, "foo");

    assertThat(query.params).containsEntry("sys.id", "foo");
  }

  @Test(expected = IllegalStateException.class)
  public void equalsThrowsIfFieldsAreSearchedButNoContentType() {
    query.where("fields.bar", IsEqualTo, "baz");
  }

  @Test(expected = IllegalArgumentException.class)
  public void equalsThrowsIfNeitherFieldNorSysIsGiven() {
    query.where("bar", IsEqualTo, "baz");
  }

  @Test(expected = NullPointerException.class)
  public void equalsWithNullOperationThrows() {
    query.withContentType("foo");
    query.where("fields.bar", null, "baz");
  }

  @Test(expected = NullPointerException.class)
  public void equalsWithNullValueThrows() {
    query.withContentType("foo");
    query.where("fields.bar", IsEqualTo, (String)null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void equalsWithNoValuesThrows() {
    query.withContentType("foo");
    query.where("fields.bar", IsEqualTo);
  }

  @Test
  public void notEqual() {
    query.withContentType("foo");
    query.where("fields.bar", IsNotEqualTo, "baz");

    assertThat(query.params).containsEntry("fields.bar[ne]", "baz");
  }

  @Test
  public void all() {
    query.withContentType("foo");
    query.where("fields.bar", HasAllOf, "a,b,c,d");

    assertThat(query.params).containsEntry("fields.bar[all]", "a,b,c,d");
  }

  @Test
  public void allSeparated() {
    query.withContentType("foo");
    query.where("fields.bar", HasAllOf, "a", "b", "c", "d");

    assertThat(query.params).containsEntry("fields.bar[all]", "a,b,c,d");
  }

  @Test
  public void atLeasOnOf() {
    query.withContentType("foo");
    query.where("fields.bar", HasOneOf, "a", "b", "c", "d");

    assertThat(query.params).containsEntry("fields.bar[in]", "a,b,c,d");
  }

  @Test
  public void noneOf() {
    query.withContentType("foo");
    query.where("fields.bar", HasNoneOf, "a", "b", "c", "d");

    assertThat(query.params).containsEntry("fields.bar[nin]", "a,b,c,d");
  }

  @Test
  public void fieldExists() {
    query.withContentType("foo");
    query.where("fields.bar", Exists);

    assertThat(query.params).containsEntry("fields.bar[exists]", "true");
  }

  @Test
  public void fieldDoesNotExists() {
    query.withContentType("foo");
    query.where("fields.bar", Exists, false);

    assertThat(query.params).containsEntry("fields.bar[exists]", "false");
  }

  @Test
  public void isLessThan() {
    query.withContentType("foo");
    query.where("fields.bar", IsLessThan, 1);

    assertThat(query.params).containsEntry("fields.bar[lt]", "1");
  }

  @Test
  public void isLessOrEqualTo() {
    query.withContentType("foo");
    query.where("fields.bar", IsLessThanOrEqualTo, 1);

    assertThat(query.params).containsEntry("fields.bar[lte]", "1");
  }

  @Test
  public void isGreaterThan() {
    query.withContentType("foo");
    query.where("fields.bar", IsGreaterThan, 1);

    assertThat(query.params).containsEntry("fields.bar[gt]", "1");
  }

  @Test
  public void isGreaterThanOrEqualTo() {
    query.withContentType("foo");
    query.where("fields.bar", IsGreaterThanOrEqualTo, 1);

    assertThat(query.params).containsEntry("fields.bar[gte]", "1");
  }

  @Test
  public void isEarlierThan() {
    query.withContentType("foo");
    query.where("fields.bar", IsEarlierThan, "2013-02-02T14:34+01:00");

    assertThat(query.params).containsEntry("fields.bar[lt]", "2013-02-02T14:34+01:00");
  }

  @Test
  public void isEarlierThanOrAt() {
    query.withContentType("foo");
    query.where("fields.bar", IsEarlierOrAt, "2013-02-02T14:34+01:00");

    assertThat(query.params).containsEntry("fields.bar[lte]", "2013-02-02T14:34+01:00");
  }


  @Test
  public void isLaterThan() {
    query.withContentType("foo");
    query.where("fields.bar", IsLaterThan, "2013-02-02T14:34+01:00");

    assertThat(query.params).containsEntry("fields.bar[gt]", "2013-02-02T14:34+01:00");
  }

  @Test
  public void isLaterThanOrEqualTo() {
    query.withContentType("foo");
    query.where("fields.bar", IsLaterOrAt, "2013-02-02T14:34+01:00");

    assertThat(query.params).containsEntry("fields.bar[gte]", "2013-02-02T14:34+01:00");
  }

  @Test
  public void matchQuery() {
    query.withContentType("foo");
    query.where("fields.bar", Matches, "bar");

    assertThat(query.params).containsEntry("content_type", "foo");
    assertThat(query.params).containsEntry("fields.bar[match]", "bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void matchQueryWithEmptyValueThrows() {
    query.withContentType("foo");
    query.where("fields.bar", Matches);
  }

  @Test(expected = IllegalStateException.class)
  public void matchQueryWithNoContentTypeButFieldsThrows() {
    query.where("fields.foo", Matches, "b");
  }

  @Test
  public void matchQueryWithSys() {
    query.where("sys.id", Matches, "foo");

    assertThat(query.params).containsEntry("sys.id[match]", "foo");
    assertThat(query.params).doesNotContainKey("content_type");
  }

  @Test
  public void near() {
    query.withContentType("foo");
    query.where("fields.bar", IsCloseTo, new Location(52d, 14d));

    assertThat(query.params).containsKey("fields.bar[near]");

    final String[] coordinates = query.params.get("fields.bar[near]").split(",");
    assertThat(coordinates).hasLength(2);
    assertThat(Double.parseDouble(coordinates[0])).isWithin(0.1d).of(52d);
    assertThat(Double.parseDouble(coordinates[1])).isWithin(0.1d).of(14d);
  }

  @Test(expected = IllegalStateException.class)
  public void nearThrowsIfNotUsedWithContentType() {
    query.where("fields.bar", IsCloseTo, new Location(52, 14));
  }

  @Test
  public void withinBoundingBox() {
    query.withContentType("foo");
    query.where("fields.bar", IsWithinBoundingBoxOf,
        new BoundingBox(new Location(0, 1), new Location(2, 3)));

    assertThat(query.params).containsKey("fields.bar[within]");

    final String[] coordinates = query.params.get("fields.bar[within]").split(",");
    assertThat(coordinates).hasLength(4);
    assertThat(Double.parseDouble(coordinates[0])).isWithin(0.1d).of(0d);
    assertThat(Double.parseDouble(coordinates[1])).isWithin(0.1d).of(1d);
    assertThat(Double.parseDouble(coordinates[2])).isWithin(0.1d).of(2d);
    assertThat(Double.parseDouble(coordinates[3])).isWithin(0.1d).of(3d);
  }

  @Test
  public void withinBoundingBoxOfDoubles() {
    query.withContentType("foo");
    query.where("fields.bar", IsWithinBoundingBoxOf, new BoundingBox(0, 1, 2, 3));

    assertThat(query.params).containsKey("fields.bar[within]");

    final String[] coordinates = query.params.get("fields.bar[within]").split(",");
    assertThat(coordinates).hasLength(4);
    assertThat(Double.parseDouble(coordinates[0])).isWithin(0.1d).of(0d);
    assertThat(Double.parseDouble(coordinates[1])).isWithin(0.1d).of(1d);
    assertThat(Double.parseDouble(coordinates[2])).isWithin(0.1d).of(2d);
    assertThat(Double.parseDouble(coordinates[3])).isWithin(0.1d).of(3d);
  }

  @Test
  public void withinBoundingCircle() {
    query.withContentType("foo");
    query.where("fields.bar", IsWithinCircleOf, new BoundingCircle(52d, 14d, 10d));

    assertThat(query.params).containsKey("fields.bar[within]");

    final String[] attributes = query.params.get("fields.bar[within]").split(",");
    assertThat(attributes).hasLength(3);
    assertThat(Double.parseDouble(attributes[0])).isWithin(0.1d).of(52d);
    assertThat(Double.parseDouble(attributes[1])).isWithin(0.1d).of(14d);
    assertThat(Double.parseDouble(attributes[2])).isWithin(0.1d).of(10d);
  }

  @Test
  public void withinTypeSaveBoundingCircle() {
    query.withContentType("foo");
    query.where("fields.bar", IsWithinCircleOf, new BoundingCircle(new Location(52d, 14d), 10d));

    assertThat(query.params).containsKey("fields.bar[within]");

    final String[] attributes = query.params.get("fields.bar[within]").split(",");
    assertThat(attributes).hasLength(3);
    assertThat(Double.parseDouble(attributes[0])).isWithin(0.1d).of(52d);
    assertThat(Double.parseDouble(attributes[1])).isWithin(0.1d).of(14d);
    assertThat(Double.parseDouble(attributes[2])).isWithin(0.1d).of(10d);
  }

  @Test
  public void orderByField() {
    query.withContentType("foo");
    query.orderBy("fields.bar");

    assertThat(query.params).containsEntry("content_type", "foo");
    assertThat(query.params).containsEntry("order", "fields.bar");
  }

  @Test
  public void orderBySysWithoutField() {
    query.orderBy("sys.foo");

    assertThat(query.params).doesNotContainKey("content_type");
    assertThat(query.params).containsEntry("order", "sys.foo");
  }

  @Test(expected = IllegalStateException.class)
  public void orderByThrowsOnFieldWithoutContentType() {
    query.orderBy("fields.foo");
  }

  @Test(expected = IllegalArgumentException.class)
  public void orderByThrowsOnEmptyKey() {
    query.orderBy("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void orderByThrowsOnNullKey() {
    query.orderBy((String) null);
  }

  @Test
  public void orderByMultipleFields() {
    query.withContentType("foo");
    query.orderBy("fields.bar", "fields.baz");

    assertThat(query.params).containsEntry("content_type", "foo");
    assertThat(query.params).containsEntry("order", "fields.bar,fields.baz");
  }

  @Test
  public void orderByMultipleFieldsWithSys() {
    query.withContentType("foo");
    query.orderBy("fields.bar", "sys.id");

    assertThat(query.params).containsEntry("content_type", "foo");
    assertThat(query.params).containsEntry("order", "fields.bar,sys.id");
  }

  @Test
  public void orderByMultipleOnlySysAndNoContentType() {
    query.orderBy("sys.foo", "sys.bar");

    assertThat(query.params).doesNotContainKey("content_type");
    assertThat(query.params).containsEntry("order", "sys.foo,sys.bar");
  }

  @Test
  public void orderByMultipleOneReverse() {
    query.orderBy("sys.foo", "-sys.bar");

    assertThat(query.params).containsEntry("order", "sys.foo,-sys.bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void orderByOneNullThrows() {
    query.withContentType("foo");
    query.orderBy("fields.bar", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void orderByOneEmptyThrows() {
    query.withContentType("foo");
    query.orderBy("fields.bar", "", "fields.baz");
  }

  @Test
  public void reverseOrderByField() {
    query.withContentType("foo");
    query.reverseOrderBy("fields.bar");

    assertThat(query.params).containsEntry("content_type", "foo");
    assertThat(query.params).containsEntry("order", "-fields.bar");
  }

  @Test
  public void reverseOrderBySysWithoutField() {
    query.reverseOrderBy("sys.foo");

    assertThat(query.params).doesNotContainKey("content_type");
    assertThat(query.params).containsEntry("order", "-sys.foo");
  }

  @Test(expected = IllegalStateException.class)
  public void reverseOrderByThrowsOnFieldWithoutContentType() {
    query.reverseOrderBy("fields.foo");
  }

  @Test(expected = IllegalArgumentException.class)
  public void reverseOrderByThrowsOnEmptyKey() {
    query.reverseOrderBy("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void reverseOrderByThrowsOnNullKey() {
    query.reverseOrderBy(null);
  }

  @Test
  public void limit() {
    query.limit(3);

    assertThat(query.params).containsEntry("limit", "3");
  }

  @Test(expected = IllegalArgumentException.class)
  public void limitWithNegativeThrows() {
    query.limit(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void limitWithMoreThen1kThrows() {
    query.limit(1001);
  }

  @Test
  public void skip() {
    query.skip(3);

    assertThat(query.params).containsEntry("skip", "3");
  }

  @Test(expected = IllegalArgumentException.class)
  public void skipWithNegativeThrows() {
    query.skip(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void skipWithNegInfThrows() {
    query.skip(Integer.MIN_VALUE);
  }

  @Test
  public void includeZero() {
    query.include(0);

    assertThat(query.params).containsEntry("include", "0");
  }

  @Test
  public void includeTen() {
    query.include(10);

    assertThat(query.params).containsEntry("include", "10");
  }

  @Test(expected = IllegalArgumentException.class)
  public void includeToManyLevelThrows() {
    query.include(11);
  }

  @Test(expected = IllegalArgumentException.class)
  public void includeNegative() {
    query.include(-1);
  }

  @Test
  public void queryingAssetTitlesDoesNotThrowWithoutTypeSet() {
    final FetchQuery<CDAAsset> assetQuery = new FetchQuery<>(CDAAsset.class, client);
    assetQuery.where("fields.title", IsEqualTo, "bar");
  }

  @Test
  public void queryForAContentTypesNameDoesNotThrow() {
    final FetchQuery<CDAContentType> typeQuery = new FetchQuery<>(CDAContentType.class, client);
    typeQuery.where("name", Matches, "Auth");
  }
}