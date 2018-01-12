package com.hv.services.testrail;

import com.hv.services.testrail.services.PTRConfigService;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.testng.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Base64;

/**
 * A Pentaho TestRail client class used for handling connection data to and from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/accessing">http://docs.gurock.com/testrail-api2/accessing</a>
 */
public class PTRClient {

  private static PTRConfigService globalConfigService;

  private static final LocalDate testRailsInceptionDate = LocalDate.of( 2016, 1, 1 );
  private static final int DEFAULT_STACK_TRACE_COUNT = 25;
  private static final String JIRA_ATTRIBUTE_TAG = "JIRA#";

  public PTRClient() {
    verifyClient();
  }

  /**
   * This method verifies the PTRClient for use by extended classes and ensures a username, API key, and base API URL
   * are always present.
   */
  private void verifyClient() {

    String username = getUsername();
    String apiKey = getApiKey();
    String baseApiUrl = getApiUrl();

    if ( username == null || username.equals( "" ) ) {
      Assert.fail(
        "Failed to create a PTRClient, the username is empty! Check the _testrail.properties file." );
    }

    if ( apiKey == null || apiKey.equals( "" ) ) {
      Assert.fail(
        "Failed to create a PTRClient, the API key is empty! Check the _testrail.properties file." );
    }

    if ( !baseApiUrl.contains( "api" ) ) {
      Assert.fail(
        "Failed to create a PTRClient, the base API url is incorrect! Check the _testrail.properties file." );
    }
  }

  /**
   * A method used to send HTTP "GET" requests to the Pentaho TestRail API server.
   *
   * @param apiExtension The TestRail API extension string. (e.g. "get_users/:user_id")
   * @return An object with JSON formatted response data unless an error has occurred, in which case the HTTP error
   * stream is parsed and returned as a String.
   * @see <a href="http://docs.gurock.com/testrail-api2/bindings-java#exampleget_request">http://docs.gurock
   * .com/testrail-api2/bindings-java#exampleget_request</a>
   */
  protected Object sendGet( String apiExtension ) {

    Object response = new Object();

    try {

      URL baseUrl = new URL( getHttpUrl() + getApiUrl() + apiExtension );
      HttpURLConnection httpConn = (HttpURLConnection) baseUrl.openConnection();

      httpConn.setRequestMethod( "GET" );
      httpConn.setRequestProperty( "Content-Type", "application/json" );
      httpConn.setRequestProperty( "Authorization", "Basic " + buildAuthorizationProperty() );

      int responseCode = httpConn.getResponseCode();

      /* Check the HTTP response.
      *  If the response is successful and there is input to receive, process it.
      *  If the response is the user was not authorized (401) and there is an error stream, process it.
      *  If all else fails, just return the response code and let the service handle what it might mean.
      */
      if ( responseCode == 200 && httpConn.getInputStream() != null ) {
        response = processInputStream( httpConn.getInputStream() );
      } else {
        response = processErrorStream( responseCode, httpConn.getErrorStream() );
      }

      httpConn.disconnect();

    } catch ( IOException e ) {
      e.printStackTrace();
    }

    return response;
  }

  /**
   * A method used to send HTTP "POST" requests to the Pentaho TestRail API server.
   *
   * @param apiExtension The TestRails api extension string. (e.g. "add_plan/:project_id")
   * @return An object with JSON formatted response data unless an error has occurred, in which case the HTTP response
   * code will be returned.
   * @see <a href="http://docs.gurock.com/testrail-api2/bindings-java#examplepost_request">http://docs.gurock
   * .com/testrail-api2/bindings-java#examplepost_request</a>
   */
  protected Object sendPost( String apiExtension, Object payloadObj ) {

    Object response = new Object();

    try {

      URL baseUrl = new URL( getHttpUrl() + getApiUrl() + apiExtension );
      HttpURLConnection httpConn = (HttpURLConnection) baseUrl.openConnection();

      httpConn.setRequestMethod( "POST" );
      httpConn.setRequestProperty( "Content-Type", "application/json" );
      httpConn.setRequestProperty( "Authorization", "Basic " + buildAuthorizationProperty() );
      httpConn.setDoOutput( true );

      processOutputStream( httpConn.getOutputStream(), payloadObj );

      int responseCode = httpConn.getResponseCode();

      /* Check the HTTP response.
      *  If the response is successful and there is input to receive, process it.
      *  If the response is the user was not authorized (401) and there is an error stream, process it.
      *  If all else fails, just return the response code and let the service handle what it might mean.
      */
      if ( responseCode == 200 && httpConn.getInputStream() != null ) {
        response = processInputStream( httpConn.getInputStream() );
      } else {
        response = processErrorStream( responseCode, httpConn.getErrorStream() );
      }

      httpConn.disconnect();

    } catch ( IOException e ) {
      e.printStackTrace();
    }
    return response;
  }

  /**
   * A method used to create the Base64 encoded basic authorization token that gets sent in the HTTP headers.
   *
   * @return A string representing the fully encoded authorization value.
   * @see <a href="http://docs.gurock.com/testrail-api2/accessing#username_and_api_key">http://docs.gurock
   * .com/testrail-api2/accessing#username_and_api_key</a>
   */
  private String buildAuthorizationProperty() {

    String authorizationProperty = getUsername() + ":" + getApiKey();

    try {
      authorizationProperty = Base64.getEncoder().encodeToString( authorizationProperty.getBytes( "UTF-8" ) );
    } catch ( UnsupportedEncodingException e ) {
      e.printStackTrace();
    }

    return authorizationProperty;
  }

  /**
   * A method used to receive a HTTP connection's input stream and process it into a JSON formatted object.
   *
   * @param inputStream The http connection's input stream if not empty.
   * @return A JSON Object based on the response from TestRails API.
   */
  private Object processInputStream( InputStream inputStream ) {

    Object streamResponse = new Object();

    try {

      InputStreamReader inputStreamReader = new InputStreamReader( inputStream, "UTF-8" );
      streamResponse = JSONValue.parse( inputStreamReader );
      inputStreamReader.close();

    } catch ( IOException e ) {
      e.printStackTrace();
    }

    return streamResponse;
  }

  /**
   * A method used to process a HTTP connection's output stream and write the payload to the connection.
   *
   * @param outputStream The POST requests connection stream to write out to.
   * @param payloadObj   The data to send in the HTTP request's message body.
   */
  private void processOutputStream( OutputStream outputStream, Object payloadObj ) {

    try {

      byte[] payload = JSONValue.toJSONString( payloadObj ).getBytes( "UTF-8" );

      outputStream.write( payload );

      outputStream.flush();
      outputStream.close();

    } catch ( IOException e ) {
      e.printStackTrace();
    }

  }

  /**
   * A method used to receive a HTTP connection's error stream and process it into a human readable format in the logs.
   *
   * @param responseCode The HTTP response code.
   * @param errorStream  The HTTP error stream if not empty.
   */
  private String processErrorStream( int responseCode, InputStream errorStream ) {

    String errorMessage = "TestRails API Error: " + responseCode + " - ";

    try {

      InputStreamReader inputStreamReader = new InputStreamReader( errorStream, "UTF-8" );
      errorMessage += ( (JSONObject) JSONValue.parse( inputStreamReader ) ).get( "error" ).toString();
      inputStreamReader.close();

    } catch ( IOException e ) {
      e.printStackTrace();
    }

    return errorMessage;
  }

  /**
   * A helper method used by extended classes to retrieve the date Pentaho activated TestRail for use by QA Automation.
   *
   * @return The activation date.
   */
  protected LocalDate getTestRailsInceptionDate() {
    return testRailsInceptionDate;
  }

  /**
   * A helper method used by extended classes to retrieve the static configuration service.
   *
   * @return The active configuration service.
   */
  protected static PTRConfigService getGlobalConfigService() {
    return globalConfigService;
  }

  /**
   * A helper method used to set the static configuration service.
   *
   * @param configService The service object to store.
   */
  protected void setGlobalConfigService( PTRConfigService configService ) {
    globalConfigService = configService;
  }

  /**
   * A helper method used by extended classes to retrieve the ignored browser configuration list.
   *
   * @return A String containing the value of the testrail_ignore_browsers key.
   */
  protected String getIgnoredBrowsers() {
    return PTRUpdater.getTestRailInfo( "testrail_ignore_browsers", true );
  }

  /**
   * A helper method used by extended classes to retrieve the ignored locale configuration list.
   *
   * @return A String containing the value of the testrail_ignore_locales key.
   */
  protected String getIgnoredLocales() {
    return PTRUpdater.getTestRailInfo( "testrail_ignore_locales", true );
  }

  /**
   * A helper method used by extended classes to retrieve the user.
   *
   * @return A String containing the value of the testrail_user key.
   */
  protected String getUsername() {
    return PTRUpdater.getTestRailInfo( "testrail_user", false );
  }

  /**
   * A helper method used by extended classes to retrieve the API key.
   *
   * @return A String containing the value of the testrail_apiKey key.
   */
  protected String getApiKey() {
    return PTRUpdater.getTestRailInfo( "testrail_apiKey", false );
  }

  /**
   * A helper method used by extended classes to retrieve the base Pentaho TestRail URL.
   *
   * @return A String containing the value of the testrail_url key.
   */
  protected String getHttpUrl() {

    String baseTestRailsUrl = PTRUpdater.getTestRailInfo( "testrail_url", false );

    if ( !baseTestRailsUrl.endsWith( "/" ) ) {
      baseTestRailsUrl += "/";
    }

    return baseTestRailsUrl;
  }

  /**
   * A helper method used by extended classes to retrieve the Pentaho TestRail API extension.
   *
   * @return A String containing the value of the testrail_baseApi_url key.
   */
  protected String getApiUrl() {
    return PTRUpdater.getTestRailInfo( "testrail_baseApi_url", false );
  }

  /**
   * A helper method used by extended classes to retrieve the boolean value indicating whether or not to group test run
   * configurations.
   *
   * @return A Boolean value of the testrail_testplan_entry_group_runs key.
   */
  protected boolean isGroupTestRunsAllowed() {
    return Boolean.valueOf( PTRUpdater.getTestRailInfo( "testrail_testplan_entry_group_runs", false ) );
  }

  /**
   * A helper method used by extended classes to retrieve the Integer value indicating how many stack trace elements
   * should be displayed in a test run's report.
   *
   * @return An int value of the testrail_results_stack_trace_amount key.
   */
  protected int getHowManyStackTracesToDisplay() {

    if ( PTRUpdater.getTestRailInfo( "testrail_results_stack_trace_amount", true ).equals( "" ) ) {
      return DEFAULT_STACK_TRACE_COUNT;
    } else {
      return Integer.valueOf( PTRUpdater.getTestRailInfo( "testrail_results_stack_trace_amount", false ) );
    }
  }

  /**
   * A helper method used by extended classes to retrieve the JIRA attribute tag used in the description of a test and a
   * test result's custom attribute that stores any Jira.setTickets().
   *
   * @return The JIRA attribute tag value.
   */
  protected String getJiraAttributeTag() {
    return JIRA_ATTRIBUTE_TAG;
  }

  /**
   * A helper method used by extended classes to retrieve the ETAF Report link for a test run on Jenkins.
   *
   * @return The report link if any was retrieve, or "" if empty.
   */
  protected String getReportLink() {
    return PTRUpdater.getReportLink();
  }

  /**
   * A helper method used by extended classes to retrieve the boolean value indicating whether or not to record any
   * retry results in the comment of a test instance.
   *
   * @return A boolean value of the testrail_results_record_retries key.
   */
  protected boolean isRecordingRetryResults() {
    return Boolean.valueOf( PTRUpdater.getTestRailInfo( "testrail_results_record_retries", false ) );
  }

  protected String getNotAJsonError( String request, String response ) {
    return "Response is not a JSON object. Something went wrong with the request. Request: " + request + ". Response: "
      + response + ".";
  }
}
