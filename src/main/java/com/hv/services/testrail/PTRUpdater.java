package com.hv.services.testrail;

import org.apache.commons.collections4.CollectionUtils;
import com.hv.services.testrail.services.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import com.hv.services.testrail.objects.*;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.util.Strings;

/**
 * A Pentaho TestRail updater class used to handle the processes before a suite runs it's tests and after all the tests
 * have finished running.
 * <p>
 * This class implements the ITestListener class and will need to be added as a suite level listener parameter to any
 * TestNG xml file that wants to use the TestRail integration package. Example parameter below:
 * <p>
 * <code>&lt listener class-name="PTRUpdater" /&gt</code>
 *
 * @see <a href="http://testng.org/doc/documentation-main.html#testng-listeners">http://testng
 *      .org/doc/documentation-main.html#testng-listeners</a>
 */
public class PTRUpdater implements ITestListener {

  private static final Logger LOGGER = Logger.getLogger( PTRUpdater.class );
  private static ResourceBundle testRailInfo;
  private static String reportLink = "";
  private ConcurrentHashMap<String, ArrayList<ITestResult>> testResults =
      new ConcurrentHashMap<String, ArrayList<ITestResult>>();
  private String browser;
  private String locale;
  private String app_version;
  private PTRTestPlan testPlan = new PTRTestPlan();
  private ConcurrentHashMap<String, PTRTestInstance> testInstance = new ConcurrentHashMap<String, PTRTestInstance>();
  private long instanceIdParam = -1;

  public PTRUpdater() {

    try {

      testRailInfo = ResourceBundle.getBundle( "_testrail" );

      // Retrieve properties from _config.properties file.
      browser = ResourceBundle.getBundle( "local" ).getString( "selenide.browser" );
      locale = ResourceBundle.getBundle( "local" ).getString( "locale" );
      app_version = ResourceBundle.getBundle( "local" ).getString( "app_version" );

      // ======= If running on Jenkins, overwrite values from _config.properties. =======
      String browser = System.getProperty( "browser" );
      if ( !Strings.isNullOrEmpty( browser ) ) {
        this.browser = browser;
      }

      String locale = System.getProperty( "locale" );
      if ( !Strings.isNullOrEmpty( locale ) ) {
        this.locale = locale;
      }

      String app_version = System.getProperty( "app_version" );
      if ( !Strings.isNullOrEmpty( app_version ) ) {
        this.app_version = app_version;
      }

      if ( Strings.isNullOrEmpty( this.app_version ) ) {
        throw new NullPointerException(
            "TestRail Updater Instantiation Error: An 'app_version' is a required value. Please check if the value is "
                + "missing in the Jenkins properties or if locally run check the_testrail.properties file!" );
      }

      // Assign test instance id if one exists, otherwise it is not a required parameter.
      if ( System.getProperty( "testrail_instance_id" ) != null
          && !System.getProperty( "testrail_instance_id" ).isEmpty() ) {
        instanceIdParam = Long.valueOf( System.getProperty( "testrail_instance_id" ) );
      }

      if ( System.getProperty( "report_url" ) != null && !System.getProperty( "report_url" ).isEmpty() ) {
        reportLink = System.getProperty( "report_url" );
      }

    } catch ( NullPointerException e ) {
      Assert.fail( e.getMessage(), e );
    } catch ( MissingResourceException e ) {
      Assert.fail( "Can't find _config resource bundle!", e );
    } catch ( NumberFormatException e ) {
      Assert.fail(
        "Property testrail_instance_id cannot be converted to a long! Make sure it doesn't contains characters.", e );
    }

  }

  @Override
  public void onTestStart( ITestResult result ) {
  }

  @Override
  public void onTestSuccess( ITestResult result ) {
    onTestCompleted( result );
  }

  @Override
  public void onTestFailure( ITestResult result ) {
    onTestCompleted( result );
  }

  @Override
  public void onTestSkipped( ITestResult result ) {
    onTestCompleted( result );
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage( ITestResult result ) {
    onTestCompleted( result );
  }

  /**
   * The method that gets called right BEFORE all of the included methods or classes in a <test> suite run and AFTER any
   * TestNG related configurations are processed for that <test> suite.
   *
   * @param context
   *          The TestNG provided test context data.
   */
  @Override
  public synchronized void onStart( ITestContext context ) {

    // Only proceed if the suite is not in a failed state
    if ( !context.getSuite().getSuiteState().isFailed() ) {

      // If we're given an instance id, we can skip all processes in between and just get the exact instance.
      if ( instanceIdParam > 0 ) {
        LOGGER
            .info( "TestRail instance id '" + instanceIdParam + "' provided. Attempting to find the test instance..." );
        testInstance.put( context.getName(), new PTRTestInstanceService().getTestInstance( instanceIdParam ) );
        if ( testInstance.get( context.getName() ).getInstanceId() > 0 ) {
          LOGGER
              .info( "TestRail instance id '" + instanceIdParam + "' found! Skipping the rest of the search process." );
        }
      } else {

        String projectTag = getTestRailInfo( "testrail_project_id", false );
        String milestoneTag = getTestRailInfo( "testrail_milestone_id", false );
        String baseTestPlanName = getTestRailInfo( "testrail_testplan_name", false );
        String testPlanDescription = getTestRailInfo( "testrail_testplan_description", true );
        boolean isCreateNewTestPlan = Boolean.valueOf( getTestRailInfo( "testrail_create_new_testplan", false ) );
        boolean isCreateNewTestPlanEntry =
            Boolean.valueOf( getTestRailInfo( "testrail_create_new_testplan_entry", false ) );
        boolean isCreateNewTestInstance =
            Boolean.valueOf( getTestRailInfo( "testrail_create_new_test_instance", false ) );
        boolean isSkipped = false;
        long caseIdParam = -1;

        // Check if the case id parameter is present in the TestNG xml file.
        if ( context.getCurrentXmlTest().getAllParameters().get( "testrail_case_id" ) != null
            && !context.getCurrentXmlTest().getAllParameters().get( "testrail_case_id" ).isEmpty() ) {
          caseIdParam = Long.valueOf( context.getCurrentXmlTest().getAllParameters().get( "testrail_case_id" ) );
        }

        if ( caseIdParam > 0 ) {
          PTRProjectService projectService = new PTRProjectService();
          PTRProject project = projectService.getProject( Long.valueOf( projectTag ) );

          if ( project.getProjectId() > 0 ) {

            PTRMilestoneService milestoneService = new PTRMilestoneService();

            PTRMilestone milestone = milestoneService.getMilestone( Long.valueOf( milestoneTag ) );

            if ( milestone.getMilestoneId() > 0 && milestone.isMilestoneStarted() ) {

              // Set test plan name
              String testPlanName = milestone.getMilestoneName() + " " + baseTestPlanName;
              boolean isTestPlanFound = false;

              // Gather current test configuration data
              PTRConfigService configService = new PTRConfigService( project.getProjectId(), browser, locale );
              ArrayList<Long> activeConfigIds = configService.getActiveTestConfigurationIds();

              PTRTestPlanService testPlanService = new PTRTestPlanService();
              ArrayList<PTRTestPlan> existingTestPlans =
                  testPlanService.getPlansByMilestones( project.getProjectId(), false, milestone.getMilestoneId() );

              if ( existingTestPlans.size() > 0 ) {
                for ( PTRTestPlan existingTestPlan : existingTestPlans ) {
                  if ( existingTestPlan.getPlanName().equals( testPlanName ) ) {
                    testPlan = testPlanService.getPlan( existingTestPlan.getTestPlanId() );
                    isTestPlanFound = true;
                    break;
                  }
                }
              }

              // Only create a new test plan if allowed and one does not exist
              if ( !isTestPlanFound ) {
                LOGGER.info(
                  "Test plan '" + testPlanName + "' was not found in the '" + project.getProjectName() + "' project!" );

                if ( isCreateNewTestPlan ) {
                  LOGGER.info( "Creating new test plan called '" + testPlanName + "' in Project '"
                      + project.getProjectName() + "' under Milestone: " + milestone.getMilestoneName() );

                  testPlan =
                      testPlanService.addPlan( project.getProjectId(), milestone.getMilestoneId(), testPlanName,
                        testPlanDescription );

                  if ( testPlan.getTestPlanId() > 0 ) {
                    isTestPlanFound = true;
                  }
                } else {
                  LOGGER.info( "Skipping automatic test plan creation. Please manually create the desired test plan!" );
                  isSkipped = true;
                }
              } else {
                LOGGER.info( "Test plan '" + testPlanName + "' was found in Project: " + project.getProjectName() );
              }

              if ( isTestPlanFound ) {

                PTRTestCaseService testCaseService = new PTRTestCaseService();
                PTRTestCase testCase = testCaseService.getCase( caseIdParam );

                if ( testCase.getCaseId() > 0 ) {

                  // Get any existing entries
                  ArrayList<PTRTestEntry> existingTestEntries = testPlan.getEntries();
                  PTRTestEntry foundTestEntry = new PTRTestEntry();
                  PTRTestRun foundTestRun = new PTRTestRun();

                  // Get the section data the test case belongs to.
                  PTRSectionService sectionService = new PTRSectionService();
                  PTRSection section = sectionService.getSection( testCase.getSectionId() );

                  boolean isEntryFound = false;
                  boolean isRunFound = false;

                  if ( existingTestEntries.size() > 0 ) {
                    for ( PTRTestEntry existingTestEntry : existingTestEntries ) {
                      if ( section.getName().equals( existingTestEntry.getName() ) ) {
                        if ( existingTestEntry.getRuns().size() > 0 ) {

                          // Check for matching test run configurations in the test entry found.
                          for ( PTRTestRun existingTestRun : existingTestEntry.getRuns() ) {

                            ArrayList<Long> existingConfigIds = existingTestRun.getConfigurationIds();

                            if ( CollectionUtils.isEqualCollection( activeConfigIds, existingConfigIds ) ) {
                              isRunFound = true;
                              foundTestRun = existingTestRun;
                              break;
                            }
                          }
                        }

                        // If a run is found, the entry has to have been be found as well.
                        if ( isRunFound ) {
                          isEntryFound = true;
                          foundTestEntry = existingTestEntry;
                          break;
                        }
                      }
                    }
                  }

                  if ( !isEntryFound ) {
                    LOGGER.info( "Test Entry '" + section.getName() + "' was not found in Test Plan: " + testPlanName );

                    if ( isCreateNewTestPlanEntry ) {

                      if ( Boolean.valueOf( getTestRailInfo( "testrail_testplan_entry_group_runs", false ) ) ) {
                        LOGGER.info( "Creating a new Test Entry for '" + section.getName() + "' in Test Plan '"
                            + testPlanName + "' with all supported run configurations!" );
                      } else {
                        LOGGER.info( "Creating a single Test Entry called '" + section.getName() + "' in Test Plan '"
                            + testPlanName + "' with the following run configuration: "
                            + configService.getActiveTestConfigurationName() );
                      }

                      // Build new empty test entry
                      PTRTestEntry testEntry = new PTRTestEntry();
                      testEntry.setName( section.getName() );
                      testEntry.setSuiteId( testCase.getSuiteId() );
                      testEntry.setDescription( section.getDescription() );

                      // Add new test entry containing a run with a configuration that was not ignored.
                      foundTestEntry =
                          testPlanService.addPlanEntry( testPlan.getTestPlanId(), testCase.getCaseId(), testEntry );

                      if ( !foundTestEntry.getUniqueId().equals( "" ) ) {
                        isEntryFound = true;

                        if ( foundTestEntry.getRuns().size() > 0 ) {

                          // Check for matching test run configurations in the test entry found.
                          for ( PTRTestRun existingTestRun : foundTestEntry.getRuns() ) {

                            ArrayList<Long> existingConfigIds = existingTestRun.getConfigurationIds();

                            if ( CollectionUtils.isEqualCollection( activeConfigIds, existingConfigIds ) ) {
                              isRunFound = true;
                              foundTestRun = existingTestRun;
                              break;
                            }
                          }
                        }
                      }

                      // Refresh the test plan by calling the API again to get the updated information after creation
                      testPlan = testPlanService.getPlan( testPlan.getTestPlanId() );
                    } else {
                      LOGGER.info(
                        "Skipping automatic test entry creation. Please manually create the desired test entry(ies)!" );
                      isSkipped = true;
                    }
                  }

                  // Only proceed if both the entry and the run are found. They cannot exist without each other.
                  if ( isEntryFound && isRunFound ) {

                    /*
                     * Ben Freed - As of 6/20/2017 An individual test run can NOT be edited independently if it belongs
                     * to a test plan. This is by design right now according to Gurock developers. See API error below:
                     * TestRail API returned HTTP 403(“This operation is not allowed. The test run belongs to a test
                     * plan and cannot be edited independently.”) Because of this, we need to first check if the test
                     * instance we want exists in the run configuration we're testing against.
                     */
                    LOGGER.info( "Test Run '" + foundTestRun.getFullName() + "' was found in Test Entry: "
                        + foundTestEntry.getName() );

                    foundTestRun.setTestInstances();
                    ArrayList<PTRTestInstance> testInstances = foundTestRun.getTestInstances();
                    boolean isTestInstanceFound = false;

                    if ( testInstances.size() > 0 ) {
                      for ( PTRTestInstance testInstance : testInstances ) {
                        if ( testCase.getCaseId() == testInstance.getRelatedCaseId() ) {
                          this.testInstance.put( context.getName(), testInstance );
                          isTestInstanceFound = true;
                          break;
                        }
                      }
                    }

                    // If not, we have to update the whole test plan entry to contain the new case the test instance
                    // will be created from. This will create a new test instance in every possible run
                    // configuration under that test entry (which should only be one unless more were manually added).
                    if ( !isTestInstanceFound ) {
                      LOGGER.info( "A Test Instance for '" + testCase.getTitle() + "' does not exist in Test Run: "
                          + foundTestRun.getFullName() );

                      if ( isCreateNewTestInstance ) {
                        LOGGER.info( "Creating new test instances from referenced case id '" + caseIdParam
                            + "' inside Test Plan '" + testPlanName + "' under the Test Entry '"
                            + foundTestEntry.getName() + "' with configurations for: " + foundTestRun.getConfigName() );

                        foundTestEntry =
                            testPlanService.updatePlanEntry( testPlan.getTestPlanId(), testCase.getCaseId(),
                              foundTestEntry, foundTestRun );

                        if ( !foundTestEntry.getUniqueId().equals( "" ) ) {
                          foundTestRun.setTestInstances();
                          testInstances = foundTestRun.getTestInstances();

                          if ( testInstances.size() > 0 ) {
                            for ( PTRTestInstance testInstance : testInstances ) {
                              if ( testCase.getCaseId() == testInstance.getRelatedCaseId() ) {
                                this.testInstance.put( context.getName(), testInstance );
                                isTestInstanceFound = true;
                                break;
                              }
                            }
                          } else {
                            LOGGER.warn( "No test instances exist." );
                          }
                        } else {
                          LOGGER.warn( "The test entry's unique ID does not exist." );
                        }
                      } else {
                        LOGGER
                            .info( "Skipping automatic test instance creation. Please manually create the desired test "
                                + "instance!" );
                        isSkipped = true;
                      }
                    }

                    if ( isTestInstanceFound ) {
                      LOGGER.info( "Test instance id '" + testInstance.get( context.getName() ).getInstanceId()
                          + "' was found. Proceeding to run test instance '"
                          + testInstance.get( context.getName() ).getInstanceTitle() + "' under Test Run: "
                          + foundTestRun.getFullName() );

                      testResults.put( context.getName(), new ArrayList<ITestResult>() );
                    } else {
                      if ( !isSkipped ) {
                        Assert.fail( "Failed to update the existing Test Entry '" + foundTestRun.getFullName()
                            + "' with the new test case id: " + caseIdParam );
                      }
                    }

                  } else {
                    if ( !isSkipped ) {
                      Assert.fail( "A matching test plan entry for '" + section.getName() + " "
                          + configService.getActiveTestConfigurationName() + "' could not be found in the test plan: "
                          + testPlanName );
                    }
                  }
                } else {
                  Assert.fail( "Test case id '" + caseIdParam
                      + "' could not be found or does not exist! A test case id is required in order to construct a new "
                      + "test instance from it!" );
                }
              } else {
                if ( !isSkipped ) {
                  Assert.fail( "Test plan '" + testPlanName + "' could not be found or does not exist!" );
                }
              }
            } else {
              Assert.fail( "Milestone id '" + milestoneTag
                  + "' could not be retrieved, does not exist, or has not been started yet!" );
            }
          } else {
            Assert.fail( "Project id '" + projectTag + "' could not be found or does not exist!" );
          }
        } else {
          Assert.fail( "A TestRail case id has not been specified in the parameters of test suite: "
              + context.getCurrentXmlTest().getName() );
        }
      }
    } else {
      LOGGER.error( "The current test suite '" + context.getCurrentXmlTest().getName()
          + "' was in a failed state. Skipping all TestRail related processes in order for the test to continue on!" );
    }
  }

  /**
   * The method that gets called AFTER all of the included methods or classes in a <test> suite run and AFTER any TestNG
   * related configurations are processed for that <test> suite.
   *
   * @param context
   *          The TestNG provided test context data.
   */
  @Override
  public synchronized void onFinish( ITestContext context ) {

    // Only proceed if the suite is not in a failed state and we have a test instance.
    if ( !context.getSuite().getSuiteState().isFailed() && testInstance.get( context.getName() ).getInstanceId() > 0 ) {

      PTRTestResultService testResultsService = new PTRTestResultService();
      PTRStatusService statusService = new PTRStatusService();
      PTRTestResult testResult = new PTRTestResult();

      testResult.setStatusId( statusService.calculateStatus( testResults.get( context.getName() ) ) );

      testResult.setComment( testResultsService.buildResultComment( context.getCurrentXmlTest().getName(),
        testResults.get( context.getName() ) ) );

      testResult.setTimeElapsed(
        testResultsService.calculateElapsedTime( context.getStartDate().getTime(), context.getEndDate().getTime() ) );

      testResult.setBuildVersion( app_version );

      testResult.setDefects( testResultsService.parseAllDefects( testResults.get( context.getName() ) ) );

      LOGGER.info(
        "Posting test result(s) into Test Instance id '" + testInstance.get( context.getName() ).getInstanceId()
            + "' called '" + testInstance.get( context.getName() ).getInstanceTitle() + "' under Test Run id: "
            + testInstance.get( context.getName() ).getRunId() );

      testResultsService.addResult( testInstance.get( context.getName() ).getInstanceId(), testResult );
    } else {
      LOGGER.error(
        "Skipping posting results to TestRails. The test suite was either in a failed state or there was no valid "
            + "test instance information." );
    }

    // Clear the test instance and results for the next test that will start if any.
    testInstance.put( context.getName(), new PTRTestInstance() );
    testResults.remove( testInstance.get( context.getName() ) );
    testInstance.remove( context );
  }

  /**
   * A static method used to retrieve any TestRail property information.
   *
   * @param key
   *          The key containing the value.
   * @return The value of the given key.
   */
  static String getTestRailInfo( String key, boolean isEmptyAllowed ) {
    // Retrieve the system property and default to the _testrail.properties file if the system property does not exist.
    String value = System.getProperty( key );
    if ( Strings.isNullOrEmpty( value ) ) {
      value = testRailInfo.getString( key );
    }

    if ( Strings.isNullOrEmpty( value ) && !isEmptyAllowed ) {
      throw new NullPointerException(
          "Key - " + key + " - does not exists in TestRail config file! Key value is null." );
    }

    return value;
  }

  /**
   * A static method used to retrieve the Jenkins ETAF Report URL.
   *
   * @return The value of the system property.
   */
  static String getReportLink() {
    return reportLink;
  }

  /**
   * A helper method used to reconfigure the results after an annotated test method is run. If the parameter for
   * testrail_results_record_retries is false, any previously stored result of that test method in will be removed favor
   * of the current invocation's result.
   *
   * @param result
   *          An ITestResult object from any type of completed test.
   */
  private void onTestCompleted( ITestResult result ) {

    result.setAttribute( "invocationId", result.getMethod().getCurrentInvocationCount() );

    if ( Boolean.valueOf( getTestRailInfo( "testrail_results_record_retries", false ) ) ) {

      getTestResults( result ).add( result );
    } else {

      ITestResult existingTestToRemove = null;

      for ( ITestResult testResult : getTestResults( result ) ) {
        if ( testResult.getMethod() == result.getMethod() ) {
          existingTestToRemove = testResult;
          break;
        }
      }

      if ( existingTestToRemove != null ) {
        getTestResults( result ).remove( existingTestToRemove );
      }

      getTestResults( result ).add( result );
    }
  }

  /**
   * Retrieves the list of test results for the test that the specified result belongs to.
   * 
   * @param result
   *          The instance of ITestResult. This is a single result that also contains the ITestContext that the list of
   *          results can be retrieved from.
   * @return Returns ArrayList<ITestResult> that contains the list of results for the test.
   */
  private ArrayList<ITestResult> getTestResults( ITestResult result ) {
    ArrayList<ITestResult> results = testResults.get( result.getTestContext().getName() );
    if ( results == null ) {
      throw new RuntimeException( "Unable to get 'testResults' for: " + result.getTestContext().getName() );
    }
    return results;
  }
}
