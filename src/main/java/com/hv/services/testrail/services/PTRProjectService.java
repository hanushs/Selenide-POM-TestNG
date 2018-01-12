package com.hv.services.testrail.services;

import com.hv.services.testrail.PTRClient;
import com.hv.services.testrail.objects.PTRProject;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;

import java.util.ArrayList;

/**
 * A Pentaho TestRail project class used for handling project data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-projects">http://docs.gurock
 * .com/testrail-api2/reference-projects</a>
 */
public class PTRProjectService extends PTRClient {

  private static final Logger LOGGER = Logger.getLogger( PTRProjectService.class );

  /**
   * A method used to retrieve the data for a single project through the TestRail API using the project's id
   * and then runs {@link #createPTRProject(JSONObject)} to create a project object from that information.
   *
   * @param projectId The assigned project id for the desired project.
   * @return {@link PTRProject} - A project object, empty if nothing is returned by the API.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-projects#get_project">http://docs.gurock
   * .com/testrail-api2/reference-projects#get_project</a>
   */
  public PTRProject getProject( long projectId ) {

    PTRProject project = new PTRProject();

    String getRequest = "get_project/" + projectId;
    Object response = sendGet( getRequest );

    if ( response instanceof JSONObject ) {
      project = createPTRProject( (JSONObject) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return project;
  }

  /**
   * A method used to return an ArrayList of all TestRail projects and their basic information.
   * <p>
   * If specific projects such as completed only or active only are desired, use {@link #getProjects(boolean)} instead.
   *
   * @return An ArrayList of the projects with information or an empty ArrayList if nothing is returned by the API.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-projects#get_projects">http://docs.gurock
   * .com/testrail-api2/reference-projects#get_projects</a>
   */
  public ArrayList<PTRProject> getProjects() {

    ArrayList<PTRProject> projects = new ArrayList<>();

    String getRequest = "get_projects";

    Object response = sendGet( getRequest );

    if ( response instanceof JSONArray ) {
      projects = createPTRProjects( (JSONArray) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return projects;
  }

  /**
   * A method used to return an ArrayList of completed or active TestRail projects and their basic information.
   * <p>
   * If all projects are desired regardless of completion or active status, use {@link #getProjects()} instead.
   *
   * @param isCompleted - True if wanting only completed projects, false for only active projects
   * @return An ArrayList of the projects with information or an empty ArrayList if nothing is returned by the API. It
   * will also be empty if there are no projects with the desired completion or active status.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-projects#get_projects">http://docs.gurock
   * .com/testrail-api2/reference-projects#get_projects</a>
   */
  public ArrayList<PTRProject> getProjects( boolean isCompleted ) {

    ArrayList<PTRProject> projects = new ArrayList<>();

    int filter = 0;
    if ( isCompleted ) {
      filter = 1;
    }

    String getRequest = "get_projects&is_completed=" + filter;
    Object response = sendGet( getRequest );

    if ( response instanceof JSONArray ) {
      projects = createPTRProjects( (JSONArray) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return projects;
  }

  /**
   * A helper method used to convert a JSONArray of projects into an ArrayList of {@link PTRProject} objects.
   *
   * @param jsonArray A JSONArray of projects to convert.
   * @return ArrayList<PTRProject> - An array list of {@link PTRProject} objects.
   */
  private ArrayList<PTRProject> createPTRProjects( JSONArray jsonArray ) {

    ArrayList<PTRProject> projects = new ArrayList<>();

    if ( jsonArray.size() > 0 ) {

      for ( Object obj : jsonArray ) {
        projects.add( createPTRProject( (JSONObject) obj ) );
      }

    } else {
      Assert.fail( "The given JSONArray does not any data for projects. Returning empty array list." );
    }

    return projects;
  }

  /**
   * A helper method used to convert a single Object into a single {@link PTRProject}.
   *
   * @param jsonObj The project object to convert.
   * @return PTRProject - A converted {@link PTRProject} object.
   */
  private PTRProject createPTRProject( JSONObject jsonObj ) {

    PTRProject project = new PTRProject();

    if ( jsonObj != null && !jsonObj.isEmpty() ) {

      long projectId = -1;

      // Check if the project has an id
      if ( jsonObj.containsKey( "id" ) && jsonObj.get( "id" ) != null ) {
        project.setProjectId( (long) jsonObj.get( "id" ) );
        projectId = project.getProjectId();
      } else {
        Assert.fail( "The project provided is missing a project id!" );
      }

      // Check if the project has an announcement (description)
      if ( jsonObj.containsKey( "announcement" ) && jsonObj.get( "announcement" ) != null ) {
        project.setDescription( (String) jsonObj.get( "announcement" ) );
      } else {
        LOGGER
          .debug( "Project id '" + projectId + "' does not have an announcement (description) associated with it!" );
      }

      // Check if the project has a date it was completed on
      if ( jsonObj.containsKey( "completed_on" ) && jsonObj.get( "completed_on" ) != null ) {
        project.setCompletedOn( (long) jsonObj.get( "completed_on" ) );
      } else {
        LOGGER.debug( "Project id '" + projectId + "' does not have a date set for when it was completed or "
          + "it is still marked as an active project!" );
      }

      // Check if the project has been marked as completed or not.
      if ( jsonObj.containsKey( "is_completed" ) && jsonObj.get( "is_completed" ) != null ) {
        project.setIsProjectCompleted( (boolean) jsonObj.get( "is_completed" ) );
      } else {
        LOGGER.debug( "Project id '" + projectId + "' has no indication as to whether or not it has been completed!" );
      }

      // Check if the project has a name
      if ( jsonObj.containsKey( "name" ) && jsonObj.get( "name" ) != null ) {
        project.setProjectName( (String) jsonObj.get( "name" ) );
      } else {
        LOGGER.debug( "Project id '" + projectId + "' is missing a name!" );
      }

      // Check if the project has elected to show its announcement (description) or not.
      if ( jsonObj.containsKey( "show_announcement" ) && jsonObj.get( "show_announcement" ) != null ) {
        project.setIsShowingDescription( (boolean) jsonObj.get( "show_announcement" ) );
      } else {
        LOGGER
          .debug( "Project id '" + projectId + "' has no indication if it is showing the announcement (description)!" );
      }

      // Check if the project has a
      if ( jsonObj.containsKey( "suite_mode" ) && jsonObj.get( "suite_mode" ) != null ) {
        project.setSuiteMode( (long) jsonObj.get( "suite_mode" ) );
      } else {
        LOGGER.debug(
          "Project id '" + projectId + "' has not declared a suite mode type!"
            + " (1 for single suite, 2 for single suite + baselines, 3 for multiple suites)" );
      }

      // Check if the project has a URL
      if ( jsonObj.containsKey( "url" ) && jsonObj.get( "url" ) != null ) {
        project.setProjectUrl( (String) jsonObj.get( "url" ) );
      } else {
        LOGGER.debug( "Project id '" + projectId + "' does not have a URL associated with it!" );
      }

    } else {
      Assert.fail( "The project object provided was empty! Could not convert to a PTRProject object!" );
    }

    return project;
  }
}
