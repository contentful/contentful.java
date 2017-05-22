package com.contentful.java.cda.interceptor;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Locale.getDefault;
import static java.util.regex.Pattern.compile;

/**
 * This header interceptor will add contentful relevant information to the custom user agent header.
 */
public class ContentfulUserAgentHeaderInterceptor extends HeaderInterceptor {
  public static final String HEADER_NAME = "X-Contentful-User-Agent";

  /**
   * A section of values used to fill out the Contentful custom HTTP header.
   */
  public static class Section {
    /**
     * Defines a version of this section.
     */
    public static class Version {

      private static final String VERSION_REGEX = "^([0-9]+).([0-9]+).([0-9]+)(.*)?$";
      private static final Pattern VERSION_PATTERN = compile(VERSION_REGEX);
      private static final String STABILITY_REGEX = "^([a-zA-Z]+[0-9]*).*";
      private static final Pattern STABILITY_PATTERN = compile(STABILITY_REGEX);

      /**
       * Convert a version into a Semver and Contentful conform version number.
       * <p>
       * A valid version string would be one similar to
       * <ul>
       * <li>0.9.7</li>
       * <li>1.0.0</li>
       * <li>1.3.4</li>
       * <li>0.2.6-beta</li>
       * <li>0.2.6-beta1</li>
       * <li>1.0.3-RC43</li>
       * </ul>
       *
       * @param version with 3 numbers for major, minor, patch and stability (dev, BETA3, etc)
       * @return a version of (1, 2, 3, dev)
       * @throws NumberFormatException    if major, minor or patch are no numbers
       * @throws IllegalArgumentException if numbers are negative.
       * @throws IllegalArgumentException if stability cannot be read.
       * @see <a href="http://semver.org/">Semver.org</a>
       */
      public static Version parse(String version) {
        final Matcher matcher = VERSION_PATTERN.matcher(version);

        if (matcher.find() && matcher.groupCount() == 4) {
          // ignore first full matching result
          final int major = Integer.parseInt(matcher.group(1));
          final int minor = Integer.parseInt(matcher.group(2));
          final int patch = Integer.parseInt(matcher.group(3));

          final String stability = parseStability(matcher.group(4));
          return new Version(major, minor, patch, stability);
        } else {
          throw new IllegalArgumentException("Could not parse version: " + version);
        }
      }

      private static String parseStability(String stability) {
        // check for hyphen
        if (!stability.startsWith("-")) {
          return null;
        }
        stability = stability.substring(1);

        // check for consecutive letters
        final Matcher matcher = STABILITY_PATTERN.matcher(stability);
        if (matcher.find()) {
          return matcher.group(0);
        } else {
          return null;
        }
      }

      private final int major;
      private final int minor;
      private final int patch;
      private final String stability;

      /**
       * Create a release version, omitting stability
       *
       * @param major How many breaking changes did this version release?
       * @param minor How many additional backwards compatible changes were added?
       * @param patch How many bugs were fixed in the release?
       * @see #parse(String)
       */
      public Version(int major, int minor, int patch) {
        this(major, minor, patch, null);
      }

      /**
       * Create a version including a stability.
       *
       * @param major     How many breaking changes did this version release?
       * @param minor     How many additional backwards compatible changes were added?
       * @param patch     How many bugs were fixed in the release?
       * @param stability Is this a stable version(null) or is this not (dev, BETA, …)?
       * @see #parse(String)
       */
      public Version(int major, int minor, int patch, String stability) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.stability = stability;
      }

      /**
       * @return major version part.
       */
      public int getMajor() {
        return major;
      }

      /**
       * @return minor version part.
       */
      public int getMinor() {
        return minor;
      }

      /**
       * @return patch part.
       */
      public int getPatch() {
        return patch;
      }

      /**
       * @return stability or null.
       */
      public String getStability() {
        return stability;
      }

      /**
       * @return version into a human and machine readable String.
       */
      @Override public String toString() {
        if (stability == null) {
          return format(getDefault(), "%d.%d.%d", major, minor, patch);
        } else {
          return format(getDefault(), "%d.%d.%d-%s", major, minor, patch, stability);
        }
      }
    }

    private final static String APP = "app";
    private final static String INTEGRATION = "integration";
    private final static String SDK = "sdk";
    private final static String PLATFORM = "platform";
    private final static String OS = "os";

    /**
     * Create an app section.
     *
     * @param name    of the app.
     * @param version of the app.
     * @return a new Section.
     */
    public static Section integration(String name, Version version) {
      return new Section(INTEGRATION, name, version);
    }

    /**
     * Create an sdk section.
     *
     * @param name    of the sdk.
     * @param version of the sdk.
     * @return a new Section.
     */
    public static Section sdk(String name, Version version) {
      return new Section(SDK, name, version);
    }

    /**
     * Create an platform section.
     *
     * @param name    of the platform.
     * @param version of the platform.
     * @return a new Section.
     */
    public static Section platform(String name, Version version) {
      return new Section(PLATFORM, name, version);
    }

    /**
     * Create an os section.
     *
     * @param name    of the os.
     * @param version of the os.
     * @return a new Section.
     */
    public static Section os(String name, Version version) {
      return new Section(OS, name, version);
    }

    /**
     * Create an app section.
     *
     * @param name    of the app.
     * @param version of the app.
     * @return a new Section.
     * @throws IllegalArgumentException if name is null or empty.
     */
    public static Section app(String name, Version version) {
      return new Section(APP, check(name).replace(" ", ""), version);
    }

    private static String check(String name) {
      if (name == null || name.length() <= 0) {
        throw new IllegalArgumentException("Cannot have an empty name for a section.");
      }
      return name;
    }

    private final String identifier;
    private final String name;
    private final Version version;

    /**
     * Create a section.
     *
     * @param name    How do we call this header section?
     * @param version What is the version of this fields' value?
     */
    private Section(String identifier, String name, Version version) {
      this.identifier = identifier;
      this.name = name;
      this.version = version;
    }

    /**
     * @return the name of this section.
     */
    public String getName() {
      return name;
    }

    /**
     * @return the version of this section.
     */
    public Version getVersion() {
      return version;
    }

    /**
     * @return which identifier this section uses.
     */
    public String getIdentifier() {
      return identifier;
    }

    /**
     * @return a string representing the section.
     */
    public String toString() {
      if (getVersion() == null) {
        return format(
            getDefault(),
            "%s %s; ",
            getIdentifier(),
            getName()
        );
      } else {
        return format(
            getDefault(),
            "%s %s/%s; ",
            getIdentifier(),
            getName(),
            getVersion().toString()
        );
      }
    }
  }

  /**
   * Create Header interceptor, saving parameters.
   *
   * @param sections a list of sections to be used to identify this application.
   */
  public ContentfulUserAgentHeaderInterceptor(Section... sections) {
    super(HEADER_NAME, sectionsToString(checkSections(sections)));
  }

  private static Section[] checkSections(Section[] sections) {
    if (sections == null || sections.length <= 0) {
      throw new IllegalArgumentException("sections cannot be empty.");
    }
    return sections;
  }

  private static String sectionsToString(Section[] sections) {
    // take last section of same identifier
    final LinkedHashMap<String, Section> mappedSections = new LinkedHashMap<String, Section>();
    for (final Section section : sections) {
      if (section != null) {
        final String identifier = section.getIdentifier();
        mappedSections.put(identifier, section);
      }
    }

    // Stringify sections
    final StringBuilder builder = new StringBuilder();
    for (final Section section : mappedSections.values()) {
      builder.append(section.toString());
    }
    return builder.toString();
  }
}
