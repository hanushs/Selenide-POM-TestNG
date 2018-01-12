package com.hv.services.testrail.objects;

/**
 * A Pentaho TestRail test result class used for storing the retrieved test result data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-results">http://docs.gurock
 * .com/testrail-api2/reference-results</a>
 */
public class PTRTestResult {

  private long resultId = -1;
  private long statusId = 3;
  private long testInstanceId = -1;
  private long assignedToUserId = -1;
  private String comment = "";
  private long createdByUserId = -1;
  private long createdOn = -1;
  private String defects = "";
  private String timeElapsed = "";
  private String buildVersion = "";

  // RESULT ID
  public long getResultId() {
    return resultId;
  }

  public void setResultId( long resultId ) {
    this.resultId = resultId;
  }

  // STATUS ID
  public long getStatusId() {
    return statusId;
  }

  public void setStatusId( long statusId ) {
    this.statusId = statusId;
  }

  // TEST INSTANCE ID
  public long getTestInstanceId() {
    return testInstanceId;
  }

  public void setTestInstanceId( long testInstanceId ) {
    this.testInstanceId = testInstanceId;
  }

  // ASSIGNED TO ID
  public long getAssignedToUserId() {
    return assignedToUserId;
  }

  public void setAssignedToUserId( long assignedToUserId ) {
    this.assignedToUserId = assignedToUserId;
  }

  // COMMENT
  public String getComment() {
    return comment;
  }

  public void setComment( String comment ) {
    this.comment = comment;
  }

  // CREATED BY USER ID
  public long getCreatedByUserId() {
    return createdByUserId;
  }

  public void setCreatedByUserId( long createdByUserId ) {
    this.createdByUserId = createdByUserId;
  }

  // DATE CREATED ON
  public long getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn( long createdOn ) {
    this.createdOn = createdOn;
  }

  // DEFECTS
  public String getDefects() {
    return defects;
  }

  public void setDefects( String defects ) {
    this.defects = defects;
  }

  // TIME ELAPSED
  public String getTimeElapsed() {
    return timeElapsed;
  }

  public void setTimeElapsed( String timeElapsed ) {
    this.timeElapsed = timeElapsed;
  }

  // BUILD VERSION
  public String getBuildVersion() {
    return buildVersion;
  }

  public void setBuildVersion( String buildVersion ) {
    this.buildVersion = buildVersion;
  }
}
