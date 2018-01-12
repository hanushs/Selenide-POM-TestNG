package com.hv.services.testrail.objects;

/**
 * A Pentaho TestRail sections class used for storing the retrieved section names from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-sections">http://docs.gurock
 * .com/testrail-api2/reference-sections</a>
 */
public class PTRSection {

  private long depth = -1;
  private String description = "";
  private long displayOrder = -1;
  private long sectionId = -1;
  private long parentSectionId = -1;
  private String name = "";
  private long suiteId = -1;

  // DEPTH IN HIERARCHY
  public long getDepth() {
    return depth;
  }

  public void setDepth( long depth ) {
    this.depth = depth;
  }

  // DESCRIPTION
  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  // DISPLAY ORDER
  public long getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder( long displayOrder ) {
    this.displayOrder = displayOrder;
  }

  // SECTION ID
  public long getSectionId() {
    return sectionId;
  }

  public void setSectionId( long sectionId ) {
    this.sectionId = sectionId;
  }

  // PARENT SECTION ID
  public long getParentSectionId() {
    return parentSectionId;
  }

  public void setParentSectionId( long parentSectionId ) {
    this.parentSectionId = parentSectionId;
  }

  // SECTION NAME
  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  // SUITE ID
  public long getSuiteId() {
    return suiteId;
  }

  public void setSuiteId( long suiteId ) {
    this.suiteId = suiteId;
  }

}
