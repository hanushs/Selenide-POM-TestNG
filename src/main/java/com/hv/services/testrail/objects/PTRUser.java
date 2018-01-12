package com.hv.services.testrail.objects;

/**
 * A Pentaho TestRail user class used for storing the retrieved user data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-users">http://docs.gurock
 * .com/testrail-api2/reference-users</a>
 */
public class PTRUser {

  private long userId = -1;
  private boolean activeStatus = false;
  private String fullname = "";
  private String emailAddress = "";

  public PTRUser() {

  }

  // USER ID
  public long getUserId() {
    return userId;
  }

  public void setUserId( long userId ) {
    this.userId = userId;
  }

  // EMAIL ADDRESS
  public String getEmail() {
    return emailAddress;
  }

  public void setEmailAddress( String emailAddress ) {
    this.emailAddress = emailAddress;
  }

  // ACTIVE STATUS
  public boolean isActive() {
    return activeStatus;
  }

  public void setActiveStatus( boolean activeStatus ) {
    this.activeStatus = activeStatus;
  }

  // FULL NAME
  public String getFullname() {
    return fullname;
  }

  public void setFullname( String fullname ) {
    this.fullname = fullname;
  }
}
