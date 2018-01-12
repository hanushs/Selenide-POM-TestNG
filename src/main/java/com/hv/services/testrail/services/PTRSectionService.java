package com.hv.services.testrail.services;

import com.hv.services.testrail.PTRClient;
import com.hv.services.testrail.objects.PTRSection;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.Assert;

/**
 * A Pentaho TestRail sections class used for handling section names from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-sections">http://docs.gurock
 * .com/testrail-api2/reference-sections</a>
 */
public class PTRSectionService extends PTRClient {

  private static final Logger LOGGER = Logger.getLogger( PTRSectionService.class );

  /**
   * A method used to retrieve the information for a specific section id through the TestRail API and then
   * runs {@link #createPTRSection(JSONObject)} to create a section object from that information.
   *
   * @param sectionId The id assigned to the section.
   * @return A section with details.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-sections#get_section">http://docs.gurock
   * .com/testrail-api2/reference-sections#get_section</a>
   */
  public PTRSection getSection( long sectionId ) {

    String getRequest = "get_section/" + sectionId;
    Object response = sendGet( getRequest );

    PTRSection section = new PTRSection();

    if ( response instanceof JSONObject ) {
      section = createPTRSection( (JSONObject) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return section;
  }

  /**
   * A helper method used to convert a JSONObject over to a {@link PTRSection} object.
   *
   * @param jsonObj The JSONOBject with section information
   * @return A {@link PTRSection} object.
   */
  private PTRSection createPTRSection( JSONObject jsonObj ) {

    PTRSection section = new PTRSection();

    if ( jsonObj != null && !jsonObj.isEmpty() ) {

      long sectionId = -1;

      // Check if the section has an id number
      if ( jsonObj.containsKey( "id" ) && jsonObj.get( "id" ) != null ) {
        section.setSectionId( (long) jsonObj.get( "id" ) );
        sectionId = section.getSectionId();
      } else {
        Assert.fail( "The section id is somehow missing from the response object!" );
      }

      // Check if the section has a name
      if ( jsonObj.containsKey( "name" ) && jsonObj.get( "name" ) != null ) {
        section.setName( (String) jsonObj.get( "name" ) );
      } else {
        Assert.fail( "The section id '" + sectionId + "' is missing a name!" );
      }

      // Check if the section has a depth value in the hierarchy
      if ( jsonObj.containsKey( "depth" ) && jsonObj.get( "depth" ) != null ) {
        section.setDepth( (long) jsonObj.get( "depth" ) );
      } else {
        LOGGER.debug( "The section id '" + sectionId + "' does not have a depth value associated with the hierarchy!" );
      }

      // Check if the section has a parent section it belongs to.
      if ( jsonObj.containsKey( "parent_id" ) && jsonObj.get( "parent_id" ) != null ) {
        section.setParentSectionId( (long) jsonObj.get( "parent_id" ) );
      } else {
        LOGGER.debug( "The section id '" + sectionId + "' does not have a parent in the section hierarchy!" );
      }

      // Check if the section has a particular display order on the section hierarchy
      if ( jsonObj.containsKey( "display_order" ) && jsonObj.get( "display_order" ) != null ) {
        section.setDisplayOrder( (long) jsonObj.get( "display_order" ) );
      } else {
        LOGGER.debug( "The section id '" + sectionId + "' does not have a display order in the section hierarchy!" );
      }

      // Check if the section has a description
      if ( jsonObj.containsKey( "description" ) && jsonObj.get( "description" ) != null ) {
        section.setDescription( (String) jsonObj.get( "description" ) );
      } else {
        LOGGER.debug( "The section id '" + sectionId + "' does not have description!" );
      }

      // Check if the section has a suite id it is associated with
      if ( jsonObj.containsKey( "suite_id" ) && jsonObj.get( "suite_id" ) != null ) {
        section.setSuiteId( (long) jsonObj.get( "suite_id" ) );
      } else {
        LOGGER.debug( "The section id '" + sectionId + "' does not have suite id associated with it!" );
      }

    } else {
      Assert.fail( "The section object provided was empty! Could not convert to a PTRSection object!" );
    }

    return section;
  }

}
