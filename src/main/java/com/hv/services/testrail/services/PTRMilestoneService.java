package com.hv.services.testrail.services;

import com.hv.services.testrail.PTRClient;
import com.hv.services.testrail.objects.PTRMilestone;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;

import java.util.ArrayList;

/**
 * A Pentaho TestRail milestone class used for handling milestone data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-milestones">http://docs.gurock
 * .com/testrail-api2/reference-milestones</a>
 */
public class PTRMilestoneService extends PTRClient {

  private static final Logger LOGGER = Logger.getLogger( PTRMilestoneService.class );

  /**
   * A method used to retrieve the data for a single milestone through the TestRail API using the project's id
   * and then runs {@link #createPTRMilestone(JSONObject)} to create a milestone object from that information.
   *
   * @param milestoneId The id assigned to the desired milestone.
   * @return A milestone with information or an empty milestone if nothing is returned by the API.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-milestones#get_milestone">http://docs.gurock
   * .com/testrail-api2/reference-milestones#get_milestone</a>
   */
  public PTRMilestone getMilestone(long milestoneId ) {

    String getRequest = "get_milestone/" + milestoneId;

    Object response = sendGet( getRequest );
    PTRMilestone milestone = new PTRMilestone();

    if ( response instanceof JSONObject ) {
      milestone = createPTRMilestone( (JSONObject) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return milestone;
  }

  /**
   * A method used to retrieve the data for all milestones belonging to the given project regardless of started or
   * completed status.
   * <p>
   * If wanting to return completed milestones only, use {@link #getMilestonesCompleted(long, boolean)} instead.
   * <p>
   * If wanting to return started milestones only, use {@link #getMilestonesStarted(long, boolean)} instead.
   *
   * @param projectId The project's id from which to get all the desired milestones
   * @return An ArrayList of the milestones with information or an empty ArrayList if nothing is returned by the API.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-milestones#get_milestones">http://docs.gurock
   * .com/testrail-api2/reference-milestones#get_milestones</a>
   */
  public ArrayList<PTRMilestone> getMilestones( long projectId ) {

    ArrayList<PTRMilestone> milestones = new ArrayList<>();

    String getRequest = "get_milestones/" + projectId;

    Object response = sendGet( getRequest );

    if ( response instanceof JSONArray ) {
      milestones = createPTRMilestones( (JSONArray) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return milestones;
  }

  /**
   * A method used to retrieve the data for only started milestones belonging to a given project.
   * <p>
   * If wanting to retrieve the data for all milestones belonging to the given project regardless of started or
   * completed status, use {@link #getMilestones(long)} instead.
   *
   * @param projectId      The project's id from which to get all the desired milestones
   * @param isUpcomingOnly {@code true} if wanting upcoming milestones only, {@code false} for started milestones only.
   * @return An ArrayList of the milestones with information or an empty ArrayList if nothing is returned by the API.
   */
  public ArrayList<PTRMilestone> getMilestonesStarted( long projectId, boolean isUpcomingOnly ) {

    ArrayList<PTRMilestone> milestones = new ArrayList<>();

    int filter = 1;

    if ( isUpcomingOnly ) {
      filter = 0;
    }

    String getRequest = "get_milestones/" + projectId + "&is_started=" + filter;
    Object response = sendGet( getRequest );

    if ( response instanceof JSONArray ) {
      milestones = createPTRMilestones( (JSONArray) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return milestones;
  }

  /**
   * A method used to retrieve the data for only started milestones belonging to a given project.
   * * <p>
   * If wanting to retrieve the data for all milestones belonging to the given project regardless of started or
   * completed status, use {@link #getMilestones(long)} instead.
   *
   * @param projectId    The project's id from which to get all the desired milestones
   * @param isActiveOnly {@code true} if wanting active/upcoming milestones only, {@code false} for completed milestones
   *                     only.
   * @return An ArrayList of the milestones with information or an empty ArrayList if nothing is returned by the API.
   */
  public ArrayList<PTRMilestone> getMilestonesCompleted( long projectId, boolean isActiveOnly ) {

    ArrayList<PTRMilestone> milestones = new ArrayList<>();

    int filter = 1;

    if ( isActiveOnly ) {
      filter = 0;
    }

    String getRequest = "get_milestones/" + projectId + "&is_completed=" + filter;

    Object response = sendGet( getRequest );

    if ( response instanceof JSONArray ) {
      milestones = createPTRMilestones( (JSONArray) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return milestones;
  }

  /**
   * A helper method used to convert a JSONArray of milestones into an ArrayList of {@link PTRMilestone} objects.
   *
   * @param jsonArray A JSONArray of milestones to convert.
   * @return An ArrayList of the milestones.
   */
  private ArrayList<PTRMilestone> createPTRMilestones( JSONArray jsonArray ) {

    ArrayList<PTRMilestone> milestones = new ArrayList<>();

    if ( jsonArray.size() > 0 ) {

      for ( Object obj : jsonArray ) {
        milestones.add( createPTRMilestone( (JSONObject) obj ) );
      }

    } else {
      Assert.fail( "The given JSONArray does not any data for milestones. Returning empty array list." );
    }

    return milestones;
  }

  /**
   * A helper method used to convert a single JSONObject into a single {@link PTRMilestone}.
   *
   * @param jsonObj A JSONObject to convert.
   * @return The converted milestone.
   */
  private PTRMilestone createPTRMilestone( JSONObject jsonObj ) {

    PTRMilestone milestone = new PTRMilestone();

    if ( jsonObj != null && !jsonObj.isEmpty() ) {

      long milestoneId = -1;

      // Check if the milestone has an id number
      if ( jsonObj.containsKey( "id" ) && jsonObj.get( "id" ) != null ) {
        milestone.setMilestoneId( (long) jsonObj.get( "id" ) );
        milestoneId = milestone.getMilestoneId();
      } else {
        Assert.fail( "The milestone provided does not have an id!" );
      }

      // Check if the milestone has a date when the milestone was marked as completed
      if ( jsonObj.containsKey( "completed_on" ) && jsonObj.get( "completed_on" ) != null ) {
        milestone.setCompletedOn( (long) jsonObj.get( "completed_on" ) );
      } else {
        LOGGER.debug( "Milestone id '" + milestoneId + "' does not have a date set for when it was completed on or "
          + "it is still marked as an active milestone!" );
      }

      // Check if the milestone has a description
      if ( jsonObj.containsKey( "description" ) && jsonObj.get( "description" ) != null ) {
        milestone.setDescription( (String) jsonObj.get( "description" ) );
      } else {
        LOGGER.debug( "Milestone id '" + milestoneId + "' does not have a description!" );
      }

      // Check if the milestone has a date for which it is due by
      if ( jsonObj.containsKey( "due_on" ) && jsonObj.get( "due_on" ) != null ) {
        milestone.setDueOn( (long) jsonObj.get( "due_on" ) );
      } else {
        LOGGER.debug( "Milestone id '" + milestoneId + "' does not have a due date set!" );
      }

      // Check if the milestone is completed or not
      if ( jsonObj.containsKey( "is_completed" ) && jsonObj.get( "is_completed" ) != null ) {
        milestone.setIsMilestoneCompleted( (boolean) jsonObj.get( "is_completed" ) );
      } else {
        LOGGER.debug( "Milestone id '" + milestoneId + "' has no indication if it was completed or not!" );
      }

      // Check if the milestone has even been started or not
      if ( jsonObj.containsKey( "is_started" ) && jsonObj.get( "is_started" ) != null ) {
        milestone.setIsMilestoneStarted( (boolean) jsonObj.get( "is_started" ) );
      } else {
        LOGGER.debug( "Milestone id '" + milestoneId + "' has no indication if it was started or not!" );
      }

      // Check if the milestone has a name
      if ( jsonObj.containsKey( "name" ) && jsonObj.get( "name" ) != null ) {
        milestone.setMilestoneName( (String) jsonObj.get( "name" ) );
      } else {
        LOGGER.debug( "Milestone id '" + milestoneId + "' has no name set!" );
      }

      // Check if the milestone is has a project id associated with it
      if ( jsonObj.containsKey( "project_id" ) && jsonObj.get( "project_id" ) != null ) {
        milestone.setProjectId( (long) jsonObj.get( "project_id" ) );
      } else {
        LOGGER.debug( "Milestone id '" + milestoneId + "' has no project id associated with it!" );
      }

      // Check if the milestone has a scheduled start time
      if ( jsonObj.containsKey( "start_on" ) && jsonObj.get( "start_on" ) != null ) {
        milestone.setScheduledStartOn( (long) jsonObj.get( "start_on" ) );
      } else {
        LOGGER.debug( "Milestone id '" + milestoneId + "' has no scheduled start time!" );
      }

      // Check when the milestone was actually started
      if ( jsonObj.containsKey( "started_on" ) && jsonObj.get( "started_on" ) != null ) {
        milestone.setActualStartOn( (long) jsonObj.get( "started_on" ) );
      } else {
        LOGGER
          .debug( "Milestone id '" + milestoneId + "' has no indication as to whether or not it has been started!" );
      }

      // Check if the milestone has a URL associated with it
      if ( jsonObj.containsKey( "url" ) && jsonObj.get( "url" ) != null ) {
        milestone.setMilestoneUrl( (String) jsonObj.get( "url" ) );
      } else {
        LOGGER.debug( "Milestone id '" + milestoneId + "' has no indication as to if it was completed or not!" );
      }

    } else {
      Assert.fail( "The milestone object provided was empty! Could not convert to a PTRMilestone object!" );
    }

    return milestone;
  }
}
