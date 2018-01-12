package com.hv.services.testrail.objects;

import java.util.ArrayList;

/**
 * A Pentaho TestRail configuration class used for storing the retrieved configuration data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-configs">http://docs.gurock
 * .com/testrail-api2/reference-configs</a>
 */
public class PTRConfig {

  private long configId = -1;
  private String name = "";
  private ArrayList<PTRConfig> additionalConfigs = new ArrayList<>();

  // CONFIGURATION IDs
  public long getConfigId() {
    return configId;
  }

  public void setConfigId( long configId ) {
    this.configId = configId;
  }

  // CONFIGURATION NAME
  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  // ADDITIONAL CONFIGURATIONS
  public ArrayList<PTRConfig> getAdditionalConfigs() {
    return additionalConfigs;
  }

  public void setAdditionalConfigs( ArrayList<PTRConfig> additionalConfigs ) {
    this.additionalConfigs = additionalConfigs;
  }
}
