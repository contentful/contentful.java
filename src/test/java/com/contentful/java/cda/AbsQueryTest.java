package com.contentful.java.cda;

import com.contentful.java.cda.QueryOperation.BoundingBox;
import com.contentful.java.cda.QueryOperation.BoundingCircle;
import com.contentful.java.cda.QueryOperation.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    query = new FetchQuery<CDAResource>(CDAResource.class, client);
  }

  @Test
  public void contentType() throws Exception {
    query.withContentType("foo");

    assertThat(query.params).containsEntry("content_type", "foo");
  }

  @Test(expected = IllegalStateException.class)
  public void settingContentTypeTwiceThrows() throws Exception {
    query.withContentType("foo");
    query.withContentType("bar");
  }

  @Test
  public void select() throws Exception {
    query.withContentType("foo");
    query.select("fields.bar");

    assertThat(query.params).containsEntry("select", "sys,fields.bar");
  }

  @Test(expected = IllegalStateException.class)
  public void selectWithoutContentTypeThrows() throws Exception {
    query.select("fields.bar");
  }

  @Test
  public void selectWithSys() throws Exception {
    query.select("sys");

    assertThat(query.params).containsEntry("select", "sys");
  }

  @Test
  public void selectWithSysAndFields() throws Exception {
    query.withContentType("foo");
    query.select("sys");
    query.select("fields.bar");

    assertThat(query.params).containsEntry("select", "sys,fields.bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptySelectThrows() throws Exception {
    query.withContentType("foo");
    query.select();
  }

  @Test(expected = IllegalArgumentException.class)
  public void selectWithEmptyNameThrows() throws Exception {
    query.select("");
  }

  @Test
  public void selectDifferentFieldNames() throws Exception {
    query.withContentType("foo");
    query.select("fields.bar");
    query.select("fields.bar2");

    assertThat(query.params).containsEntry("select", "sys,fields.bar,fields.bar2");
  }

  @Test(expected = IllegalArgumentException.class)
  public void selectWithToManyLevelsThrows() throws Exception {
    query.withContentType("foo");
    query.select("fields.baz.bar");
  }

  @Test
  public void selects() throws Exception {
    query.withContentType("foo");
    query.select("fields.bar", "fields.bar2");

    assertThat(query.params).containsEntry("select", "sys,fields.bar,fields.bar2");
  }

  @Test(expected = IllegalStateException.class)
  public void selectsWithoutTypeThrows() throws Exception {
    query.select("fields.bar", "fields.bar2");
  }

  @Test(expected = IllegalArgumentException.class)
  public void selectsWithoutFieldNames() throws Exception {
    query.withContentType("foo");
    query.select();
  }

  @Test(expected = IllegalArgumentException.class)
  public void selectsWithoutNullEntry() throws Exception {
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
    query.where("fields.bar", IsEqualTo, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void equalsWithNoValuesThrows() {
    query.withContentType("foo");
    query.where("fields.bar", IsEqualTo);
  }

  @Test
  public void notEqual() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", IsNotEqualTo, "baz");

    assertThat(query.params).containsEntry("fields.bar[ne]", "baz");
  }

  @Test
  public void all() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", HasAllOf, "a,b,c,d");

    assertThat(query.params).containsEntry("fields.bar[all]", "a,b,c,d");
  }

  @Test
  public void allSeparated() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", HasAllOf, "a", "b", "c", "d");

    assertThat(query.params).containsEntry("fields.bar[all]", "a,b,c,d");
  }

  @Test
  public void atLeasOnOf() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", HasOneOf, "a", "b", "c", "d");

    assertThat(query.params).containsEntry("fields.bar[in]", "a,b,c,d");
  }

  @Test
  public void noneOf() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", HasNoneOf, "a", "b", "c", "d");

    assertThat(query.params).containsEntry("fields.bar[nin]", "a,b,c,d");
  }

  @Test
  public void fieldExists() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", Exists);

    assertThat(query.params).containsEntry("fields.bar[exists]", "true");
  }

  @Test
  public void fieldDoesNotExists() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", Exists, false);

    assertThat(query.params).containsEntry("fields.bar[exists]", "false");
  }

  @Test
  public void isLessThan() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", IsLessThan, 1);

    assertThat(query.params).containsEntry("fields.bar[lt]", "1");
  }

  @Test
  public void isLessOrEqualTo() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", IsLessThanOrEqualTo, 1);

    assertThat(query.params).containsEntry("fields.bar[lte]", "1");
  }

  @Test
  public void isGreaterThan() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", IsGreaterThan, 1);

    assertThat(query.params).containsEntry("fields.bar[gt]", "1");
  }

  @Test
  public void isGreaterThanOrEqualTo() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", IsGreaterThanOrEqualTo, 1);

    assertThat(query.params).containsEntry("fields.bar[gte]", "1");
  }

  @Test
  public void isEarlierThan() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", IsEarlierThan, "2013-02-02T14:34+01:00");

    assertThat(query.params).containsEntry("fields.bar[lt]", "2013-02-02T14:34+01:00");
  }

  @Test
  public void isEarlierThanOrAt() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", IsEarlierOrAt, "2013-02-02T14:34+01:00");

    assertThat(query.params).containsEntry("fields.bar[lte]", "2013-02-02T14:34+01:00");
  }


  @Test
  public void isLaterThan() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", IsLaterThan, "2013-02-02T14:34+01:00");

    assertThat(query.params).containsEntry("fields.bar[gt]", "2013-02-02T14:34+01:00");
  }

  @Test
  public void isLaterThanOrEqualTo() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", IsLaterOrAt, "2013-02-02T14:34+01:00");

    assertThat(query.params).containsEntry("fields.bar[gte]", "2013-02-02T14:34+01:00");
  }

  @Test
  public void matchQuery() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", Matches, "bar");

    assertThat(query.params).containsEntry("content_type", "foo");
    assertThat(query.params).containsEntry("fields.bar[match]", "bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void matchQueryWithEmptyValueThrows() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", Matches);
  }

  @Test(expected = IllegalStateException.class)
  public void matchQueryWithNoContentTypeButFieldsThrows() throws Exception {
    query.where("fields.foo", Matches, "b");
  }

  @Test
  public void matchQueryWithSys() throws Exception {
    query.where("sys.id", Matches, "foo");

    assertThat(query.params).containsEntry("sys.id[match]", "foo");
    assertThat(query.params).doesNotContainKey("content_type");
  }

  @Test
  public void near() throws Exception {
    query.withContentType("foo");
    query.where("fields.bar", IsCloseTo, new Location(52d, 14d));

    assertThat(query.params).containsKey("fields.bar[near]");

    final String[] coordinates = query.params.get("fields.bar[near]").split(",");
    assertThat(coordinates).hasLength(2);
    assertThat(Double.parseDouble(coordinates[0])).isWithin(0.1d).of(52d);
    assertThat(Double.parseDouble(coordinates[1])).isWithin(0.1d).of(14d);
  }

  @Test(expected = IllegalStateException.class)
  public void nearThrowsIfNotUsedWithContentType() throws Exception {
    query.where("fields.bar", IsCloseTo, new Location(52, 14));
  }

  @Test
  public void withinBoundingBox() throws Exception {
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
  public void withinBoundingBoxOfDoubles() throws Exception {
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
  public void withinBoundingCircle() throws Exception {
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
  public void withinTypeSaveBoundingCircle() throws Exception {
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
  public void orderByField() throws Exception {
    query.withContentType("foo");
    query.orderBy("fields.bar");

    assertThat(query.params).containsEntry("content_type", "foo");
    assertThat(query.params).containsEntry("order", "fields.bar");
  }

  @Test
  public void orderBySysWithoutField() throws Exception {
    query.orderBy("sys.foo");

    assertThat(query.params).doesNotContainKey("content_type");
    assertThat(query.params).containsEntry("order", "sys.foo");
  }

  @Test(expected = IllegalStateException.class)
  public void orderByThrowsOnFieldWithoutContentType() throws Exception {
    query.orderBy("fields.foo");
  }

  @Test(expected = IllegalArgumentException.class)
  public void orderByThrowsOnEmptyKey() throws Exception {
    query.orderBy("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void orderByThrowsOnNullKey() throws Exception {
    query.orderBy((String) null);
  }

  @Test
  public void orderByMultipleFields() throws Exception {
    query.withContentType("foo");
    query.orderBy("fields.bar", "fields.baz");

    assertThat(query.params).containsEntry("content_type", "foo");
    assertThat(query.params).containsEntry("order", "fields.bar,fields.baz");
  }

  @Test
  public void orderByMultipleFieldsWithSys() throws Exception {
    query.withContentType("foo");
    query.orderBy("fields.bar", "sys.id");

    assertThat(query.params).containsEntry("content_type", "foo");
    assertThat(query.params).containsEntry("order", "fields.bar,sys.id");
  }

  @Test
  public void orderByMultipleOnlySysAndNoContentType() throws Exception {
    query.orderBy("sys.foo", "sys.bar");

    assertThat(query.params).doesNotContainKey("content_type");
    assertThat(query.params).containsEntry("order", "sys.foo,sys.bar");
  }

  @Test
  public void orderByMultipleOneReverse() throws Exception {
    query.orderBy("sys.foo", "-sys.bar");

    assertThat(query.params).containsEntry("order", "sys.foo,-sys.bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void orderByOneNullThrows() throws Exception {
    query.withContentType("foo");
    query.orderBy("fields.bar", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void orderByOneEmptyThrows() throws Exception {
    query.withContentType("foo");
    query.orderBy("fields.bar", "", "fields.baz");
  }

  @Test
  public void reverseOrderByField() throws Exception {
    query.withContentType("foo");
    query.reverseOrderBy("fields.bar");

    assertThat(query.params).containsEntry("content_type", "foo");
    assertThat(query.params).containsEntry("order", "-fields.bar");
  }

  @Test
  public void reverseOrderBySysWithoutField() throws Exception {
    query.reverseOrderBy("sys.foo");

    assertThat(query.params).doesNotContainKey("content_type");
    assertThat(query.params).containsEntry("order", "-sys.foo");
  }

  @Test(expected = IllegalStateException.class)
  public void reverseOrderByThrowsOnFieldWithoutContentType() throws Exception {
    query.reverseOrderBy("fields.foo");
  }

  @Test(expected = IllegalArgumentException.class)
  public void reverseOrderByThrowsOnEmptyKey() throws Exception {
    query.reverseOrderBy("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void reverseOrderByThrowsOnNullKey() throws Exception {
    query.reverseOrderBy(null);
  }

  @Test
  public void limit() throws Exception {
    query.limit(3);

    assertThat(query.params).containsEntry("limit", "3");
  }

  @Test(expected = IllegalArgumentException.class)
  public void limitWithNegativeThrows() throws Exception {
    query.limit(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void limitWithMoreThen1kThrows() throws Exception {
    query.limit(1001);
  }

  @Test
  public void skip() throws Exception {
    query.skip(3);

    assertThat(query.params).containsEntry("skip", "3");
  }

  @Test(expected = IllegalArgumentException.class)
  public void skipWithNegativeThrows() throws Exception {
    query.skip(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void skipWithNegInfThrows() throws Exception {
    query.skip(Integer.MIN_VALUE);
  }

  @Test
  public void includeZero() throws Exception {
    query.include(0);

    assertThat(query.params).containsEntry("include", "0");
  }

  @Test
  public void includeTen() throws Exception {
    query.include(10);

    assertThat(query.params).containsEntry("include", "10");
  }

  @Test(expected = IllegalArgumentException.class)
  public void includeToManyLevelThrows() throws Exception {
    query.include(11);
  }

  @Test(expected = IllegalArgumentException.class)
  public void includeNegative() throws Exception {
    query.include(-1);
  }

  @Test
  public void queryingAssetTitlesDoesNotThrowWithoutTypeSet() {
    final FetchQuery<CDAAsset> assetQuery = new FetchQuery<CDAAsset>(CDAAsset.class, client);
    assetQuery.where("fields.title", IsEqualTo, "bar");
  }

  @Test
  public void queryForAContentTypesNameDoesNotThrow() {
    final FetchQuery<CDAContentType> typeQuery = new FetchQuery<CDAContentType>(CDAContentType.class, client);
    typeQuery.where("name", Matches, "Auth");
  }
}