package com.hv.services.testrail.services;

import com.hv.services.testrail.PTRClient;
import com.hv.services.testrail.configurations.PTRConfigOptions;
import com.hv.services.testrail.objects.PTRConfig;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.util.Strings;

import java.util.ArrayList;

/**
 * A Pentaho TestRail configuration class used for handling configurations (custom or not) from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-configs">http://docs.gurock
 * .com/testrail-api2/reference-configs</a>
 */
public class PTRConfigService extends PTRClient {

  private static final Logger LOGGER = Logger.getLogger( PTRConfigService.class );
  private static final String DEFAULT_FALLBACK_LOCALE = "en";
  private ArrayList<PTRConfig> globalConfigs = new ArrayList<>();
  private ArrayList<PTRConfig> globalIgnoredConfigs = new ArrayList<>();
  private PTRConfig activeBrowserConfig = new PTRConfig();
  private PTRConfig activeLocaleConfig = new PTRConfig();

  public PTRConfigService( long projectId, String browser, String locale ) {
    globalConfigs = getConfigs( projectId );
    setIgnoredConfigurations( getIgnoredBrowsers(), getIgnoredLocales() );
    setActiveTestConfigurations( browser, locale );
    setGlobalConfigService( this );
  }

  /**
   * A method used to retrieve specific configurations from the stored global list, regardless of ignored options.
   *
   * @param type A {@link PTRConfigOptions.Types} option.
   * @return An ArrayList of matching {@link PTRConfig} options.
   */
  public ArrayList<PTRConfig> getConfigType( PTRConfigOptions.Types type ) {
    return getConfigType( type, false );
  }

  /**
   * A method used to retrieve specific configurations from the stored global list, with param to specify whether or not
   * to exclude any ignored options.
   *
   * @param type             A {@link PTRConfigOptions.Types} option.
   * @param isIgnoredApplied True to return configurations excluding any ignored, false to return all matching
   *                         configurations.
   * @return An ArrayList of matching {@link PTRConfig} options.
   */
  public ArrayList<PTRConfig> getConfigType( PTRConfigOptions.Types type, boolean isIgnoredApplied ) {

    ArrayList<PTRConfig> requestedConfigs = new ArrayList<>();

    for ( PTRConfig config : globalConfigs ) {
      if ( config.getName().equals( type.getName() ) ) {
        requestedConfigs = config.getAdditionalConfigs();
      }
    }

    if ( isIgnoredApplied ) {
      for ( PTRConfig ignoredConfig : globalIgnoredConfigs ) {
        for ( PTRConfig requestedConfig : requestedConfigs ) {
          if ( requestedConfig.getConfigId() == ignoredConfig.getConfigId() ) {
            requestedConfigs.remove( requestedConfig );
            break;
          }
        }
      }
    }

    return requestedConfigs;
  }

  /**
   * A method used to set the current test's active configuration setup. (i.e. The browser and locale being tested).
   *
   * @param browser The name of a Pentaho supported modern browser.
   * @param locale  The abbreviation of a Pentaho supported locale.
   */
  private void setActiveTestConfigurations( String browser, String locale ) {

    LOGGER.info( "Attempting to set the active test configurations..." );

    // Browser Configuration Check
    if ( !Strings.isNullOrEmpty( browser ) ) {
      activeBrowserConfig = getBrowserConfiguration( browser );
    }

    // Locale configuration MUST be added after browser configuration for comparison checks elsewhere.
    if ( !Strings.isNullOrEmpty( locale ) ) {
      activeLocaleConfig = getLocaleConfiguration( locale );
    } else {
      activeLocaleConfig = getLocaleConfiguration( DEFAULT_FALLBACK_LOCALE );
    }

    // Check against the ignored configurations
    for ( PTRConfig ignoredConfig : globalIgnoredConfigs ) {
      if ( ignoredConfig.getConfigId() == activeBrowserConfig.getConfigId()
        || ignoredConfig.getConfigId() == activeLocaleConfig.getConfigId() ) {
        Assert.fail(
          "Configuration Error: You cannot run a test with a configuration that has been ignored from the test plan. "
            + "Please check the _testrail.properties file for any ignored browsers or locales!" );
      }
    }

    // Check if active browser was set to a real value.
    if ( activeBrowserConfig.getConfigId() > 0 ) {
      LOGGER
        .info( "Successfully set the active browser configuration for TestRail as: " + activeBrowserConfig.getName() );
    }

    // Check if active locale was set to a real value, if not, we need to fail. A locale has to be present.
    if ( activeLocaleConfig.getConfigId() > 0 ) {
      LOGGER
        .info( "Successfully set the active locale configuration for TestRail as: " + activeLocaleConfig.getName() );
    } else {
      Assert.fail(
        "Configuration Error: The locale for the test was not configured properly or was unable to fallback to the "
          + "default locale." );
    }
  }

  /**
   * A method used to return an ArrayList<Long> containing the active test configuration ids.
   *
   * @return ArrayList<Long> with active configuration ids.
   */
  public ArrayList<Long> getActiveTestConfigurationIds() {
    ArrayList<Long> activeConfigIds = new ArrayList<>();

    // Check if a browser configuration is active or not
    if ( activeBrowserConfig.getConfigId() > 0 ) {
      activeConfigIds.add( activeBrowserConfig.getConfigId() );
    }

    activeConfigIds.add( activeLocaleConfig.getConfigId() );

    return activeConfigIds;
  }

  /**
   * A helper method used to retrieve the active browser configuration for the current test.
   *
   * @return A {@link PTRConfig} object with the active browser configurations.
   */
  public PTRConfig getActiveBrowserConfiguration() {
    return activeBrowserConfig;
  }

  /**
   * A helper method used to retrieve the active locale configuration for the current test.
   *
   * @return A {@link PTRConfig} object with the active locale configurations.
   */
  public PTRConfig getActiveLocaleConfiguration() {
    return activeLocaleConfig;
  }

  /**
   * A helper method used to retrieve just the configuration name in the following format: "(Browser, Locale)"
   *
   * @return The full string name for the active configuration.
   */
  public String getActiveTestConfigurationName() {
    String activeTestConfigName = "(";

    // If the active browser does not exist, do not use it in the configuration name.
    if ( activeBrowserConfig.getConfigId() > 0 ) {
      activeTestConfigName += activeBrowserConfig.getName() + ", ";
    }

    activeTestConfigName += activeLocaleConfig.getName() + ")";

    return activeTestConfigName;
  }

  /**
   * A helper method used to retrieve the list of ignored configurations.
   *
   * @return An ArrayList of {@link PTRConfig} objects that have been ignored.
   */
  public ArrayList<PTRConfig> getIgnoredConfigurations() {
    return globalIgnoredConfigs;
  }

  /**
   * A method used to set the globally ignored browser(s) and locale(s) so when a new test plan or entry is created, any
   * browser(s) or locale(s) set in this list will NOT be a part of the creation process.
   *
   * @param browsersToIgnore An non-spaced, comma separated list of Pentaho supported browser names. (e.g.
   *                         chrome,firefox,ie,edge)
   * @param localesToIgnore  An non-spaced, comma separated list of Pentaho supported locale abbreviations. (e.g.
   *                         en,de,fr,ja)
   */
  public void setIgnoredConfigurations( String browsersToIgnore, String localesToIgnore ) {

    // Ignored Browser Configurations
    if ( !browsersToIgnore.isEmpty() ) {
      for ( String ignoredBrowser : browsersToIgnore.split( "," ) ) {
        PTRConfig ignoredBrowserConfig = getBrowserConfiguration( ignoredBrowser );

        if ( !globalIgnoredConfigs.contains( ignoredBrowserConfig ) ) {
          globalIgnoredConfigs.add( ignoredBrowserConfig );
        }
      }
    }

    // Ignored Locale Configurations
    if ( !localesToIgnore.isEmpty() ) {
      for ( String ignoredLocale : localesToIgnore.split( "," ) ) {
        PTRConfig ignoredLocaleConfig = getLocaleConfiguration( ignoredLocale );

        if ( !globalIgnoredConfigs.contains( ignoredLocaleConfig ) ) {
          globalIgnoredConfigs.add( ignoredLocaleConfig );
        }
      }
    }
  }


  /**
   * A package-private method used to convert a single JSONObject configuration into {@link PTRConfig} objects.
   *
   * @param jsonObj A JSONObject containing configuration information from TestRail API.
   * @return A {@link PTRConfig} object.
   */
  PTRConfig convertConfigId( JSONObject jsonObj ) {

    PTRConfig requestedConfig = new PTRConfig();

    for ( int i = 0; i < jsonObj.size(); i++ ) {
      for ( PTRConfig config : globalConfigs ) {
        if ( config.getConfigId() == (long) jsonObj.get( i ) ) {
          requestedConfig = config;
        }
      }
    }
    return requestedConfig;
  }

  /**
   * A package-private method used to convert multiple configurations in a JSONArray into an ArrayList of {@link
   * PTRConfig} objects.
   *
   * @param jsonArray A JSONArray containing configuration information from TestRail API.
   * @return An ArrayList of {@link PTRConfig} objects.
   */
  ArrayList<PTRConfig> convertConfigIds( JSONArray jsonArray ) {

    ArrayList<PTRConfig> requestedConfigs = new ArrayList<>();

    for ( int i = 0; i < jsonArray.size(); i++ ) {
      for ( PTRConfig config : globalConfigs ) {
        if ( config.getConfigId() == (long) jsonArray.get( i ) ) {
          requestedConfigs.add( config );
        } else {
          if ( config.getAdditionalConfigs().size() > 0 ) {
            for ( PTRConfig additionalConfig : config.getAdditionalConfigs() ) {
              if ( additionalConfig.getConfigId() == (long) jsonArray.get( i ) ) {
                requestedConfigs.add( additionalConfig );
              }
            }
          }
        }
      }
    }

    return requestedConfigs;
  }

  /**
   * A helper method used to retrieve a browser configuration.
   *
   * @param browser The name of a Pentaho supported modern browser.
   * @return A {@link PTRConfig} object of the browser.
   */
  private PTRConfig getBrowserConfiguration( String browser ) {

    ArrayList<PTRConfig> browserConfigs = getConfigType( PTRConfigOptions.Types.WEB_BROWSER );
    PTRConfig config = new PTRConfig();

    if ( !browser.isEmpty() ) {
      switch ( browser.toLowerCase() ) {
        case "chrome": {
          browser = PTRConfigOptions.Browsers.CHROME.getName();
          break;
        }
        case "firefox": {
          browser = PTRConfigOptions.Browsers.FIREFOX.getName();
          break;
        }
        case "ie": {
          browser = PTRConfigOptions.Browsers.IE.getName();
          break;
        }
        case "safari": {
          browser = PTRConfigOptions.Browsers.SAFARI.getName();
          break;
        }
        case "edge": {
          browser = PTRConfigOptions.Browsers.EDGE.getName();
          break;
        }
      }

      for ( PTRConfig browserConfig : browserConfigs ) {
        if ( browserConfig.getName().equals( browser ) ) {
          config.setName( browserConfig.getName() );
          config.setConfigId( browserConfig.getConfigId() );
          break;
        }
      }
    }

    return config;
  }

  /**
   * A helper method used to retrieve a locale configuration.
   *
   * @param locale The abbreviation of a Pentaho supported locale.
   * @return A {@link PTRConfig} object of the locale.
   */
  private PTRConfig getLocaleConfiguration( String locale ) {

    ArrayList<PTRConfig> localeConfigs = getConfigType( PTRConfigOptions.Types.LOCALE );
    PTRConfig config = new PTRConfig();

    if ( !locale.isEmpty() ) {

      switch ( locale.toLowerCase() ) {
        case "en": {
          locale = PTRConfigOptions.Locales.ENGLISH.getFullName();
          break;
        }
        case "fr": {
          locale = PTRConfigOptions.Locales.FRENCH.getFullName();
          break;
        }
        case "de": {
          locale = PTRConfigOptions.Locales.GERMAN.getFullName();
          break;
        }
        case "ja": {
          locale = PTRConfigOptions.Locales.JAPANESE.getFullName();
          break;
        }
      }

      for ( PTRConfig localeConfig : localeConfigs ) {
        if ( localeConfig.getName().equals( locale ) ) {
          config.setName( localeConfig.getName() );
          config.setConfigId( localeConfig.getConfigId() );
          break;
        }
      }
    }

    return config;
  }

  /**
   * A helper method used to send the GET request from the Pentaho TestRail API
   *
   * @param projectId The TestRail project id to retrieve configuration data for.
   * @return An ArrayList of {@link PTRConfig} objects.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-configs#get_configs">http://docs.gurock
   * .com/testrail-api2/reference-configs#get_configs</a>
   */
  private ArrayList<PTRConfig> getConfigs( long projectId ) {

    String getRequest = "get_configs/" + projectId;

    Object response = sendGet( getRequest );

    ArrayList<PTRConfig> configs = new ArrayList<>();

    if ( response instanceof JSONArray ) {
      configs = createPTRConfigs( (JSONArray) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return configs;
  }


  /**
   * A helper method used to convert a JSONArray of configurations into an ArrayList of {@link PTRConfig} objects.
   *
   * @param jsonArray A JSONArray of configurations to convert.
   * @return An ArrayList of {@link PTRConfig} objects.
   */
  private ArrayList<PTRConfig> createPTRConfigs( JSONArray jsonArray ) {

    ArrayList<PTRConfig> configs = new ArrayList<>();

    if ( jsonArray.size() > 0 ) {

      for ( Object obj : jsonArray ) {
        configs.add( createPTRConfig( (JSONObject) obj ) );
      }

    } else {
      Assert.fail( "The given JSONArray does not any data for configurations. Returning empty array list." );
    }

    return configs;
  }

  /**
   * A helper method used to convert a single JSONObject into a single {@link PTRConfig}.
   *
   * @param jsonObj The JSONObject to convert.
   * @return The converted configuration.
   */
  private PTRConfig createPTRConfig( JSONObject jsonObj ) {

    PTRConfig config = new PTRConfig();

    if ( jsonObj != null && !jsonObj.isEmpty() ) {

      long configId = -1;

      // Check if the configuration has an id.
      if ( jsonObj.containsKey( "id" ) && jsonObj.get( "id" ) != null ) {
        config.setConfigId( (long) jsonObj.get( "id" ) );
        configId = config.getConfigId();
      } else {
        Assert.fail( "The configuration provided does not have an id associated with it!" );
      }

      // Check if the configuration has a name.
      if ( jsonObj.containsKey( "name" ) && jsonObj.get( "name" ) != null ) {
        config.setName( (String) jsonObj.get( "name" ) );
      } else {
        LOGGER.debug( "Configuration '" + configId + "' does not have a name!" );
      }

      // Check if the configuration has any additional configurations.
      if ( jsonObj.containsKey( "configs" ) && jsonObj.get( "configs" ) != null ) {
        if ( jsonObj.get( "configs" ) instanceof JSONArray ) {
          config.setAdditionalConfigs( createPTRConfigs( (JSONArray) jsonObj.get( "configs" ) ) );
        }
      } else {
        LOGGER.debug( "Configuration '" + configId + "' does not have any additional configurations!" );
      }

    } else {
      Assert.fail( "The configuration object provided was empty! Could not convert to a PTRConfig object!" );
    }

    return config;
  }
}
