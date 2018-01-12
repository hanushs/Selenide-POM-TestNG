package com.hv.services.testrail.objects;

/**
 * A Pentaho TestRail milestone class used for storing the retrieved milestone data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-milestones">http://docs.gurock
 * .com/testrail-api2/reference-milestones</a>
 */
public class PTRMilestone {

  private long milestoneId = -1;
  private String milestoneName = "";
  private String description = "";
  private long scheduledStartOn = -1;
  private long actualStartOn = -1;
  private long completedOn = -1;
  private long dueOn = -1;
  private boolean isMilestoneCompleted = false;
  private boolean isMilestoneStarted = false;
  private long projectId = -1;
  private String milestoneUrl = "";

  public PTRMilestone() {

  }

  // MILESTONE ID
  public long getMilestoneId() {
    return milestoneId;
  }

  public void setMilestoneId( long milestoneId ) {
    this.milestoneId = milestoneId;
  }

  // MILESTONE NAME
  public String getMilestoneName() {
    return milestoneName;
  }

  public void setMilestoneName( String milestoneName ) {
    this.milestoneName = milestoneName;
  }

  // MILESTONE DESCRIPTION
  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  // MILESTONE SCHEDULED START ON DATE
  public long getScheduledStartOn() {
    return scheduledStartOn;
  }

  public void setScheduledStartOn( long scheduledStartOn ) {
    this.scheduledStartOn = scheduledStartOn;
  }

  // MILESTONE ACTUAL START ON DATE
  public long getActualStartOn() {
    return actualStartOn;
  }

  public void setActualStartOn( long actualStartOn ) {
    this.actualStartOn = actualStartOn;
  }

  // MILESTONE COMLPETED ON DATE
  public long getCompletedOn() {
    return completedOn;
  }

  public void setCompletedOn( long completedOn ) {
    this.completedOn = completedOn;
  }

  // MILESTONE DUE ON DATE
  public long getDueOn() {
    return dueOn;
  }

  public void setDueOn( long dueOn ) {
    this.dueOn = dueOn;
  }

  // IS MILESTONE COMPLETED
  public boolean isMilestoneCompleted() {
    return isMilestoneCompleted;
  }

  public void setIsMilestoneCompleted( boolean milestoneCompleted ) {
    isMilestoneCompleted = milestoneCompleted;
  }

  // IS MILESTONE STARTED
  public boolean isMilestoneStarted() {
    return isMilestoneStarted;
  }

  public void setIsMilestoneStarted( boolean milestoneStarted ) {
    isMilestoneStarted = milestoneStarted;
  }

  // PROJECT ID
  public long getProjectId() {
    return projectId;
  }

  public void setProjectId( long projectId ) {
    this.projectId = projectId;
  }

  // MILESTONE URL
  public String getMilestoneUrl() {
    return milestoneUrl;
  }

  public void setMilestoneUrl( String milestoneUrl ) {
    this.milestoneUrl = milestoneUrl;
  }
}
