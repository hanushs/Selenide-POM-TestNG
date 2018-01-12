package com.hv.services.testrail.objects;

/**
 * A Pentaho TestRail test instance class used for storing the retrieved instance data returned by the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-tests">http://docs.gurock
 * .com/testrail-api2/reference-tests</a>
 */
public class PTRTestInstance {

  private long instanceId = -1;
  private String instanceTitle = "";
  private long relatedCaseId = -1;
  private long runId = -1;
  private long milestoneId = -1;
  private long assignedToId = -1;
  private long statusId = -1;
  private long typeId = -1;
  private long priorityId = -1;
  private String estimate = "1m";
  private String estimateForecast = "1m";
  private String references = "";

  // INSTANCE ID
  public long getInstanceId() {
    return instanceId;
  }

  public void setInstanceId( long instanceId ) {
    this.instanceId = instanceId;
  }

  // INSTANCE TITLE
  public String getInstanceTitle() {
    return instanceTitle;
  }

  public void setInstanceTitle( String instanceTitle ) {
    this.instanceTitle = instanceTitle;
  }

  // RELATED CASE ID
  public long getRelatedCaseId() {
    return relatedCaseId;
  }

  public void setRelatedCaseId( long relatedCaseId ) {
    this.relatedCaseId = relatedCaseId;
  }

  // RUN ID
  public long getRunId() {
    return runId;
  }

  public void setRunId( long runId ) {
    this.runId = runId;
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

  // STATUS ID
  public long getStatusId() {
    return statusId;
  }

  public void setStatusId( long statusId ) {
    this.statusId = statusId;
  }

  // TYPE ID
  public long getTypeId() {
    return typeId;
  }

  public void setTypeId( long typeId ) {
    this.typeId = typeId;
  }

  // PRIORITY ID
  public long getPriorityId() {
    return priorityId;
  }

  public void setPriorityId( long priorityId ) {
    this.priorityId = priorityId;
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

  // REFERENCES
  public String getReferences() {
    return references;
  }

  public void setReferences( String references ) {
    this.references = references;
  }
}
