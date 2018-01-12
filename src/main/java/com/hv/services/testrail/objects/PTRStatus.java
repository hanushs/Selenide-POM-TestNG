package com.hv.services.testrail.objects;

/**
 * A Pentaho TestRail status class used for storing the retrieved status options from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-statuses">http://docs.gurock
 * .com/testrail-api2/reference-statuses</a>
 */
public class PTRStatus {

  private long statusId = -1;
  private String displayName = "";
  private String systemName = "";

  // UNIQUE STATUS ID
  public long getStatusId() {
    return statusId;
  }

  public void setStatusId( long statusId ) {
    this.statusId = statusId;
  }

  // DISPLAY NAME
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName( String displayName ) {
    this.displayName = displayName;
  }

  // SYSTEM NAME
  public String getSystemName() {
    return systemName;
  }

  public void setSystemName( String systemName ) {
    this.systemName = systemName;
  }
}
