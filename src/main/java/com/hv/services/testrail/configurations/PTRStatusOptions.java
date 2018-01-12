package com.hv.services.testrail.configurations;

/**
 * An enum used to help identify the various Pentaho related TestRail statuses.
 */
public enum PTRStatusOptions {

  PASSED( "Passed", 1 ),
  BLOCKED( "Blocked", 2 ),
  UNTESTED( "Untested", 3 ),
  RETEST( "Retest", 4 ),
  FAILED( "Failed", 5 ),
  PASSED_KNOWN_ISSUES( "Passed - Known Issues", 7 ),
  BAD_CASE( "Bad Test Case" );

  private String name;
  private long id;

  PTRStatusOptions( String name ) {
    this.name = name;
  }

  PTRStatusOptions( String name, long id ) {
    this.name = name;
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public long getId() {
    return this.id;
  }

}
