package com.hv.services.testrail.services;

import com.hv.services.testrail.PTRClient;
import com.hv.services.testrail.configurations.PTRStatusOptions;
import com.hv.services.testrail.objects.PTRTestResult;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * A Pentaho TestRails test results class used for sending and retrieving test result data from the TestRails API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-results">http://docs.gurock
 * .com/testrail-api2/reference-results</a>
 */
public class PTRTestResultService extends PTRClient {

  private static final Logger LOGGER = Logger.getLogger( PTRTestResultService.class );

  /**
   * A method used to GET the results from a single test instance.
   *
   * @param testInstanceId The test instance id to get results from.
   * @return A {@link PTRTestResult} object.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-results#get_results">http://docs.gurock
   * .com/testrail-api2/reference-results#get_results</a>
   */
  public ArrayList<PTRTestResult> getResults( long testInstanceId ) {

    ArrayList<PTRTestResult> testResults = new ArrayList<>();

    String getRequest = "get_results/" + testInstanceId;
    Object response = sendGet( getRequest );

    if ( response instanceof JSONArray ) {
      testResults = createPTRTestResults( (JSONArray) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return testResults;

  }

  /**
   * A method used to POST a new result to a single test instance.
   *
   * @param testInstanceId The test instance id to add the new result to.
   * @param testResult     The result.
   * @return A new {@link PTRTestResult} containing any updated data from the API after POST.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-results#add_result">http://docs.gurock
   * .com/testrail-api2/reference-results#add_result</a>
   */
  public PTRTestResult addResult( long testInstanceId, PTRTestResult testResult ) {

    JSONObject payload = new JSONObject();

    if ( testResult.getStatusId() != PTRStatusOptions.UNTESTED.getId() ) {
      payload.put( "status_id", testResult.getStatusId() );
    }

    payload.put( "comment", testResult.getComment() );
    payload.put( "elapsed", testResult.getTimeElapsed() );
    payload.put( "version", testResult.getBuildVersion() );
    payload.put( "defects", testResult.getDefects() );

    String postRequest = "add_result/" + testInstanceId;
    Object response = sendPost( postRequest, payload );

    if ( response instanceof JSONObject ) {
      testResult = createPTRTestResult( (JSONObject) response );
    } else {
      Assert.fail( getNotAJsonError( postRequest, response.toString() ) );
    }

    return testResult;
  }

  /**
   * A method used to construct a result's comment.
   *
   * @param testResults The TestNG results from which to construct the comment data.
   * @return A String containing the final comment's data and formatting information.
   */
  public String buildResultComment( String xmlTestName, ArrayList<ITestResult> testResults ) {
    StringBuilder sb = new StringBuilder();

    sb.append( '#' );
    sb.append( xmlTestName );
    sb.append( " Results#" );

    if ( !getReportLink().equals( "" ) ) {
      sb.append( "\n _[ETAF Report Page](" );
      sb.append( getReportLink() );
      sb.append( ") / [ETAF Zip Download](" );
      sb.append( getReportLink() );
      sb.append( "/*zip*/eTAF_Report.zip)" );
      sb.append( " / [Rebuild](" );
      sb.append( getReportLink().replace( "/eTAF_Report", "/rebuild" ) );
      sb.append( ")_" );
    }

    sb.append( "\n||| :Result: | Method Name: | :Message" );

    ArrayList<String> badMethods = new ArrayList<>();

    for ( ITestResult testResult : testResults ) {

      int invocationId = (int) testResult.getAttribute( "invocationId" ) - 1;

      sb.append( "\n|| " );

      switch ( testResult.getStatus() ) {
        case ITestResult.SUCCESS: {
          sb.append( PTRStatusOptions.PASSED.getName().toUpperCase() );
          sb.append( " | " );
          sb.append( testResult.getName() );

          if ( isRecordingRetryResults() && invocationId > 0 ) {
            sb.append( " - Retry #" );
            sb.append( invocationId );
          }

          sb.append( " | " );
          sb.append( "This method executed successfully." );
          break;
        }
        case ITestResult.FAILURE: {

          sb.append( PTRStatusOptions.FAILED.getName().toUpperCase() );
          sb.append( " | " );
          sb.append( testResult.getName() );

          if ( isRecordingRetryResults() && invocationId > 0 ) {
            sb.append( " - Retry #" );
            sb.append( invocationId );
          }

          sb.append( " | " );

          if ( testResult.getThrowable().getMessage() != null ) {
            sb.append( testResult.getThrowable().getMessage().replace( "\n", " " ) );
          } else {
            sb.append( "Throwable had no failure message(s) for the error." );
          }

          if ( getHowManyStackTracesToDisplay() > 0 ) {
            int counter = 1;
            for ( StackTraceElement stackTraceElement : testResult.getThrowable().getStackTrace() ) {
              sb.append( "\n|| | \u21B3 | " );
              sb.append( '`' );
              sb.append( stackTraceElement );
              sb.append( '`' );

              if ( counter >= getHowManyStackTracesToDisplay() ) {
                break;
              } else {
                counter++;
              }
            }
          }

          badMethods.add( testResult.getInstanceName() + "." + testResult.getMethod().getMethodName() );
          break;
        }
        case ITestResult.SKIP: {
          sb.append( "SKIPPED" );
          sb.append( " | " );
          sb.append( testResult.getName() );

          if ( isRecordingRetryResults() && invocationId > 0 ) {
            sb.append( " - Retry #" );
            sb.append( invocationId );
          }

          sb.append( " | " );

          if ( testResult.getMethod().getMethodsDependedUpon().length > 0 ) {
            for ( String methodDependedOn : testResult.getMethod().getMethodsDependedUpon() ) {
              if ( badMethods.contains( methodDependedOn ) ) {
                sb.append( "Depended on method: " );
                sb.append( methodDependedOn.substring( methodDependedOn.lastIndexOf( '.' ) + 1 ) );
                break;
              }
            }
          } else {

            Throwable throwable = testResult.getThrowable();
            if ( throwable != null ) {
              if ( throwable.getMessage() != null ) {
                sb.append( throwable.getMessage().replace( "\n", " " ) );
              } else {
                sb.append( "Throwable had no message(s) for the skip." );
              }
            } else {
              sb.append( "No exception was thrown for the skip." );
            }
          }

          badMethods.add( testResult.getInstanceName() + "." + testResult.getMethod().getMethodName() );
          break;
        }
      }
      sb.append( "\n|| ` `" );
    }

    return sb.toString();
  }

  /**
   * A helper method used to calculate the time that has elapsed between onStart and onFinish methods of a test. If the
   * value could not be calculated or is somehow less than 1.00 seconds, then default to the lowest supported TestRail
   * value of "1s"
   *
   * @param startTime The start time milliseconds from the test result.
   * @param endTime   The end time in milliseconds from the test result.
   * @return A String that is used by TestRail in the result's elapsed field.
   */
  public String calculateElapsedTime( long startTime, long endTime ) {
    long elapsedTime = ( endTime - startTime ) / 1000L;
    if ( elapsedTime >= 1L ) {
      return String.valueOf( elapsedTime ) + "s";
    } else {
      return "1s";
    }
  }

  /**
   * A helper method used to retrieve the tagged defects in a result's test method description or the result's
   * attribute for JIRA tickets that gets set by any calls to the Jira.setTickets() methods.
   *
   * @param testResults The results list to parse through.
   * @return A TestRail formatted string of non-spaced, comma-separated, all uppercase JIRA ticket numbers.
   */
  public String parseAllDefects( ArrayList<ITestResult> testResults ) {

    ArrayList<String> allDefects = new ArrayList<>();

    for ( ITestResult testResult : testResults ) {

      ArrayList<String> attributeDefects = (ArrayList<String>) testResult.getAttribute( getJiraAttributeTag() );
      String description = testResult.getMethod().getDescription();

      // Collect description defects
      if ( description != null && description.contains( getJiraAttributeTag() ) ) {
        description =
          description.substring( description.indexOf( getJiraAttributeTag() ) + getJiraAttributeTag().length() ).trim();

        if ( description.contains( "," ) ) {
          for ( String splitTicket : description.split( "," ) ) {
            if ( !allDefects.contains( splitTicket ) ) {
              allDefects.add( splitTicket.trim() );
            }
          }
        } else {
          if ( !allDefects.contains( description ) ) {
            allDefects.add( description.trim() );
          }
        }
      }

      // Collect attribute defects
      if ( attributeDefects != null && attributeDefects.size() > 0 ) {
        for ( String attributeDefect : attributeDefects ) {
          if ( !allDefects.contains( attributeDefect ) ) {
            allDefects.add( attributeDefect );
          }
        }
      }
    }

    return allDefects.stream().collect( Collectors.joining( "," ) ).toUpperCase();
  }

  /**
   * A helper method used to convert a JSONArray of test results into an ArrayList of {@link PTRTestResult} objects.
   *
   * @param jsonArray A JSONArray of results to convert.
   * @return ArrayList<PTRTestResult> - An array list of {@link PTRTestResult} objects.
   */
  private ArrayList<PTRTestResult> createPTRTestResults( JSONArray jsonArray ) {

    ArrayList<PTRTestResult> testResults = new ArrayList<>();

    if ( jsonArray.size() > 0 ) {

      for ( Object obj : jsonArray ) {
        testResults.add( createPTRTestResult( (JSONObject) obj ) );
      }

    } else {
      Assert.fail( "The given JSONArray does not any data for test results. Returning empty array list." );
    }

    return testResults;
  }

  /**
   * A helper method used to convert a single Object into a single {@link PTRTestResult}.
   *
   * @param jsonObj The object to convert.
   * @return The converted test result.
   */
  private PTRTestResult createPTRTestResult( JSONObject jsonObj ) {

    PTRTestResult testResult = new PTRTestResult();

    if ( jsonObj != null && !jsonObj.isEmpty() ) {
      long resultId = -1;

      // Check if the test result has an id itself.
      if ( jsonObj.containsKey( "id" ) && jsonObj.get( "id" ) != null ) {
        testResult.setResultId( (long) jsonObj.get( "id" ) );
        resultId = testResult.getResultId();
      } else {
        Assert.fail( "Test result received does not have an id associated with it!" );
      }

      // Check if the test result has a test instance related to it.
      if ( jsonObj.containsKey( "test_id" ) && jsonObj.get( "test_id" ) != null ) {
        testResult.setTestInstanceId( (long) jsonObj.get( "test_id" ) );
      } else {
        LOGGER.debug( "Test result id '" + resultId + "' does not have a test instance associated with it!" );
      }

      // Check if the test result has a status id.
      if ( jsonObj.containsKey( "status_id" ) && jsonObj.get( "status_id" ) != null ) {
        testResult.setStatusId( (long) jsonObj.get( "status_id" ) );
      } else {
        LOGGER.debug( "Test result id '" + resultId + "' does not have a status id!" );
      }

      // Check if the test result is assigned to a user.
      if ( jsonObj.containsKey( "assignedto_id" ) && jsonObj.get( "assignedto_id" ) != null ) {
        testResult.setAssignedToUserId( (long) jsonObj.get( "assignedto_id" ) );
      } else {
        LOGGER.debug( "Test result id '" + resultId + "' does not a user id associated with it!" );
      }

      // Check if the test result contains a comment.
      if ( jsonObj.containsKey( "comment" ) && jsonObj.get( "comment" ) != null ) {
        testResult.setComment( (String) jsonObj.get( "comment" ) );
      } else {
        LOGGER.debug( "Test result id '" + resultId + "' does not have a comment!" );
      }

      // Check if the test result has a user id for who created it.
      if ( jsonObj.containsKey( "created_by" ) && jsonObj.get( "created_by" ) != null ) {
        testResult.setCreatedByUserId( (long) jsonObj.get( "created_by" ) );
      } else {
        LOGGER.debug( "Test result id '" + resultId + "' does not have a user id for who created it!" );
      }

      // Check if the test result has a date on which it was created.
      if ( jsonObj.containsKey( "created_on" ) && jsonObj.get( "created_on" ) != null ) {
        testResult.setCreatedOn( (long) jsonObj.get( "created_on" ) );
      } else {
        LOGGER.debug( "Test result id '" + resultId + "' does not have a start date for when it was created!" );
      }

      // Check if the test result has any associated defects.
      if ( jsonObj.containsKey( "defects" ) && jsonObj.get( "defects" ) != null ) {
        testResult.setDefects( (String) jsonObj.get( "defects" ) );
      } else {
        LOGGER.debug( "Test result id '" + resultId + "' does not have any set defects!" );
      }

      // Check if the test result has the time it took to execute.
      if ( jsonObj.containsKey( "elapsed" ) && jsonObj.get( "elapsed" ) != null ) {
        testResult.setTimeElapsed( (String) jsonObj.get( "elapsed" ) );
      } else {
        LOGGER.debug( "Test result id '" + resultId + "' does not indicate how long it took!" );
      }

      // Check if the test result contains a build version that it was ran against.
      if ( jsonObj.containsKey( "version" ) && jsonObj.get( "version" ) != null ) {
        testResult.setBuildVersion( (String) jsonObj.get( "version" ) );
      } else {
        LOGGER.debug( "Test result id '" + resultId + "' does not have any build version set!" );
      }

    } else {
      Assert.fail( "The result object provided was empty! Could not convert to a PTRResult object!" );
    }

    return testResult;
  }
}
