package com.contentful.java.cda;

/**
 * This enumeration will be used to formulate more complex search queries.
 *
 * @see AbsQuery#where(String, QueryOperation, Object...)
 */
public class QueryOperation<T> {

  public static final QueryOperation<String> IsEqualTo = new QueryOperation<>("");
  public static final QueryOperation<String> IsNotEqualTo = new QueryOperation<>("[ne]");
  public static final QueryOperation<String> HasOneOf = new QueryOperation<>("[in]");
  public static final QueryOperation<String> HasNoneOf = new QueryOperation<>("[nin]");
  public static final QueryOperation<String> HasAllOf = new QueryOperation<>("[all]");

  public static final QueryOperation<Integer> IsLessThan = new QueryOperation<>("[lt]");
  public static final QueryOperation<Integer> IsLessThanOrEqualTo
      = new QueryOperation<>("[lte]");
  public static final QueryOperation<Integer> IsGreaterThan = new QueryOperation<>("[gt]");
  public static final QueryOperation<Integer> IsGreaterThanOrEqualTo
      = new QueryOperation<>("[gte]");

  public static final QueryOperation<Boolean> Exists = new QueryOperation<>("[exists]", true);

  public static final QueryOperation<String> IsEarlierThan = new QueryOperation<>("[lt]");
  public static final QueryOperation<String> IsEarlierOrAt = new QueryOperation<>("[lte]");
  public static final QueryOperation<String> IsLaterThan = new QueryOperation<>("[gt]");
  public static final QueryOperation<String> IsLaterOrAt = new QueryOperation<>("[gte]");

  public static final QueryOperation<String> Matches = new QueryOperation<>("[match]");

  public static final QueryOperation<Location> IsCloseTo = new QueryOperation<>("[near]");
  public static final QueryOperation<BoundingBox> IsWithinBoundingBoxOf
      = new QueryOperation<>("[within]");
  public static final QueryOperation<BoundingCircle> IsWithinCircleOf
      = new QueryOperation<>("[within]");

  final String operator;
  final T defaultValue;

  /**
   * Create an operation, using only an operator.
   * <p>
   * This means this operation needs to have a value to operate on.
   *
   * @param operator a string representing the url parameter operation (aka. [eq])
   */
  protected QueryOperation(String operator) {
    this(operator, null);
  }

  /**
   * Create an operation which does take its default value, if no value is given.
   *
   * @param operator     the operator to be used.
   * @param defaultValue the default value of the operation.
   */
  protected QueryOperation(String operator, T defaultValue) {
    this.operator = operator;
    this.defaultValue = defaultValue;
  }

  /**
   * Check presence of a default value
   *
   * @return true if this operation can be used without a parameter.
   */
  protected boolean hasDefaultValue() {
    return defaultValue != null;
  }

  /**
   * Model representing coordinates for query operations.
   */
  public static class Location {
    private final double latitude;
    private final double longitude;

    /**
     * Create a location based on its coodinates.
     *
     * @param latitude  of this location.
     * @param longitude of this location.
     */
    public Location(double latitude, double longitude) {
      this.latitude = latitude;
      this.longitude = longitude;
    }

    /**
     * @return returns a url friendly representation.
     */
    @Override
    public String toString() {
      return String.format("%f,%f", latitude, longitude);
    }
  }

  /**
   * Model representing a bounding box in geocoordinates.
   */
  public static class BoundingBox {
    private final Location bottomLeft;
    private final Location topRight;

    /**
     * Construct a bounding box with type save arguments.
     *
     * @param bottomLeft bottom left corner of bounding box.
     * @param topRight   top right corner of bounding box.
     */
    public BoundingBox(Location bottomLeft, Location topRight) {
      this.bottomLeft = bottomLeft;
      this.topRight = topRight;
    }

    /**
     * Simple constructor of bounding box taking double parameters instead of locations
     *
     * @param bottom bottom most location of the box.
     * @param left   left most location of the box.
     * @param top    top most location of the box.
     * @param right  right most location of the box.
     */
    public BoundingBox(double bottom, double left, double top, double right) {
      this.bottomLeft = new Location(bottom, left);
      this.topRight = new Location(top, right);
    }

    /**
     * @return returns a url friendly representation.
     */
    @Override public String toString() {
      return String.format("%s,%s", bottomLeft.toString(), topRight.toString());
    }
  }

  /**
   * Model representing a bounding circle in geocoordinates.
   */
  public static class BoundingCircle {
    private final Location center;
    private final double radius;

    /**
     * Type safe constructor of bounding circle.
     *
     * @param center position of circle on the globe in geo location.
     * @param radius the radius of the circle in kilometer.
     */
    public BoundingCircle(Location center, double radius) {
      this.center = center;
      this.radius = radius;
    }

    /**
     * Simple constructor of bounding circle.
     *
     * @param centerLatitude  center coordinate on latitude axis.
     * @param centerLongitude center coordinate of circle, on longitude axis.
     * @param radius          in km.
     */
    public BoundingCircle(double centerLatitude, double centerLongitude, double radius) {
      this.center = new Location(centerLatitude, centerLongitude);
      this.radius = radius;
    }

    /**
     * @return returns a url friendly representation.
     */
    @Override public String toString() {
      return String.format("%s,%f", center.toString(), radius);
    }
  }
}
