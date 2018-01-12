package com.hv.services.testrail.objects;

import java.util.ArrayList;

/**
 * A Pentaho TestRail test Case class used for storing the retrieved test case data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-cases">http://docs.gurock
 * .com/testrail-api2/reference-cases</a>
 */
public class PTRTestCase {

  public PTRTestCase() {

  }

  private long caseId = -1;
  private long createdByUserId = -1;
  private long createdOn = -1;
  private String estimate = "1m";
  private String estimateForecast = "1m";
  private long milestoneId = -1;
  private long priorityId = -1;
  private long sectionId = -1;
  private long suiteId = -1;
  private long templateId = -1;
  private long typeId = -1;
  private long updatedByUserId = -1;
  private long dateUpdatedOn = -1;
  private String title = "";
  private String references = "";
  private String preconditions = "";
  private ArrayList<PTRTestStep> testSteps = new ArrayList<>();

  // CASE ID
  public long getCaseId() {
    return caseId;
  }

  public void setCaseId( long caseId ) {
    this.caseId = caseId;
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

  // ESTIMATED TIME
  public String getEstimate() {
    return estimate;
  }

  public void setEstimate( String estimate ) {
    this.estimate = estimate;
  }

  // FORECAST ESTIMATE
  public String getEstimateForecast() {
    return estimateForecast;
  }

  public void setEstimateForecast( String estimateForecast ) {
    this.estimateForecast = estimateForecast;
  }

  // MILESTONE ID
  public long getMilestoneId() {
    return milestoneId;
  }

  public void setMilestoneId( long milestoneId ) {
    this.milestoneId = milestoneId;
  }

  // PRIORITY ID
  public long getPriorityId() {
    return priorityId;
  }

  public void setPriorityId( long priorityId ) {
    this.priorityId = priorityId;
  }

  // SECTION ID
  public long getSectionId() {
    return sectionId;
  }

  public void setSectionId( long sectionId ) {
    this.sectionId = sectionId;
  }

  // SUITE ID
  public long getSuiteId() {
    return suiteId;
  }

  public void setSuiteId( long suiteId ) {
    this.suiteId = suiteId;
  }

  // TEMPLATE ID
  public long getTemplateId() {
    return templateId;
  }

  public void setTemplateId( long templateId ) {
    this.templateId = templateId;
  }

  // TYPE ID
  public long getTypeId() {
    return typeId;
  }

  public void setTypeId( long typeId ) {
    this.typeId = typeId;
  }

  // UPDATED BY USER ID
  public long getUpdatedByUserId() {
    return updatedByUserId;
  }

  public void setUpdatedByUserId( long updatedByUserId ) {
    this.updatedByUserId = updatedByUserId;
  }

  // DATE UPDATED ON
  public long getDateUpdatedOn() {
    return dateUpdatedOn;
  }

  public void setDateUpdatedOn( long dateUpdatedOn ) {
    this.dateUpdatedOn = dateUpdatedOn;
  }

  // TITLE
  public String getTitle() {
    return title;
  }

  public void setTitle( String title ) {
    this.title = title;
  }

  // REFERENCES
  public String getReferences() {
    return references;
  }

  public void setReferences( String references ) {
    this.references = references;
  }

  // PRECONDITIONS
  public String getPreconditions() {
    return preconditions;
  }

  public void setPreconditions( String preconditions ) {
    this.preconditions = preconditions;
  }

  // TEST STEPS
  public ArrayList<PTRTestStep> getTestSteps() {
    return testSteps;
  }

  public void setTestSteps( ArrayList<PTRTestStep> testSteps ) {
    this.testSteps = testSteps;
  }
}
