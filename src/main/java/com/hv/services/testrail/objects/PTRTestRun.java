package com.hv.services.testrail.objects;

import com.hv.services.testrail.services.PTRTestInstanceService;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A Pentaho TestRail test run class used for storing the retrieved test run data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-runs">http://docs.gurock
 * .com/testrail-api2/reference-runs</a>
 */
public class PTRTestRun {

  private long runId = -1;
  private long assignedToId = -1;
  private String fullName = "";
  private String description = "";
  private ArrayList<PTRTestInstance> testInstances = new ArrayList<>();
  private ArrayList<PTRConfig> configurations = new ArrayList<>();
  private String configName = "";
  private long createdByUserId = -1;
  private long createdOn = -1;
  private long completedOn = -1;
  private boolean includeAll = true;
  private boolean isCompleted = false;
  private long projectId = -1;
  private long milestoneId = -1;
  private long suiteId = -1;
  private long testPlanId = -1;
  private String runName = "";
  private String runURL = "";
  private long passedCount = -1;
  private long failedCount = -1;
  private long blockedCount = -1;
  private long retestCount = -1;

  public PTRTestRun() {

  }

  // RUN ID
  public long getRunId() {
    return runId;
  }

  public void setRunId( long runId ) {
    this.runId = runId;
  }

  // ASSIGNED TO ID
  public long getAssignedtoId() {
    return assignedToId;
  }

  public void setAssignedtoId( long assignedToId ) {
    this.assignedToId = assignedToId;
  }

  //FULL NAME
  public String getFullName() {
    return runName + " (" + configName + ")";
  }

  // DESCRIPTION
  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  // CONFIGURATION NAME
  public String getConfigName() {
    return configName;
  }

  public void setConfigName( String configName ) {
    this.configName = configName;
  }

  // CONFIGURATION IDs
  public ArrayList<PTRConfig> getConfigurations() {
    return configurations;
  }

  public void addConfiguration( PTRConfig... configuration ) {
    this.configurations.addAll( Arrays.asList( configuration ) );
  }

  public ArrayList<Long> getConfigurationIds() {
    ArrayList<Long> configIds = new ArrayList<>();
    configurations.forEach( config -> configIds.add( config.getConfigId() ) );
    return configIds;
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

  // COMPLETED ON
  public long getCompletedOn() {
    return completedOn;
  }

  public void setCompletedOn( long completedOn ) {
    this.completedOn = completedOn;
  }

  // INCLUDE ALL TEST CASES
  public boolean isIncludeAll() {
    return includeAll;
  }

  public void setIncludeAll( boolean includeAll ) {
    this.includeAll = includeAll;
  }

  // IS COMPLETED
  public boolean isCompleted() {
    return isCompleted;
  }

  public void setIsCompleted( boolean completed ) {
    isCompleted = completed;
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

  // SUITE ID
  public long getSuiteId() {
    return suiteId;
  }

  public void setSuiteId( long suiteId ) {
    this.suiteId = suiteId;
  }

  // TEST PLAN ID
  public long getTestPlanId() {
    return testPlanId;
  }

  public void setTestPlanId( long testPlanId ) {
    this.testPlanId = testPlanId;
  }

  // RUN NAME
  public String getRunName() {
    return runName;
  }

  public void setRunName( String runName ) {
    this.runName = runName;
  }

  // RUN URL
  public String getRunURL() {
    return runURL;
  }

  public void setRunURL( String runURL ) {
    this.runURL = runURL;
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

  // TEST CASES
  public void setTestInstances() {
    this.testInstances = new PTRTestInstanceService().getAllTestInstances( runId );
  }

  public ArrayList<PTRTestInstance> getTestInstances() {
    return testInstances;
  }
}
