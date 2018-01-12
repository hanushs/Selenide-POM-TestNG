package com.hv.services.testrail.objects;

/**
 * A Pentaho TestRail test step class used for storing the retrieved step data from a test case retrieved from the
 * TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-cases#get_case">http://docs.gurock
 *      .com/testrail-api2/reference-cases#get_case</a>
 */

public class PTRTestStep {

  public PTRTestStep() {

  }

  private long stepNumber = -1;
  private String stepContent = "";
  private String expectedResult = "";
  private String actualResult = "";
  private long statusId = 3;

  // STEP NUMBER
  public long getStepNumber() {
    return stepNumber;
  }

  public void setStepNumber( long stepNumber ) {
    this.stepNumber = stepNumber;
  }

  // STEP CONTENT
  public String getStepContent() {
    return stepContent;
  }

  public void setStepContent( String stepContent ) {
    this.stepContent = stepContent;
  }

  // EXPECTED RESULTS
  public String getExpectedResult() {
    return expectedResult;
  }

  public void setExpectedResult( String expectedResult ) {
    this.expectedResult = expectedResult;
  }

  // ACTUAL RESULTS
  public String getActualResult() {
    return actualResult;
  }

  public void setActualResult( String actualResult ) {
    this.actualResult = actualResult;
  }

  // STATUS ID
  public long getStatusId() {
    return statusId;
  }

  public void setStatusId( long statusId ) {
    this.statusId = statusId;
  }
}
