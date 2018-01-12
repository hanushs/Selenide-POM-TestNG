package com.hv.services.testrail.services;

import com.hv.services.testrail.PTRClient;
import com.hv.services.testrail.objects.PTRTestCase;
import com.hv.services.testrail.objects.PTRTestStep;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A Pentaho TestRail test case class used for handling test cases from the TestRails API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-cases">http://docs.gurock
 * .com/testrail-api2/reference-cases</a>
 */
public class PTRTestCaseService extends PTRClient {

  private static final Logger LOGGER = Logger.getLogger( PTRTestCaseService.class );

  /**
   * A method used to retrieve the data for a single test case through the TestRail API using the test case's id
   * and then runs {@link #createPTRTestCase(JSONObject)} to create a test case object from that information.
   *
   * @param caseId The assigned case id for the desired test case.
   * @return {@link PTRTestCase} - A test case object, empty if nothing is returned by the API.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-cases#get_case">http://docs.gurock
   * .com/testrail-api2/reference-cases#get_case</a>
   */
  public PTRTestCase getCase( long caseId ) {

    String getRequest = "get_case/" + caseId;
    Object response = sendGet( getRequest );
    PTRTestCase testCase = new PTRTestCase();

    if ( response instanceof JSONObject ) {
      testCase = createPTRTestCase( (JSONObject) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return testCase;
  }

  /**
   * A method used to retrieve the data for EVERY SINGLE TEST CASE in the given project and suite through the TestRail
   * API and then runs {@link #createPTRTestCases(JSONArray)} to create test case objects from that information.
   * <p>
   * <b>WARNING:</b> This method will retrieve and convert every available test case associated with the project id and
   * suite id provided. This could take awhile to run if you have over 100+ test cases.
   * <p>
   * Please use one of the filtered methods below to have more control over the returned list:
   * <ul>
   * <li>{@link #getCasesFromSection(long, long, long)}</li>
   * <li>{@link #getCasesBetweenDates(long, long, LocalDate, LocalDate)}</li>
   * </ul>
   *
   * @param projectId The id of the project to retrieve test cases from.
   * @param suiteId   The id of the test suite to retrieve test cases from.
   * @return An ArrayList with each test case or an empty ArrayList if nothing is returned by the API.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-cases#get_cases">http://docs.gurock
   * .com/testrail-api2/reference-cases#get_cases</a>
   */
  public ArrayList<PTRTestCase> getCases( long projectId, long suiteId ) {
    return getCases( projectId, suiteId, "" );
  }

  /**
   * A method used to retrieve the data for test cases in the given project, suite, and specific section.
   *
   * @param projectId The id of the project to retrieve test cases from.
   * @param suiteId   The id of the test suite to retrieve test cases from.
   * @param sectionId The id of the section to retrieve test cases from.
   * @return An ArrayList with each test case or an empty ArrayList if nothing is returned by the API.
   */
  public ArrayList<PTRTestCase> getCasesFromSection( long projectId, long suiteId, long sectionId ) {
    return getCases( projectId, suiteId, "&section_id=" + sectionId );
  }

  /**
   * A method used to retrieve the data for test cases in the given project, suite, and between specific dates.
   *
   * @param projectId The id of the project to retrieve test cases from.
   * @param suiteId The id of the test suite to retrieve test cases from.
   * @param afterDate The LocalDate to retrieve cases after.
   * @param beforeDate The LocaleDate to retreive cases before.
   * @return An ArrayList with each test case or an empty ArrayList if nothing is returned by the API.
   */
  public ArrayList<PTRTestCase> getCasesBetweenDates( long projectId, long suiteId, LocalDate afterDate,
                                                      LocalDate beforeDate ) {

    String filters = "";

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

    return getCases( projectId, suiteId, filters );
  }

  /**
   * A method used to send the GET request to the TestRail API including all filters applied.
   *
   * @param projectId The id of the project to retrieve test cases from.
   * @param suiteId The id of the test suite to retrieve test cases from.
   * @param filters A string containing TestRail API URL specific filters and values
   * @return An ArrayList with each test case or an empty ArrayList if nothing is returned by the API.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-cases#get_cases">http://docs.gurock.com/testrail-api2/reference-cases#get_cases</a>
   */
  private ArrayList<PTRTestCase> getCases( long projectId, long suiteId, String filters ) {

    ArrayList<PTRTestCase> testCases = new ArrayList<>();
    Object response;
    String getRequest;

    if ( filters.equals( "" ) ) {
      getRequest = "get_cases/" + projectId + "&suite_id=" + suiteId;
    } else {
      getRequest = "get_cases/" + projectId + "&suite_id=" + suiteId + filters;
    }

    response = sendGet( getRequest );

    if ( response instanceof JSONArray ) {
      testCases = createPTRTestCases( (JSONArray) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return testCases;
  }

  /**
   * A helper method used to convert a JSONArray of test cases into an ArrayList of {@link PTRTestCase} objects.
   *
   * @param jsonArray A JSONArray of projects to convert.
   * @return ArrayList<PTRTestCase> - An array list of {@link PTRTestCase} objects.
   */
  private ArrayList<PTRTestCase> createPTRTestCases( JSONArray jsonArray ) {

    ArrayList<PTRTestCase> testCases = new ArrayList<>();

    if ( jsonArray.size() > 0 ) {

      for ( Object obj : jsonArray ) {
        testCases.add( createPTRTestCase( (JSONObject) obj ) );
      }

    } else {
      Assert.fail( "The given JSONArray does not any data for test cases. Returning empty array list." );
    }

    return testCases;
  }

  /**
   * A helper method used to convert a single Object into a single {@link PTRTestCase}.
   *
   * @param jsonObj The object to convert.
   * @return The converted test case.
   */
  private PTRTestCase createPTRTestCase( JSONObject jsonObj ) {

    PTRTestCase testCase = new PTRTestCase();

    if ( jsonObj != null && !jsonObj.isEmpty() ) {

      long caseId = -1;

      // Check if the test case has an id.
      if ( jsonObj.containsKey( "id" ) && jsonObj.get( "id" ) != null ) {
        testCase.setCaseId( (long) jsonObj.get( "id" ) );
        caseId = testCase.getCaseId();
      } else {
        Assert.fail( "Test case object does not have an id associated with it!" );
      }

      // Check if the test case has a user id for who created it.
      if ( jsonObj.containsKey( "created_by" ) && jsonObj.get( "created_by" ) != null ) {
        testCase.setCreatedByUserId( (long) jsonObj.get( "created_by" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have a user id associated with who created it!" );
      }

      // Check if the test case has the date it was created on set.
      if ( jsonObj.containsKey( "created_on" ) && jsonObj.get( "created_on" ) != null ) {
        testCase.setCreatedOn( (long) jsonObj.get( "created_on" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have a date set for when it was created!" );
      }

      // Check if the test case has an estimated time for how long it will take to run.
      if ( jsonObj.containsKey( "estimate" ) && jsonObj.get( "estimate" ) != null ) {
        testCase.setEstimate( (String) jsonObj.get( "estimate" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have a time estimate set!" );
      }

      // Check if the test case has a forecast for the estimate.
      if ( jsonObj.containsKey( "estimate_forecast" ) && jsonObj.get( "estimate_forecast" ) != null ) {
        testCase.setEstimateForecast( (String) jsonObj.get( "estimate_forecast" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have an estimated forecast set!" );
      }

      // Check if the test case has a milestone id associated with it.
      if ( jsonObj.containsKey( "milestone_id" ) && jsonObj.get( "milestone_id" ) != null ) {
        testCase.setMilestoneId( (long) jsonObj.get( "milestone_id" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have a milestone id associated with it!" );
      }

      // Check if the test case has a priority id associated with it.
      if ( jsonObj.containsKey( "priority_id" ) && jsonObj.get( "priority_id" ) != null ) {
        testCase.setPriorityId( (long) jsonObj.get( "priority_id" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have a priority id associated with it!" );
      }

      // Check if the test case has a section id associated with it.
      if ( jsonObj.containsKey( "section_id" ) && jsonObj.get( "section_id" ) != null ) {
        testCase.setSectionId( (long) jsonObj.get( "section_id" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have a section id associated with it!" );
      }

      // Check if the test case has a suite id associated with it.
      if ( jsonObj.containsKey( "suite_id" ) && jsonObj.get( "suite_id" ) != null ) {
        testCase.setSuiteId( (long) jsonObj.get( "suite_id" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have a suite id associated with it!" );
      }

      // Check if the test case has a template id associated with it.
      if ( jsonObj.containsKey( "template_id" ) && jsonObj.get( "template_id" ) != null ) {
        testCase.setTemplateId( (long) jsonObj.get( "template_id" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have a template id associated with it!" );
      }

      // Check if the test case has a type id associated with it.
      if ( jsonObj.containsKey( "type_id" ) && jsonObj.get( "type_id" ) != null ) {
        testCase.setTypeId( (long) jsonObj.get( "type_id" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have a type id associated with it!" );
      }

      // Check if the test case has a user id associated with who last updated the case.
      if ( jsonObj.containsKey( "updated_by" ) && jsonObj.get( "updated_by" ) != null ) {
        testCase.setUpdatedByUserId( (long) jsonObj.get( "updated_by" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have a user id set for who last updated the case!" );
      }

      // Check if the test case has a date set for when the case was last updated.
      if ( jsonObj.containsKey( "updated_on" ) && jsonObj.get( "updated_on" ) != null ) {
        testCase.setDateUpdatedOn( (long) jsonObj.get( "updated_on" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have date set for when the case was last updated!" );
      }

      // Check if the test case has a title associated with it.
      if ( jsonObj.containsKey( "title" ) && jsonObj.get( "title" ) != null ) {
        testCase.setTitle( (String) jsonObj.get( "title" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have a title set!" );
      }

      // Check if the test case has any references/requirements.
      if ( jsonObj.containsKey( "refs" ) && jsonObj.get( "refs" ) != null ) {
        testCase.setReferences( (String) jsonObj.get( "refs" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have any references or requirements set!" );
      }

      // Check if the test case has any custom preconditions.
      if ( jsonObj.containsKey( "custom_preconds" ) && jsonObj.get( "custom_preconds" ) != null ) {
        testCase.setReferences( (String) jsonObj.get( "custom_preconds" ) );
      } else {
        LOGGER.debug( "Test case id '" + caseId + "' does not have any preconditions set!" );
      }

      /*  ========== IGNORE CUSTOM STEPS AS WE CURRENTLY DO NOT CARE ABOUT THEM FOR RESULTS. ==========

      // Check if the test case has any steps.
      if ( jsonObj.containsKey( "custom_steps_separated" ) && jsonObj.get( "custom_steps_separated" ) != null ) {

        if ( jsonObj.get( "custom_steps_separated" ) instanceof JSONArray ) {
          testCase.setTestSteps( createPTRTestSteps( caseId, (JSONArray) jsonObj.get( "custom_steps_separated" ) ) );
        } else {
          LOGGER.debug( "Test case id '" + caseId + "' contains steps but not in the right JSONArray format!" );
        }

      } else {
        Assert.fail( "Test case id '" + caseId + "' does not have any custom steps set!" );
      }
      */

    } else {
      Assert.fail( "The test case object provided was empty! Could not convert to a PTRTestCase object!" );
    }

    return testCase;
  }

  /**
   * A helper method used to convert a single JSONArray into an ArrayList of {@link PTRTestStep}
   *
   * @param jsonArray The custom steps separated to convert.
   * @return The converted test steps.
   */
  private ArrayList<PTRTestStep> createPTRTestSteps( long caseId, JSONArray jsonArray ) {

    ArrayList<PTRTestStep> steps = new ArrayList<>();

    if ( jsonArray.size() > 0 ) {

      // Set to human readable format "1" because PTRTestStep will automatically decrement for zero based array format.
      long stepNumber = 1;

      for ( Object obj : jsonArray ) {
        steps.add( createPTRTestStep( caseId, stepNumber, (JSONObject) obj ) );
        stepNumber++;
      }

    } else {
      Assert.fail( "The given JSONArray does not any data for test steps. Returning empty array list." );
    }

    return steps;
  }

  /**
   * A helper method used to convert a single Object into a single {@link PTRTestStep}
   *
   * @param jsonObj The step object to convert.
   * @return The converted test step.
   */
  private PTRTestStep createPTRTestStep( long caseId, long stepNumber, JSONObject jsonObj ) {

    PTRTestStep step = new PTRTestStep();

    if ( jsonObj != null && !jsonObj.isEmpty() ) {

      if ( stepNumber > 0 ) {

        step.setStepNumber( stepNumber );

        // Check if the step has any content written in the left most step box.
        if ( jsonObj.containsKey( "content" ) && jsonObj.get( "content" ) != null ) {
          step.setStepContent( (String) jsonObj.get( "content" ) );
        } else {
          LOGGER.debug( "Step number '" + stepNumber + "' of case id '" + caseId + "' does not have any content set!" );
        }

        // Check if the step has any expectations written in the right most step box.
        if ( jsonObj.containsKey( "expected" ) && jsonObj.get( "expected" ) != null ) {
          step.setExpectedResult( (String) jsonObj.get( "expected" ) );
        } else {
          LOGGER
            .debug( "Step number '" + stepNumber + "' of case id '" + caseId + "' does not have any expectations!" );
        }

      } else {
        LOGGER.debug( "The step number '" + stepNumber + "' provided was 0 or below. It must be 1 or higher!" );
      }

    } else {
      Assert.fail( "The test step object provided was empty! Could not convert to a PTRTestStep object!" );
    }

    return step;
  }
}
