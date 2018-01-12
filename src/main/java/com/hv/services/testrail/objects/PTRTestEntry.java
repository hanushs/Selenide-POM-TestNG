package com.hv.services.testrail.objects;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A Pentaho TestRail test entry class used for storing the retrieved test entry data from a test plan
 * returned by the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-plans">http://docs.gurock
 * .com/testrail-api2/reference-plans</a>
 */
public class PTRTestEntry {

  private static final Logger LOGGER = Logger.getLogger( PTRTestEntry.class );

  private String name = "";
  private String description = "";
  private long suiteId = -1;
  private String uniqueId = "";
  private boolean includeAll = false;
  private ArrayList<PTRTestRun> runs = new ArrayList<>();

  public PTRTestEntry() {
  }

  // NAME
  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  // DESCRIPTION
  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  // SUITE ID
  public long getSuiteId() {
    return suiteId;
  }

  public void setSuiteId( long suiteId ) {
    this.suiteId = suiteId;
  }

  // UNIQUE ID
  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId( String uniqueId ) {
    this.uniqueId = uniqueId;
  }

  // INCLUDE ALL
  public boolean isIncludeAll() {
    return includeAll;
  }

  public void setIncludeAll( boolean includeAll ) {
    this.includeAll = includeAll;
  }

  // TEST RUNS
  public ArrayList<PTRTestRun> getRuns() {
    return runs;
  }

  public void addRun( PTRTestRun... newRuns ) {

    for ( PTRTestRun newRun : Arrays.asList( newRuns ) ) {

      boolean hasExistingConfig = false;

      for ( PTRTestRun existingRun : runs ) {
        if ( newRun.getConfigurations().equals( existingRun.getConfigurations() ) ) {
          hasExistingConfig = true;
        }
      }

      if ( !hasExistingConfig ) {
        runs.add( newRun );
      } else {
        LOGGER.debug(
          "A run with configuration ids for '" + newRun.getConfigName() + "' already exists in test plan entry: " + name
            + " (Unique id: " + uniqueId + ")" );
      }
    }
  }
}
