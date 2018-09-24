package com.contentful.java.cda.interceptor;

import com.contentful.java.cda.Platform;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;

/**
 * This header interceptor will add contentful relevant information to the custom user agent header.
 */
public class ContentfulUserAgentHeaderInterceptor extends HeaderInterceptor {
  public static final String HEADER_NAME = "X-Contentful-User-Agent";
  private static final String NO_ASCII_REGEX = "[^\\p{ASCII}]+";
  private static final Pattern NO_ASCII_PATTERN = Pattern.compile(NO_ASCII_REGEX);

  /**
   * A section of values used to fill out the Contentful custom HTTP header.
   */
  public static class Section {

    /**
     * Fixed enumeration of what operating systems we support.
     */
    public enum OperatingSystem {
      iOS, tvOS, watchOS, macOS, Windows, Linux, Android;

      /**
       * Parses a string to the closes match of a given OperatingSystemConstant.
       *
       * @param osName the name of the os, as returned from the platform.
       * @return one of the {@link OperatingSystem} fields closely matching, or null if unknown.
       * @see Platform
       */
      public static OperatingSystem parse(String osName) {
        osName = removeNonAsciiCharacters(osName);
        if (osName.startsWith("Windows")) {
          return Windows;
        }
        if (osName.startsWith("Mac OS")) {
          return macOS;
        }
        if (osName.startsWith("Android")) {
          return Android;
        }
        return Linux;
      }
    }

    /**
     * Defines a version of this section.
     */
    public static class Version {

      private static final String VERSION_REGEX = "^(\\p{N}+).(\\p{N}+).(\\p{N}+)(.*)?$";
      private static final Pattern VERSION_PATTERN = compile(VERSION_REGEX);
      private static final int VERSION_PATTERN_GROUP_COUNT = 4;
      private static final int VERSION_PATTERN_GROUP_MAJOR = 1;
      private static final int VERSION_PATTERN_GROUP_MINOR = 2;
      private static final int VERSION_PATTERN_GROUP_PATCH = 3;
      private static final String STABILITY_REGEX = "^\\p{Alpha}+\\p{Alnum}*";
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
       * @return a version of (1, 2, 3, dev), or null if a parsing error occurred.
       * @see <a href="http://semver.org/">Semver.org</a>
       */
      public static Version parse(String version) {
        if (version == null || version.length() <= 0) {
          return null;
        }

        if (!version.contains(".")) {
          return null;
        } else if (version.indexOf(".") == version.lastIndexOf(".")) {
          version = version + ".0";
        }

        final Matcher matcher = VERSION_PATTERN.matcher(version);

        if (matcher.find() && matcher.groupCount() == VERSION_PATTERN_GROUP_COUNT) {
          // ignore first full matching result
          int major = extractNumberFromGroup(matcher, VERSION_PATTERN_GROUP_MAJOR);
          int minor = extractNumberFromGroup(matcher, VERSION_PATTERN_GROUP_MINOR);
          int patch = extractNumberFromGroup(matcher, VERSION_PATTERN_GROUP_PATCH);

          if (major == minor && minor == patch && patch == 0) {
            return null;
          }

          final String stability = parseStability(matcher.group(4));
          return new Version(major, minor, patch, stability);
        } else {
          return null;
        }
      }

      private static int extractNumberFromGroup(Matcher matcher, int group) {
        try {
          return Integer.parseInt(matcher.group(group));
        } catch (IllegalArgumentException e) {
          return 0;
        }
      }

      private static String parseStability(String stability) {
        // check for hyphen
        if (!stability.startsWith("-")) {
          return null;
        }
        stability = stability.substring(1);
        stability = removeNonAsciiCharacters(stability);

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
          return format(Locale.ENGLISH, "%d.%d.%d", major, minor, patch);
        } else {
          return format(Locale.ENGLISH, "%d.%d.%d-%s", major, minor, patch, stability);
        }
      }
    }

    private static final String APP = "app";
    private static final String INTEGRATION = "integration";
    private static final String SDK = "sdk";
    private static final String PLATFORM = "platform";
    private static final String OS = "os";

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
     * @param os      one of the supported and understood operation systems.
     * @param version the version of the os.
     * @return a new Section.
     */
    public static Section os(OperatingSystem os, Version version) {
      return new Section(OS, os.name(), version);
    }

    /**
     * Create an app section.
     *
     * @param name    of the app.
     * @param version of the app.
     * @return a new Section or null, if app name is invalid.
     */
    public static Section app(String name, Version version) {
      name = check(name);
      if (name == null) {
        return null;
      } else {
        return new Section(APP, name, version);
      }
    }

    private static String check(String name) {
      if (name == null || name.length() <= 0) {
        return null;
      }
      name = removeNonAsciiCharacters(name);
      return name.replace(" ", "-").toLowerCase();
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
      this.identifier = removeNonAsciiCharacters(identifier);
      this.name = removeNonAsciiCharacters(name);
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
            Locale.ENGLISH,
            "%s %s; ",
            getIdentifier(),
            getName()
        );
      } else {
        return format(
            Locale.ENGLISH,
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
    final LinkedHashMap<String, Section> mappedSections = new LinkedHashMap<>();
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

  static String removeNonAsciiCharacters(String input) {
    final Matcher m = NO_ASCII_PATTERN.matcher(input);
    String result = input;
    if (m.find()) {
      result = m.replaceAll("");
    }
    return result;
  }
}
