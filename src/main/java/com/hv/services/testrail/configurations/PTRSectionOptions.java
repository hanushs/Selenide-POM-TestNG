package com.hv.services.testrail.configurations;

/**
 * An enum used to help identify the various Pentaho related TestRail suite sections.
 */
public enum PTRSectionOptions {

  PENTAHO_DATA_INTEGRATION( "PDI", "Pentaho Data Integration" ),
  PENTAHO_DATA_EXPLORARTION_TOOL( "DET", "Pentaho Data Exploration Tool" ),
  PENTAHO_BIG_DATA( "BAD", "Pentaho Big Data" ),
  PENTAHO_INSTALLATION( "INST", "Pentaho Installer" ),
  PENTAHO_USER_CONSOLE( "PUC", "Pentaho User Console" ),
  PENTAHO_ANALYZER( "PAZ", "Pentaho Analyzer" ),
  PENTAHO_INTERACTIVE_REPORT( "PIR", "Pentaho Interactive Report" ),
  PENTAHO_DASHBOARD_DESIGNER( "PDD", "Pentaho Dashboard Designer" ),
  PENTAHO_REPORT_DESIGNER( "PRD", "Pentaho Report Designer" ),
  PENTAHO_METADATA_EDITOR( "PME", "Pentaho Metadata Editor" ),
  PENTAHO_AGGREGATION_DESIGNER( "PAD", "Pentaho Aggregation Designer" ),
  PENTAHO_SCHEMA_WORKBENCH( "PSW", "Pentaho Schema Workbench" );

  private String acronym;
  private String name;

  PTRSectionOptions( String acronym, String name ) {
    this.acronym = acronym;
    this.name = name;
  }

  public String getAcronym() {
    return this.acronym;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.acronym + " - " + this.name;
  }

}
