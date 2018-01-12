package com.hv.services.testrail.services;

import com.hv.services.testrail.PTRClient;
import com.hv.services.testrail.objects.PTRTestInstance;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;

import java.util.ArrayList;

/**
 * A Pentaho TestRail test instance class used for handling test instance data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-tests">http://docs.gurock
 * .com/testrail-api2/reference-tests</a>
 */
public class PTRTestInstanceService extends PTRClient {

  private static final Logger LOGGER = Logger.getLogger( PTRTestInstanceService.class );

  /**
   * A method used to retrieve a single test instance based on the given instance id.
   *
   * @param testInstanceId The test instance id.
   * @return A {@link PTRTestInstance} object.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-tests#get_test">http://docs.gurock
   * .com/testrail-api2/reference-tests#get_test</a>
   */
  public PTRTestInstance getTestInstance( long testInstanceId ) {

    String getRequest = "get_test/" + testInstanceId;
    Object response = sendGet( getRequest );

    PTRTestInstance testInstance = new PTRTestInstance();

    if ( response instanceof JSONObject ) {
      testInstance = createPTRTestInstance( (JSONObject) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return testInstance;
  }

  /**
   *  A method used to retrieve all test instances from a specific test run.
   *
   * @param runId The run id to retrieve instances from.
   * @return An ArrayList of {@link PTRTestInstance} object.
   */
  public ArrayList<PTRTestInstance> getAllTestInstances( long runId ) {

    String getRequest = "get_tests/" + runId;
    Object response = sendGet( getRequest );

    ArrayList<PTRTestInstance> testInstances = new ArrayList<>();

    if ( response instanceof JSONArray ) {
      testInstances = createPTRTestInstances( (JSONArray) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return testInstances;
  }

  /**
   * A package private helper method used to convert a JSONArray of test instances into an ArrayList of {@link
   * PTRTestInstance} objects.
   *
   * @param jsonArray A JSONArray of test instances to convert.
   * @return An ArrayList of the test instances.
   */
  private ArrayList<PTRTestInstance> createPTRTestInstances( JSONArray jsonArray ) {

    ArrayList<PTRTestInstance> runs = new ArrayList<>();

    if ( jsonArray.size() > 0 ) {

      for ( Object obj : jsonArray ) {
        runs.add( createPTRTestInstance( (JSONObject) obj ) );
      }

    } else {
      Assert.fail( "The given JSONArray does not any data for test instances. Returning empty array list." );
    }

    return runs;
  }

  /**
   * A helper method used to convert a single JSONObject into a single {@link PTRTestInstance}.
   *
   * @param jsonObj The JSONObject to convert.
   * @return The converted test instance.
   */
  private PTRTestInstance createPTRTestInstance( JSONObject jsonObj ) {

    PTRTestInstance instance = new PTRTestInstance();

    if ( jsonObj != null && !jsonObj.isEmpty() ) {

      long instanceId = -1;

      // Check if the test instance has an id.
      if ( jsonObj.containsKey( "id" ) && jsonObj.get( "id" ) != null ) {
        instance.setInstanceId( (long) jsonObj.get( "id" ) );
        instanceId = instance.getInstanceId();
      } else {
        Assert.fail( "The test instance provided does not have an id!" );
      }

      // Check if the test instance has a title.
      if ( jsonObj.containsKey( "title" ) && jsonObj.get( "title" ) != null ) {
        instance.setInstanceTitle( (String) jsonObj.get( "title" ) );
      } else {
        LOGGER.debug( "Test instance '" + instanceId + "' provided does not have a title!" );
      }

      // Check if the test instance has a related case id.
      if ( jsonObj.containsKey( "case_id" ) && jsonObj.get( "case_id" ) != null ) {
        instance.setRelatedCaseId( (long) jsonObj.get( "case_id" ) );
      } else {
        LOGGER.debug( "Test instance '" + instanceId + "' provided does not have a related case id!" );
      }

      // Check if the test instance has a run id.
      if ( jsonObj.containsKey( "run_id" ) && jsonObj.get( "run_id" ) != null ) {
        instance.setRunId( (long) jsonObj.get( "run_id" ) );
      } else {
        LOGGER.debug( "Test instance '" + instanceId + "' provided does not have a run id!" );
      }

      // Check if the test instance has a milestone id.
      if ( jsonObj.containsKey( "milestone_id" ) && jsonObj.get( "milestone_id" ) != null ) {
        instance.setMilestoneId( (long) jsonObj.get( "milestone_id" ) );
      } else {
        LOGGER.debug( "Test instance '" + instanceId + "' provided does not have a milestone id!" );
      }

      // Check if the test instance has a status id.
      if ( jsonObj.containsKey( "status_id" ) && jsonObj.get( "status_id" ) != null ) {
        instance.setStatusId( (long) jsonObj.get( "status_id" ) );
      } else {
        LOGGER.debug( "Test instance '" + instanceId + "' provided does not have a status id!" );
      }

      // Check if the test instance has a type id.
      if ( jsonObj.containsKey( "type_id" ) && jsonObj.get( "type_id" ) != null ) {
        instance.setTypeId( (long) jsonObj.get( "type_id" ) );
      } else {
        LOGGER.debug( "Test instance '" + instanceId + "' provided does not have a type id!" );
      }

      // Check if the test instance has a priority id.
      if ( jsonObj.containsKey( "priority_id" ) && jsonObj.get( "priority_id" ) != null ) {
        instance.setPriorityId( (long) jsonObj.get( "priority_id" ) );
      } else {
        LOGGER.debug( "Test instance '" + instanceId + "' provided does not have a priority id!" );
      }

      // Check if the test instance has an id of the user it is assigned to.
      if ( jsonObj.containsKey( "assignedto_id" ) && jsonObj.get( "assignedto_id" ) != null ) {
        instance.setAssignedToId( (long) jsonObj.get( "assignedto_id" ) );
      } else {
        LOGGER.debug( "Test instance '" + instanceId + "' provided does not have a user id assigned to it!" );
      }

      // Check if the test instance has an estimate.
      if ( jsonObj.containsKey( "estimate" ) && jsonObj.get( "estimate" ) != null ) {
        instance.setEstimate( (String) jsonObj.get( "estimate" ) );
      } else {
        LOGGER.debug( "Test instance '" + instanceId + "' provided does not have an estimate!" );
      }

      // Check if the test instance has an estimated forecast.
      if ( jsonObj.containsKey( "estimate_forecast" ) && jsonObj.get( "estimate_forecast" ) != null ) {
        instance.setEstimateForecast( (String) jsonObj.get( "estimate_forecast" ) );
      } else {
        LOGGER.debug( "Test instance '" + instanceId + "' provided does not have an estimated forecast!" );
      }

      // Check if the test instance has any references.
      if ( jsonObj.containsKey( "refs" ) && jsonObj.get( "refs" ) != null ) {
        instance.setReferences( (String) jsonObj.get( "refs" ) );
      } else {
        LOGGER.debug( "Test instance '" + instanceId + "' provided does not have any references!" );
      }

    } else {
      Assert.fail( "The test instance object provided was empty! Could not convert to a PTRTestInstance object!" );
    }

    return instance;
  }

}
