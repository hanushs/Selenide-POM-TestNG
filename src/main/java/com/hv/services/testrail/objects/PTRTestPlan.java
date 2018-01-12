package com.hv.services.testrail.objects;

import java.util.ArrayList;

/**
 * A Pentaho TestRail test plan class used for storing the retrieved test plan data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-plans">http://docs.gurock
 * .com/testrail-api2/reference-plans</a>
 */
public class PTRTestPlan {

  private long testPlanId = -1;
  private long projectId = -1;
  private long milestoneId = -1;
  private long assignedToId = -1;
  private long createdByUserId = -1;
  private long createdOn = -1;
  private String description = "";
  private String planName = "";
  private String planURL = "";
  private boolean isCompleted = false;
  private long completedOn = 1;
  private ArrayList<PTRTestEntry> entries = new ArrayList<>();
  private long passedCount = -1;
  private long failedCount = -1;
  private long blockedCount = -1;
  private long retestCount = -1;
  private long untestedCount = -1;

  public PTRTestPlan() {

  }

  // TEST PLAN ID
  public long getTestPlanId() {
    return testPlanId;
  }

  public void setTestPlanId( long testPlanId ) {
    this.testPlanId = testPlanId;
  }

  // PROJECT ID
  public long getProjectId() {
    return projectId;
  }

  public void setProjectId( long projectId ) {
    this.projectId = projectId;
  }

  // MILESTONE ID
  public long getMilestoneId() {
    return milestoneId;
  }

  public void setMilestoneId( long milestoneId ) {
    this.milestoneId = milestoneId;
  }

  // ASSIGNED TO ID
  public long getAssignedToId() {
    return assignedToId;
  }

  public void setAssignedToId( long assignedToId ) {
    this.assignedToId = assignedToId;
  }

  // CREATED BY USER ID
  public long getCreatedByUserId() {
    return createdByUserId;
  }

  public void setCreatedByUserId( long createdByUserId ) {
    this.createdByUserId = createdByUserId;
  }

  // CREATED ON DATE
  public long getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn( long createdOn ) {
    this.createdOn = createdOn;
  }

  // DESCRIPTION
  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  // PLAN NAME
  public String getPlanName() {
    return planName;
  }

  public void setPlanName( String planName ) {
    this.planName = planName;
  }

  // PLAN URL
  public String getPlanURL() {
    return planURL;
  }

  public void setPlanURL( String planURL ) {
    this.planURL = planURL;
  }

  // IS COMPLETED
  public boolean isCompleted() {
    return isCompleted;
  }

  public void setIsCompleted( boolean isCompleted ) {
    this.isCompleted = isCompleted;
  }

  // COMPLETED ON DATE
  public long getCompletedOn() {
    return completedOn;
  }

  public void setCompletedOn( long completedOn ) {
    this.completedOn = completedOn;
  }

  // ENTRIES
  public ArrayList<PTRTestEntry> getEntries() {
    return entries;
  }

  public void setEntries( ArrayList<PTRTestEntry> entries ) {
    this.entries = entries;
  }

  // PASSED COUNT
  public long getPassedCount() {
    return passedCount;
  }

  public void setPassedCount( long passedCount ) {
    this.passedCount = passedCount;
  }

  // FAILED COUNT
  public long getFailedCount() {
    return failedCount;
  }

  public void setFailedCount( long failedCount ) {
    this.failedCount = failedCount;
  }

  // BLOCKED COUNT
  public long getBlockedCount() {
    return blockedCount;
  }

  public void setBlockedCount( long blockedCount ) {
    this.blockedCount = blockedCount;
  }

  // RETEST COUNT
  public long getRetestCount() {
    return retestCount;
  }

  public void setRetestCount( long retestCount ) {
    this.retestCount = retestCount;
  }

  // UNTESTED COUNT
  public long getUntestedCount() {
    return untestedCount;
  }

  public void setUntestedCount( long untestedCount ) {
    this.untestedCount = untestedCount;
  }
}
