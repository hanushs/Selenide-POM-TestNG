package com.hv.services.testrail.services;

import com.hv.services.testrail.PTRClient;
import com.hv.services.testrail.objects.PTRStatus;
import com.hv.services.testrail.configurations.PTRStatusOptions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import java.util.ArrayList;
import java.util.List;

/**
 * A Pentaho TestRail status class used for handling status information from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-statuses">http://docs.gurock
 *      .com/testrail-api2/reference-statuses</a>
 */
public class PTRStatusService extends PTRClient {

  private ArrayList<PTRStatus> statuses = new ArrayList<>();

  public PTRStatusService() {
    statuses = getStatuses();
  }

  /**
   * A method used to calculate the status id a test instance should receive based upon the final TestNG test results.
   *
   * @param testResults
   *          An ArrayList of {@link ITestResult} objects to calculate from.
   * @return long - The Pentaho specific status id.
   */
  public long calculateStatus( ArrayList<ITestResult> testResults ) {

    int passCount = 0, failCount = 0, skipCount = 0, blockedCount = 0, knownIssuesCount = 0;

    ArrayList<String> badMethods = new ArrayList<>();

    for ( ITestResult testResult : testResults ) {

      switch ( testResult.getStatus() ) {
        case ITestResult.SUCCESS: {
          passCount++;
          break;
        }
        case ITestResult.FAILURE: {
          ITestNGMethod testMethod = testResult.getMethod();
          List<String> jiraTickets = (ArrayList<String>) testResult.getAttribute( getJiraAttributeTag() );
          // If we're recording retries, we don't want to record a failure for every failed invocation.
          // Only record it as a failure if it was the last known invocation of the method.
          if ( isRecordingRetryResults() ) {
            if ( ( (int) testResult.getAttribute( "invocationId" ) ) == testMethod.getCurrentInvocationCount() ) {
              if ( doesMethodContainKnownIssues( jiraTickets ) ) {
                knownIssuesCount++;
              } else {
                failCount++;
              }
            }
          } else {
            if ( doesMethodContainKnownIssues( jiraTickets ) ) {
              knownIssuesCount++;
            } else {
              failCount++;
            }
          }
          badMethods.add( testResult.getInstanceName() + "." + testResult.getMethod().getMethodName() );
          break;
        }
        case ITestResult.SKIP: {

          if ( testResult.getMethod().getMethodsDependedUpon().length > 0 ) {
            for ( String dependentMethod : testResult.getMethod().getMethodsDependedUpon() ) {
              if ( badMethods.contains( dependentMethod ) ) {
                blockedCount++;
              } else {
                skipCount++;
              }
            }
          } else {
            skipCount++;
          }

          badMethods.add( testResult.getInstanceName() + "." + testResult.getMethod().getMethodName() );
          break;
        }
      }
    }

    // Determine the overall status of a result based upon counts.
    if ( failCount == 0 && knownIssuesCount > 0 ) {
      return PTRStatusOptions.PASSED_KNOWN_ISSUES.getId();
    } else if ( failCount > 0 ) {
      return PTRStatusOptions.FAILED.getId();
    } else if ( passCount > 0 && blockedCount + skipCount == 0 ) {
      return PTRStatusOptions.PASSED.getId();
    } else {
      return PTRStatusOptions.RETEST.getId();
    }
  }

  private boolean doesMethodContainKnownIssues( List<String> jiraTickets ) {
    return ( jiraTickets != null && !jiraTickets.isEmpty() );
  }

  /**
   * A method used to send the GET request to the TestRail API and retrieve all available status options during
   * instantiation of the status service.
   *
   * @return An ArrayList of {@link PTRStatus} objects available.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-statuses#get_statuses">http://docs.gurock
   *      .com/testrail-api2/reference-statuses#get_statuses</a>
   */
  private ArrayList<PTRStatus> getStatuses() {

    String getRequest = "get_statuses";
    Object response = sendGet( getRequest );

    ArrayList<PTRStatus> statuses = new ArrayList<>();

    if ( response instanceof JSONArray ) {
      statuses = createPTRStatuses( (JSONArray) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return statuses;
  }

  /**
   * A method used to convert a JSONArray with status information into an ArrayList of {@link PTRStatus} objects.
   *
   * @param jsonArray
   *          The JSONArray containing status information.
   * @return An ArrayList of {@link PTRStatus} objects.
   */
  private ArrayList<PTRStatus> createPTRStatuses( JSONArray jsonArray ) {

    ArrayList<PTRStatus> configs = new ArrayList<>();

    if ( jsonArray.size() > 0 ) {

      for ( Object obj : jsonArray ) {
        configs.add( createPTRStatus( (JSONObject) obj ) );
      }

    } else {
      Assert.fail( "The given JSONArray does not any data for statuses. Returning empty array list." );
    }

    return configs;
  }

  /**
   * A method used to convert a single JSONObject with status information into a {@link PTRStatus} object.
   *
   * @param jsonObj
   *          The JSONObject containing status information.
   * @return An ArrayList of {@link PTRStatus} objects.
   */
  private PTRStatus createPTRStatus( JSONObject jsonObj ) {

    PTRStatus status = new PTRStatus();

    if ( jsonObj != null && !jsonObj.isEmpty() ) {

      // Check if the status has an id.
      if ( jsonObj.containsKey( "id" ) && jsonObj.get( "id" ) != null ) {
        status.setStatusId( (long) jsonObj.get( "id" ) );
      } else {
        Assert.fail( "The status object provided does not have an id associated with it!" );
      }

      // Check if the status has a display name
      if ( jsonObj.containsKey( "label" ) && jsonObj.get( "label" ) != null ) {
        status.setDisplayName( (String) jsonObj.get( "label" ) );
      } else {
        Assert.fail( "The status object provided does not have a display name!" );
      }

      // Check if the status has a system name
      if ( jsonObj.containsKey( "name" ) && jsonObj.get( "name" ) != null ) {
        status.setSystemName( (String) jsonObj.get( "name" ) );
      } else {
        Assert.fail( "The status object provided does not have a system name!" );
      }

    } else {
      Assert.fail( "The status object provided was empty! Could not convert to a PTRConfig object!" );
    }

    return status;
  }

}
