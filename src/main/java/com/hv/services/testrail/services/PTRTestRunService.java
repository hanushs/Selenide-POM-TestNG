package com.hv.services.testrail.services;

import com.hv.services.testrail.PTRClient;
import com.hv.services.testrail.objects.PTRConfig;
import com.hv.services.testrail.objects.PTRTestRun;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;

import java.util.ArrayList;

/**
 * A Pentaho TestRail test run class used for handling test run data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-runs">http://docs.gurock
 * .com/testrail-api2/reference-runs</a>
 */
public class PTRTestRunService extends PTRClient {

  private static final Logger LOGGER = Logger.getLogger( PTRTestRunService.class );

  /**
   * A method used to GET a single test run from the TestRail API.
   *
   * @param runId The run id of the run.
   * @return
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-runs#get_run">http://docs.gurock
   * .com/testrail-api2/reference-runs#get_run</a>
   */
  public PTRTestRun getRun( long runId ) {

    String getRequest = "get_run/" + runId;
    Object response = sendGet( getRequest );
    PTRTestRun testRun = new PTRTestRun();

    if ( response instanceof JSONObject ) {
      testRun = createPTRTestRun( (JSONObject) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return testRun;
  }

  /**
   * A package private method used to convert a JSONArray of test runs into an ArrayList of {@link PTRTestRun}
   * objects.
   *
   * @param jsonArray A JSONArray of test runs to convert.
   * @return An ArrayList of the test runs.
   */
  static ArrayList<PTRTestRun> createPTRTestRuns( JSONArray jsonArray ) {

    ArrayList<PTRTestRun> runs = new ArrayList<>();

    if ( jsonArray.size() > 0 ) {

      for ( Object obj : jsonArray ) {
        runs.add( createPTRTestRun( (JSONObject) obj ) );
      }

    } else {
      Assert.fail( "The given JSONArray does not any data for test runs. Returning empty array list." );
    }

    return runs;
  }

  /**
   * A helper method used to convert a single JSONObject into a single {@link PTRTestRun}.
   *
   * @param jsonObj The JSONObject to convert.
   * @return The converted test run.
   */
  private static PTRTestRun createPTRTestRun( JSONObject jsonObj ) {

    PTRTestRun run = new PTRTestRun();

    if ( jsonObj != null && !jsonObj.isEmpty() ) {

      long runId = -1;

      // Check if the test run has an id.
      if ( jsonObj.containsKey( "id" ) && jsonObj.get( "id" ) != null ) {
        run.setRunId( (long) jsonObj.get( "id" ) );
        runId = run.getRunId();
      } else {
        Assert.fail( "The test run provided does not have an id!" );
      }

      // Check if the test run has a user id assigned to it.
      if ( jsonObj.containsKey( "assignedto_id" ) && jsonObj.get( "assignedto_id" ) != null ) {
        run.setAssignedtoId( (long) jsonObj.get( "assignedto_id" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have a user assigned to it!" );
      }

      // Check if the test run has a description.
      if ( jsonObj.containsKey( "description" ) && jsonObj.get( "description" ) != null ) {
        run.setDescription( (String) jsonObj.get( "description" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have description set!" );
      }

      // Check if the test run has any configuration name associated with it.
      if ( jsonObj.containsKey( "config" ) && jsonObj.get( "config" ) != null ) {
        run.setConfigName( (String) jsonObj.get( "config" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not a configuration name!" );
      }

      // Check if the test run has any configuration ids.
      if ( jsonObj.containsKey( "config_ids" ) && jsonObj.get( "config_ids" ) != null ) {

        // If the run has an array of ids, parse them all and add to the run
        if ( jsonObj.get( "config_ids" ) instanceof JSONArray ) {
          ArrayList<PTRConfig> configs =
            getGlobalConfigService().convertConfigIds( (JSONArray) jsonObj.get( "config_ids" ) );
          configs.forEach( run::addConfiguration );
        }

        // If the run has just an object, it's a singular configuration.
        if ( jsonObj.get( "config_ids" ) instanceof JSONObject ) {
          run.addConfiguration( getGlobalConfigService().convertConfigId( (JSONObject) jsonObj.get( "config_ids" ) ) );
        }

      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have any configurations assigned to it!" );
      }

      // Check if the test run has a user id for who created it.
      if ( jsonObj.containsKey( "created_by" ) && jsonObj.get( "created_by" ) != null ) {
        run.setCreatedByUserId( (long) jsonObj.get( "created_by" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have a user assigned for who created it!" );
      }

      // Check if the test run has a date which is was created on.
      if ( jsonObj.containsKey( "created_on" ) && jsonObj.get( "created_on" ) != null ) {
        run.setCreatedOn( (long) jsonObj.get( "created_on" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have a date set for when it was created!" );
      }

      // Check if the test run has a date which is was completed on.
      if ( jsonObj.containsKey( "completed_on" ) && jsonObj.get( "completed_on" ) != null ) {
        run.setCompletedOn( (long) jsonObj.get( "completed_on" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have a date set for when it was completed!" );
      }

      // Check if the test run is including all test cases or not.
      if ( jsonObj.containsKey( "include_all" ) && jsonObj.get( "include_all" ) != null ) {
        run.setIncludeAll( (boolean) jsonObj.get( "include_all" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not indicate if it includes all test cases or not!" );
      }

      // Check if the test run has been completed or not.
      if ( jsonObj.containsKey( "is_completed" ) && jsonObj.get( "is_completed" ) != null ) {
        run.setIsCompleted( (boolean) jsonObj.get( "is_completed" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not indicate if it is complete or not!" );
      }

      // Check if the test run has a project id associated with it.
      if ( jsonObj.containsKey( "project_id" ) && jsonObj.get( "project_id" ) != null ) {
        run.setProjectId( (long) jsonObj.get( "project_id" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have a project id associated with it!" );
      }

      // Check if the test run has a milestone id associated with it.
      if ( jsonObj.containsKey( "milestone_id" ) && jsonObj.get( "milestone_id" ) != null ) {
        run.setMilestoneId( (long) jsonObj.get( "milestone_id" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have a milestone id associated with it!" );
      }

      // Check if the test run has a suite id associated with it.
      if ( jsonObj.containsKey( "suite_id" ) && jsonObj.get( "suite_id" ) != null ) {
        run.setSuiteId( (long) jsonObj.get( "suite_id" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have a suite id associated with it!" );
      }

      // Check if the test run has a plan id associated with it.
      if ( jsonObj.containsKey( "plan_id" ) && jsonObj.get( "plan_id" ) != null ) {
        run.setTestPlanId( (long) jsonObj.get( "plan_id" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have a plan id associated with it!" );
      }

      // Check if the test run has a name.
      if ( jsonObj.containsKey( "name" ) && jsonObj.get( "name" ) != null ) {
        run.setRunName( (String) jsonObj.get( "name" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have name set!" );
      }

      // Check if the test run has a URL.
      if ( jsonObj.containsKey( "url" ) && jsonObj.get( "url" ) != null ) {
        run.setRunURL( (String) jsonObj.get( "url" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have a URL set for it!" );
      }

      // Check if the test run has the passed count.
      if ( jsonObj.containsKey( "passed_count" ) && jsonObj.get( "passed_count" ) != null ) {
        run.setPassedCount( (long) jsonObj.get( "passed_count" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have a value for passed tests!" );
      }

      // Check if the test run has the failure count.
      if ( jsonObj.containsKey( "failed_count" ) && jsonObj.get( "failed_count" ) != null ) {
        run.setFailedCount( (long) jsonObj.get( "failed_count" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have a value for failed tests!" );
      }

      // Check if the test run has the blocked count.
      if ( jsonObj.containsKey( "blocked_count" ) && jsonObj.get( "blocked_count" ) != null ) {
        run.setBlockedCount( (long) jsonObj.get( "blocked_count" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have a value for blocked tests!" );
      }

      // Check if the test run has the retest count.
      if ( jsonObj.containsKey( "retest_count" ) && jsonObj.get( "retest_count" ) != null ) {
        run.setRetestCount( (long) jsonObj.get( "retest_count" ) );
      } else {
        LOGGER.debug( "Test run id '" + runId + "' does not have a value for tests to retest!" );
      }

    } else {
      Assert.fail( "The run object provided was empty! Could not convert to a PTRRun object!" );
    }

    return run;
  }

}
