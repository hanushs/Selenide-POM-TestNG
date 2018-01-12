package com.hv.services.testrail.services;

import com.hv.services.testrail.PTRClient;
import com.hv.services.testrail.objects.PTRConfig;
import com.hv.services.testrail.objects.PTRTestEntry;
import com.hv.services.testrail.objects.PTRTestInstance;
import com.hv.services.testrail.objects.PTRTestPlan;
import com.hv.services.testrail.configurations.PTRConfigOptions;
import com.hv.services.testrail.objects.PTRTestRun;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A Pentaho TestRail test plan class used for handling test plan data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-plans">http://docs.gurock
 * .com/testrail-api2/reference-plans</a>
 */
public class PTRTestPlanService extends PTRClient {

  private static final Logger LOGGER = Logger.getLogger( PTRTestPlanService.class );

  /**
   * A method used to retrieve a single test plan from the TestRail API.
   *
   * @param testPlanId The id of the test plan.
   * @return A test plan with information or an empty test plan if nothing is returned by the API.
   */
  public PTRTestPlan getPlan(long testPlanId ) {

    PTRTestPlan testPlan = new PTRTestPlan();

    String getRequest = "get_plan/" + testPlanId;
    Object response = sendGet( getRequest );

    if ( response instanceof JSONObject ) {
      testPlan = createPTRTestPlan( (JSONObject) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return testPlan;
  }

  /**
   * A method used to retrieve the data of all test plans belonging to a given project.
   * <p>
   * The test plans returned by this method do <b>NOT</b> include any test run entries in the response.
   * <p>
   * If it is desired to return a test plan with its test run entries, use {@link #getPlan(long)}
   *
   * @param projectId The id of the project the test plans are in.
   * @return An ArrayList of the test plans in the project, without filters applied.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-plans#get_plans">http://docs.gurock
   * .com/testrail-api2/reference-plans#get_plans</a>
   */
  public ArrayList<PTRTestPlan> getPlans( long projectId ) {

    return getPlans( projectId, "" );
  }

  /**
   * A method used to retrieve the data of test plans created by specific users belonging to a given project. This
   * method retrieves only active or completed test plans.
   * <p>
   * The test plans returned by this method do <b>NOT</b> include any test run entries in the response.
   * <p>
   * If it is desired to return a test plan with its test run entries, use {@link #getPlan(long)}
   * <p>
   *
   * @param projectId       The project id the test plans belong to.
   * @param isCompletedOnly True to return completed plans only, false to return active plans only.
   * @param userId          The id(s) of the user(s) who created the plan(s) to be returned.
   * @return An ArrayList of the test plans in the project belonging to the given user id(s).
   */
  public ArrayList<PTRTestPlan> getPlansByUsers( long projectId, boolean isCompletedOnly, long... userId ) {

    String filters = "";

    if ( isCompletedOnly ) {
      filters += "&is_completed=1";
    } else {
      filters += "&is_completed=0";
    }

    if ( userId.length == 1 ) {

      filters += "&created_by=" + userId[ 0 ];

    } else if ( userId.length > 1 ) {

      filters += "&created_by=";

      for ( int i = 0; i < userId.length; i++ ) {
        if ( i != ( userId.length - 1 ) ) {
          filters += userId[ i ] + ",";
        } else {
          filters += userId[ i ];
        }
      }

    } else {
      LOGGER.warn( "The URL filter to retrieve test plans created by specific user ids was empty "
        + "or contains a number less than 1. Excluding the filter from the request." );
    }

    return getPlans( projectId, filters );
  }

  /**
   * A method used to retrieve the data of test plans created in a given project between a specific date range.
   * <p>
   * The test plans returned by this method do <b>NOT</b> include any test run entries in the response.
   * <p>
   * If it is desired to return a test plan with its test run entries, use {@link #getPlan(long)}
   *
   * @param projectId       The project id the test plans belong to.
   * @param afterDate       The starting date for which to return plans created after this date.
   * @param beforeDate      The ending date for which to return plans created before this date.
   * @param isCompletedOnly True to return completed plans only, false to return active plans only.
   * @return An ArrayList of the test plans in the project between the given date rate.
   */
  public ArrayList<PTRTestPlan> getPlansBetweenDates( long projectId, boolean isCompletedOnly, LocalDate afterDate,
                                                      LocalDate beforeDate ) {

    String filters = "";

    if ( isCompletedOnly ) {
      filters += "&is_completed=1";
    } else {
      filters += "&is_completed=0";
    }

    if ( afterDate.isEqual( getTestRailsInceptionDate() ) || afterDate.isAfter( getTestRailsInceptionDate() ) ) {
      filters += "&created_after=" + ( afterDate.toEpochDay() * 86400L );
    } else {
      LOGGER.warn( "The URL filter for the 'created_after' date cannot be earlier then the TestRail inception date."
        + " Excluding the filter from the request." );
    }

    if ( beforeDate.isAfter( getTestRailsInceptionDate() ) ) {
      filters += "&created_before=" + ( beforeDate.toEpochDay() * 86400L );
    } else {
      LOGGER.warn( "The URL filter for the 'created_before' date cannot be earlier then the TestRail inception date."
        + " Excluding the filter from the request." );
    }

    return getPlans( projectId, filters );
  }

  /**
   * A method used to retrieve the data of test plans created in a given project under specified milestones.
   *
   * @param projectId       The project id the test plans belong to.
   * @param isCompletedOnly True to return completed plans only, false to return active plans only.
   * @param milestoneId     The id(s) of the milestone(s) that the plan(s) are belonging to.
   * @return An ArrayList of the test plans in the project belonging to the given milestone id(s).
   */
  public ArrayList<PTRTestPlan> getPlansByMilestones( long projectId, boolean isCompletedOnly, long... milestoneId ) {

    String filters = "";

    if ( isCompletedOnly ) {
      filters += "&is_completed=1";
    } else {
      filters += "&is_completed=0";
    }

    if ( milestoneId.length == 1 ) {

      filters += "&milestone_id=" + milestoneId[ 0 ];

    } else if ( milestoneId.length > 1 ) {

      filters += "&milestone_id=";

      for ( int i = 0; i < milestoneId.length; i++ ) {
        if ( i != ( milestoneId.length - 1 ) ) {
          filters += milestoneId[ i ] + ",";
        } else {
          filters += milestoneId[ i ];
        }
      }

    } else {
      LOGGER.warn( "The URL filter to retrieve test plans apart of the specified milestone id(s) is empty or contains "
        + "a number less than 1. Excluding the filter from the request." );
    }

    return getPlans( projectId, filters );
  }

  /**
   * A method used to retrieve the data of all test plans belonging to a given project.
   * <p>
   * The test plans returned by this method do <b>NOT</b> include any test run entries in the response.
   * <p>
   * If it is desired to return a test plan with its test run entries, use {@link #getPlan(long)}
   *
   * @param projectId The project id the test plans belong to.
   * @param filters   The URL filters to apply during retrieval.
   * @return An ArrayList of the test plans in the project, with or without filters applied.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-plans#get_plans">http://docs.gurock
   * .com/testrail-api2/reference-plans#get_plans</a>
   */
  private ArrayList<PTRTestPlan> getPlans( long projectId, String filters ) {

    ArrayList<PTRTestPlan> testPlans = new ArrayList<>();

    String getRequest;
    Object response;

    if ( filters.equals( "" ) ) {
      getRequest = "get_plans/" + projectId;
    } else {
      getRequest = "get_plans/" + projectId + filters;
    }

    response = sendGet( getRequest );

    if ( response instanceof JSONArray ) {
      testPlans = createPTRTestPlans( (JSONArray) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return testPlans;
  }

  /**
   * A method used to add new test plans to a given project and milestone.
   *
   * @param projectId    The project id to add the test plan to.
   * @param milestoneId  The milestone to add the test plan to.
   * @param testPlanName The name of the test plan.
   * @param description  The description of the test plan.
   * @return If successful, returns the new test plan as if {@link #getPlan(long)} was called.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-plans#add_plan">http://docs.gurock
   * .com/testrail-api2/reference-plans#add_plan</a>
   */
  public PTRTestPlan addPlan( long projectId, long milestoneId, String testPlanName, String description ) {

    PTRTestPlan testPlan = new PTRTestPlan();

    JSONObject payload = new JSONObject();
    payload.put( "name", testPlanName );
    payload.put( "description", description );
    payload.put( "milestone_id", milestoneId );

    String postRequest = "add_plan/" + projectId;
    Object response = sendPost( postRequest, payload );

    if ( response instanceof JSONObject ) {
      testPlan = createPTRTestPlan( (JSONObject) response );
    } else {
      Assert.fail( getNotAJsonError( postRequest, response.toString() ) );
    }

    return testPlan;
  }

  /**
   * A method used to add a new test plan entry to a given existing test plan.
   * <p>
   * If a test plan requires a new test entry to be added via this method, the resulting test entry that will be added
   * to that test plan if flagged to group test runs will contain all possible supported configurations as runs for that
   * test entry, excluding any of those ignored in the _testrail.properties file.
   * <p>
   * As of 6/23/2017, the TestRail API does not support updating any existing test entry in an existing test plan with a
   * new run configuration, thus the required initial creation with all combinations. If the
   * "testrail_testplan_entry_group_runs" property is set to false, a single test entry will be created that contains a
   * single run configuration. Any subsequent tests that require a new run configuration will create a new test entry.
   *
   * @param planId         The test plan id for which to add a new entry
   * @param startingCaseId The id of the test case that will be instantiated in the test entry. A test entry MUST
   *                       contain at least ONE test case.
   * @param entry          A {@link PTRTestEntry} object.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-plans#add_plan_entry">http://docs.gurock
   * .com/testrail-api2/reference-plans#add_plan_entry</a>
   */
  public PTRTestEntry addPlanEntry( long planId, long startingCaseId, PTRTestEntry entry ) {

    JSONObject payload = buildPlanEntryPayload( startingCaseId, entry, null );

    String postRequest = "add_plan_entry/" + planId;
    Object response = sendPost( postRequest, payload );

    if ( response instanceof JSONObject ) {
      entry = createPTRTestEntry( (JSONObject) response );
    } else {
      Assert.fail( getNotAJsonError( postRequest, response.toString() ) );
    }

    return entry;
  }

  /**
   * A method used to update an existing test plan entry in a given existing test plan with any case ids being tested
   * that are missing from any existing test run configurations.
   * <p>
   * This method is primarily used to update a test entry's list of selected test cases that the run configurations will
   * inherit from. Each update performed needs to rebuild the list of existing case ids while adding any new case id.
   * <p>
   * As of 6/23/2017, the TestRail API does not support updating individual test runs that are INSIDE an existing test
   * plan with new case ids, thus the requirement to update an entire test entry so that any child test runs will
   * automatically inherit from.
   * <p>
   * The TestRail API response below is what a user will receive if the do attempt to update a run inside a test plan:
   * <p>
   * <code>com.test.APIException: TestRail API returned HTTP 403("This operation is not allowed. The test run belongs to
   * a test plan and cannot be edited independently."</code>
   *
   * @param planId        The test plan id that the test entry is inside.
   * @param caseIdToAdd   The new case id that the test entry will add.
   * @param entryToUpdate The specific test entry to update.
   * @param foundTestRun  The test run that is being updated. (Necessary in order to retrieve all other case ids from
   *                      test instances)
   * @return
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-plans#update_plan_entry">http://docs.gurock
   * .com/testrail-api2/reference-plans#update_plan_entry</a>
   * @see <a href="https://discuss.gurock.com/t/how-to-resolve-com-test-apiexception-testrail-api-returned-http-403
   * -this-operation-is-not-allowed-the-test-run-belongs-to-a-test-plan-and-cannot-be-edited-independently/3866/2
   * ">Gurock Team member explanation and issue.</a>
   */
  public PTRTestEntry updatePlanEntry( long planId, long caseIdToAdd, PTRTestEntry entryToUpdate,
                                       PTRTestRun foundTestRun ) {

    JSONObject payload = buildPlanEntryPayload( caseIdToAdd, entryToUpdate, foundTestRun );

    String postRequest = "update_plan_entry/" + planId + "/" + entryToUpdate.getUniqueId();
    Object response = sendPost( postRequest, payload );

    if ( response instanceof JSONObject ) {
      entryToUpdate = createPTRTestEntry( (JSONObject) response );
    } else {
      Assert.fail( getNotAJsonError( postRequest, response.toString() ) );
    }

    return entryToUpdate;
  }


  /**
   * A method used to build the JSONObject payload that will be sent via POST to the TestRail API.
   *
   * @param caseId  The case id being added.
   * @param entry   The specific test entry being updated or added.
   * @param testRun The test run containing any test instances necessary to rebuild the case id list from.
   * @return A JSONObject formatted to TestRail API requirements.
   */
  private JSONObject buildPlanEntryPayload( long caseId, PTRTestEntry entry, PTRTestRun testRun ) {

    JSONObject payload = new JSONObject();

    payload.put( "suite_id", entry.getSuiteId() );
    payload.put( "name", entry.getName() );
    payload.put( "description", entry.getDescription() );
    payload.put( "include_all", entry.isIncludeAll() );

    payload.put( "case_ids", buildCaseIdsPayload( caseId, testRun ) );

    PTRConfigService configService = getGlobalConfigService();
    JSONArray allConfigIds = new JSONArray();
    JSONArray allRuns = new JSONArray();

    // Ben Freed - As of 6/23/2017
    // If run configurations should be grouped per entry, create all possible combinations allowed because after
    // initial test entry creation, new test run configurations can NOT be dynamically added to that test entry.
    // @see - https://discuss.gurock.com/t/adding-a-new-test-run-configuration-to-an-existing-entry/1366
    if ( isGroupTestRunsAllowed() ) {

      // Get all known locale configurations.
      ArrayList<PTRConfig> localeConfigs =
        configService.getConfigType( PTRConfigOptions.Types.LOCALE, true );
      localeConfigs.forEach( ( localeConfig ) -> allConfigIds.add( localeConfig.getConfigId() ) );

      // Create each unique combination of run configurations and add the config id to the combined test entry array.
      // If there is an active browser, we need to perform a different grouping method than a locale only one.
      // TODO: TEMP SETUP ONLY - Will need a rewrite in permutation logic to accommodate any future configuration types.
      if ( configService.getActiveBrowserConfiguration().getConfigId() > 0 ) {

        ArrayList<PTRConfig> browserConfigs =
          configService.getConfigType( PTRConfigOptions.Types.WEB_BROWSER, true );
        browserConfigs.forEach( ( browserConfig ) -> allConfigIds.add( browserConfig.getConfigId() ) );

        for ( PTRConfig browserConfig : browserConfigs ) {
          for ( PTRConfig localeConfig : localeConfigs ) {

            JSONArray configIds = new JSONArray();
            JSONObject runObj = new JSONObject();

            configIds.add( browserConfig.getConfigId() );
            configIds.add( localeConfig.getConfigId() );

            runObj.put( "config_ids", configIds );

            allRuns.add( runObj );

          }
        }
      } else {
        for ( PTRConfig localeConfig : localeConfigs ) {

          JSONArray configIds = new JSONArray();
          JSONObject runObj = new JSONObject();

          configIds.add( localeConfig.getConfigId() );

          runObj.put( "config_ids", configIds );

          allRuns.add( runObj );
        }
      }

      // A test entry must contain a combined array of configuration ids for the test run(s) it is responsible for.
      payload.put( "config_ids", allConfigIds );

    } else {
      // Get just the active configurations and add them to the payload.
      allConfigIds.addAll( configService.getActiveTestConfigurationIds() );

      payload.put( "config_ids", allConfigIds );

      JSONObject runObj = new JSONObject();
      runObj.put( "config_ids", allConfigIds );

      allRuns.add( runObj );
    }

    payload.put( "runs", allRuns );

    return payload;
  }

  /**
   * A helper method used to rebuild the case id's of a test entry and include the new case id.
   *
   * @param caseId  The new case id.
   * @param testRun The test run containing test instances with related case ids.
   * @return A JSONArray formatted to fit in the TestRail specified JSONObject that will be sent via POST.
   */
  private JSONArray buildCaseIdsPayload( long caseId, PTRTestRun testRun ) {

    JSONArray allCaseIds = new JSONArray();
    if ( testRun != null ) {
      for ( PTRTestInstance testInstance : testRun.getTestInstances() ) {
        if ( !allCaseIds.contains( testInstance.getRelatedCaseId() ) ) {
          allCaseIds.add( testInstance.getRelatedCaseId() );
        }
      }
    }

    if ( !allCaseIds.contains( caseId ) ) {
      allCaseIds.add( caseId );
    }

    return allCaseIds;
  }

  /**
   * A helper method used to convert a JSONArray of test plans into an ArrayList of {@link PTRTestPlan} objects.
   *
   * @param jsonArray A JSONArray of test plans to convert.
   * @return An ArrayList of the test plans.
   */
  private ArrayList<PTRTestPlan> createPTRTestPlans( JSONArray jsonArray ) {

    ArrayList<PTRTestPlan> plans = new ArrayList<>();

    if ( jsonArray.size() > 0 ) {

      for ( Object obj : jsonArray ) {
        plans.add( createPTRTestPlan( (JSONObject) obj ) );
      }

    } else {
      Assert.fail( "The given JSONArray does not have any data for test plans. Returning empty array list." );
    }

    return plans;
  }

  /**
   * A helper method used to convert a single Object into a single {@link PTRTestPlan}.
   *
   * @param jsonObj The object to convert.
   * @return The converted test plan.
   */
  private PTRTestPlan createPTRTestPlan( JSONObject jsonObj ) {

    PTRTestPlan testPlan = new PTRTestPlan();

    if ( jsonObj != null && !jsonObj.isEmpty() ) {

      long planId = -1;

      // Check if the test plan has an id.
      if ( jsonObj.containsKey( "id" ) && jsonObj.get( "id" ) != null ) {
        testPlan.setTestPlanId( (long) jsonObj.get( "id" ) );
        planId = testPlan.getTestPlanId();
      } else {
        Assert.fail( "The test plan provided does not have an id associated with it!" );
      }

      // Check if the test plan has a user id assigned to it.
      if ( jsonObj.containsKey( "assignedto_id" ) && jsonObj.get( "assignedto_id" ) != null ) {
        testPlan.setAssignedToId( (long) jsonObj.get( "assignedto_id" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have a user assigned to it!" );
      }

      // Check if the test plan has any tests marked as blocked.
      if ( jsonObj.containsKey( "blocked_count" ) && jsonObj.get( "blocked_count" ) != null ) {
        testPlan.setBlockedCount( (long) jsonObj.get( "blocked_count" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have any key for blocked test cases!" );
      }

      // Check if the test plan has a timestamp for when it was completed on.
      if ( jsonObj.containsKey( "completed_on" ) && jsonObj.get( "completed_on" ) != null ) {
        testPlan.setCompletedOn( (long) jsonObj.get( "completed_on" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have a date set for when it was completed on or "
          + "it is still marked as an active test!" );
      }

      // Check if the test plan has a user id for who created it.
      if ( jsonObj.containsKey( "created_by" ) && jsonObj.get( "created_by" ) != null ) {
        testPlan.setCreatedByUserId( (long) jsonObj.get( "created_by" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have a user id set for who created it!" );
      }

      // Check if the test plan has any indication for when it was created.
      if ( jsonObj.containsKey( "created_on" ) && jsonObj.get( "created_on" ) != null ) {
        testPlan.setCreatedOn( (long) jsonObj.get( "created_on" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have a date set for when it was created!" );
      }

      // Check if the test plan has a description.
      if ( jsonObj.containsKey( "description" ) && jsonObj.get( "description" ) != null ) {
        testPlan.setDescription( (String) jsonObj.get( "description" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have a description set for it!" );
      }

      // Check if the test plan has any tests marked as failed.
      if ( jsonObj.containsKey( "failed_count" ) && jsonObj.get( "failed_count" ) != null ) {
        testPlan.setFailedCount( (long) jsonObj.get( "failed_count" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have any key for failed test cases!" );
      }

      // Check if the test plan has has a completed status.
      if ( jsonObj.containsKey( "is_completed" ) && jsonObj.get( "is_completed" ) != null ) {
        testPlan.setIsCompleted( (boolean) jsonObj.get( "is_completed" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have any indication if it was completed or not!" );
      }

      // Check if the test plan has a milestone id associated with it.
      if ( jsonObj.containsKey( "milestone_id" ) && jsonObj.get( "milestone_id" ) != null ) {
        testPlan.setMilestoneId( (long) jsonObj.get( "milestone_id" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have a milestone id associated with it!" );
      }

      // Check if the test plan has a name.
      if ( jsonObj.containsKey( "name" ) && jsonObj.get( "name" ) != null ) {
        testPlan.setPlanName( (String) jsonObj.get( "name" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have a name!" );
      }

      // Check if the test plan has any tests marked as passed
      if ( jsonObj.containsKey( "passed_count" ) && jsonObj.get( "passed_count" ) != null ) {
        testPlan.setPassedCount( (long) jsonObj.get( "passed_count" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have any key for passed test cases!" );
      }

      // Check if the test plan has a project id associated with it.
      if ( jsonObj.containsKey( "project_id" ) && jsonObj.get( "project_id" ) != null ) {
        testPlan.setProjectId( (long) jsonObj.get( "project_id" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have a project id associated with it!" );
      }

      // Check if the test plan has any tests marked as retest.
      if ( jsonObj.containsKey( "retest_count" ) && jsonObj.get( "retest_count" ) != null ) {
        testPlan.setRetestCount( (long) jsonObj.get( "retest_count" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have any key for retest test cases!" );
      }

      // Check if the test plan has any tests marked as untested.
      if ( jsonObj.containsKey( "untested_count" ) && jsonObj.get( "untested_count" ) != null ) {
        testPlan.setUntestedCount( (long) jsonObj.get( "untested_count" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have any key for untested test cases!" );
      }

      // Check if the test plan has a URL set for it.
      if ( jsonObj.containsKey( "url" ) && jsonObj.get( "url" ) != null ) {
        testPlan.setPlanURL( (String) jsonObj.get( "url" ) );
      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have a URL set for it!" );
      }

      // Check if the test plan has any entries.
      if ( jsonObj.containsKey( "entries" ) && jsonObj.get( "entries" ) != null ) {

        if ( jsonObj.get( "entries" ) instanceof JSONArray ) {
          testPlan.setEntries( createPTRTestEntries( (JSONArray) jsonObj.get( "entries" ) ) );
        } else {
          LOGGER.debug( "Test plan id '" + planId + "' contains entries but not in the right JSONArray format!" );
        }

      } else {
        LOGGER.debug( "Test plan id '" + planId + "' does not have any entries in it!" );
      }

    } else {
      Assert.fail( "The test plan object provided was empty! Could not convert to a PTRTestPlan object!" );
    }

    return testPlan;
  }

  /**
   * A helper method used to convert a JSONArray of test entries inside a test plan into an ArrayList of {@link
   * PTRTestEntry} objects.
   *
   * @param jsonArray The JSONArray of test entry information.
   * @return An ArrayList of the {@link PTRTestEntry} objects.
   */
  private ArrayList<PTRTestEntry> createPTRTestEntries( JSONArray jsonArray ) {

    ArrayList<PTRTestEntry> entries = new ArrayList<>();

    if ( jsonArray.size() > 0 ) {

      for ( Object obj : jsonArray ) {
        entries.add( createPTRTestEntry( (JSONObject) obj ) );
      }

    }

    return entries;
  }

  /**
   * A helper method used to convert a single JSONObject with test entry information into a single {@link PTRTestEntry}
   * object.
   *
   * @param jsonObj A JSONObject with the test entry information.
   * @return A {@link PTRTestEntry} object.
   */
  private PTRTestEntry createPTRTestEntry( JSONObject jsonObj ) {

    PTRTestEntry entry = new PTRTestEntry();

    if ( jsonObj != null && !jsonObj.isEmpty() ) {

      String entryName = "";

      // Check if the test entry has a name.
      if ( jsonObj.containsKey( "name" ) && jsonObj.get( "name" ) != null ) {
        entry.setName( (String) jsonObj.get( "name" ) );
        entryName = entry.getName();
      } else {
        Assert.fail( "The test entry provided does not have a name!" );
      }

      // Check if the test entry has a unique id.
      if ( jsonObj.containsKey( "id" ) && jsonObj.get( "id" ) != null ) {
        entry.setUniqueId( (String) jsonObj.get( "id" ) );
      } else {
        Assert.fail( "The test entry provided does not have a unique id!" );
      }

      // Check if the test entry has a suite id.
      if ( jsonObj.containsKey( "suite_id" ) && jsonObj.get( "suite_id" ) != null ) {
        entry.setSuiteId( (long) jsonObj.get( "suite_id" ) );
      } else {
        LOGGER.debug( "The test entry '" + entryName + "' does not have a suite id associated with it!" );
      }

      // Check if the test entry has description.
      if ( jsonObj.containsKey( "description" ) && jsonObj.get( "description" ) != null ) {
        entry.setDescription( (String) jsonObj.get( "description" ) );
      } else {
        LOGGER.debug( "The test entry '" + entryName + "' does not have a description!" );
      }

      // Check if the test entry has any entries.
      if ( jsonObj.containsKey( "runs" ) && jsonObj.get( "runs" ) != null ) {

        if ( jsonObj.get( "runs" ) instanceof JSONArray ) {

          PTRTestRunService.createPTRTestRuns( (JSONArray) jsonObj.get( "runs" ) ).forEach( entry::addRun );

        } else {
          LOGGER
            .debug( "The test entry '" + entryName + "' contains test runs but not in the right JSONArray format!" );
        }

      } else {
        LOGGER.debug( "The test entry '" + entryName + "' does not have any runs in it!" );
      }

    } else {
      Assert.fail( "The test entry object provided was empty! Could not convert to a PTRTestEntry object!" );
    }

    return entry;
  }
}
