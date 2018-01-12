package com.hv.services.testrail.configurations;

/**
 * A class to house the enums that help identify the various Pentaho related TestRail suite configurations.
 */
public class PTRConfigOptions {

  /**
   * An enum used to help identify the various Pentaho related types of configurations available.
   */
  public enum Types {

    BIG_DATA( "Big Data" ),
    CLIENT_OS( "Client Operating System" ),
    DATABASES( "Databases" ),
    HADOOP_DIST_SHIM( "Hadoop Distribution Shims (Big Data)" ),
    HADOOP_VENDOR_CLUSTER_TYPE( "Hadoop vendors and cluster types" ),
    LOCALE( "Locale" ),
    PATCH_TYPE( "Patch type" ),
    SERVER_OS( "Server Operating System" ),
    SPARK( "Spark version (Big Data)" ),
    WEB_APP_SERVER( "Web Application Server" ),
    WEB_BROWSER( "Web Browser" );

    private String name;

    Types( String name ) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }
  }

  /**
   * An enum used to help identify the various Pentaho supported browser configurations.
   */
  public enum Browsers {
    CHROME( "Chrome" ),
    FIREFOX( "Firefox" ),
    SAFARI( "Safari" ),
    IE( "Microsoft IE11" ),
    EDGE( "Microsoft Edge" );

    private String name;

    Browsers( String name ) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }
  }

  /**
   * An enum used to help identify the various Pentaho supported operating system configurations.
   */
  public enum ClientOS {
    IPAD( "iPad 8" ),
    OSX_10_11( "OSX 10.11" ),
    OSX_10_12( "OSX 10.12" ),
    UBUNTU_16( "Ubuntu Desktop 16.04" ),
    UBUNTU_14( "Ubuntu Desktop 14.04" ),
    WINDOWS_10( "Windows 10" ),
    WINDOWS_7( "Windows 7" );

    private String name;

    ClientOS( String name ) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }
  }

  /**
   * An enum used to help identify the various native Pentaho supported locales and their abbreviations.
   */
  public enum Locales {
    ENGLISH( "en", "English" ),
    FRENCH( "fr", "French" ),
    GERMAN( "de", "German" ),
    JAPANESE( "ja", "Japanese" );

    private String abbreviation;
    private String fullName;

    Locales( String abbreviation, String fullName ) {
      this.fullName = fullName;
    }

    public String getFullName() {
      return this.fullName;
    }

    public String getAbbreviation() {
      return this.abbreviation;
    }
  }

}
