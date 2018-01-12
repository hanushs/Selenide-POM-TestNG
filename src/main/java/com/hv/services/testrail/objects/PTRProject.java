package com.hv.services.testrail.objects;

/**
 * A Pentaho TestRail project class used for storing the retrieved project data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-projects">http://docs.gurock
 * .com/testrail-api2/reference-projects</a>
 */
public class PTRProject {

  private String description = "";
  private long completedOn = -1;
  private long projectId = -1;
  private boolean isProjectCompleted = false;
  private String projectName = "";
  private boolean isShowingDescription = false;
  private long suiteMode = -1;
  private String projectUrl = "";

  public PTRProject() {

  }

  // DESCRIPTION
  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  // DATE COMPLETED ON
  public long getCompletedOn() {
    return completedOn;
  }

  public void setCompletedOn( long completedOn ) {
    this.completedOn = completedOn;
  }

  // PROJECT ID
  public long getProjectId() {
    return projectId;
  }

  public void setProjectId( long projectId ) {
    this.projectId = projectId;
  }

  // IS PROJECT COMPLETED
  public boolean isProjectCompleted() {
    return isProjectCompleted;
  }

  public void setIsProjectCompleted( boolean projectCompleted ) {
    isProjectCompleted = projectCompleted;
  }

  // PROJECT NAME
  public String getProjectName() {
    return projectName;
  }

  public void setProjectName( String projectName ) {
    this.projectName = projectName;
  }

  // IS SHOWING DESCRIPTION
  public boolean isShowingDescription() {
    return isShowingDescription;
  }

  public void setIsShowingDescription( boolean showingDescription ) {
    isShowingDescription = showingDescription;
  }

  // SUITE MODE
  public long getSuiteMode() {
    return suiteMode;
  }

  public void setSuiteMode( long suiteMode ) {
    this.suiteMode = suiteMode;
  }

  // PROJECT URL
  public String getProjectUrl() {
    return projectUrl;
  }

  public void setProjectUrl( String projectUrl ) {
    this.projectUrl = projectUrl;
  }
}
