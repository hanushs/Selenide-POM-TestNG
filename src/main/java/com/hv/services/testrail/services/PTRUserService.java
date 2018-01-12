package com.hv.services.testrail.services;

import com.hv.services.testrail.PTRClient;
import com.hv.services.testrail.objects.PTRUser;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;

import java.util.ArrayList;

/**
 * A Pentaho TestRail user class for handling user data from the TestRail API.
 *
 * @see <a href="http://docs.gurock.com/testrail-api2/reference-users">http://docs.gurock
 * .com/testrail-api2/reference-users</a>
 */
public class PTRUserService extends PTRClient {

  private static final Logger LOGGER = Logger.getLogger( PTRUserService.class );

  /**
   * A method used to retrieve a single user's basic information through the TestRail API using their user id number
   * and then runs {@link #createPTRUser(JSONObject)} to create a user object from that information.
   *
   * @param userId The assigned user id in TestRails for the desired user.
   * @return A user with information or an empty user if nothing is returned by the API.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-users#get_user">http://docs.gurock
   * .com/testrail-api2/reference-users#get_user</a>
   */
  public PTRUser getUser(long userId ) {

    String getRequest = "get_user/" + userId;
    Object response = sendGet( getRequest );
    PTRUser user = new PTRUser();

    if ( response instanceof JSONObject ) {
      user = createPTRUser( (JSONObject) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return user;
  }

  /**
   * A method used to retrieve a single user's basic information through the TestRail API using their email address
   * and then runs {@link #createPTRUser(JSONObject)} to create a user object from that information.
   *
   * @param emailAddress The desired user's email address in TestRails.
   * @return A user with information or an empty user if nothing is returned by the API.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-users#get_user_by_email">http://docs.gurock
   * .com/testrail-api2/reference-users#get_user_by_email</a>
   */
  public PTRUser getUserByEmail( String emailAddress ) {

    String getRequest = "get_user_by_email&email=" + emailAddress;
    Object response = sendGet( getRequest );
    PTRUser user = new PTRUser();

    if ( response instanceof JSONObject ) {
      user = createPTRUser( (JSONObject) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return user;
  }

  /**
   * A method used to return a JSONArray of all the TestRails users and their basic information.
   *
   * @return An ArrayList of the users with information or an empty ArrayList if nothing is returned by the API.
   * @see <a href="http://docs.gurock.com/testrail-api2/reference-users#get_users">http://docs.gurock
   * .com/testrail-api2/reference-users#get_users</a>
   */
  public ArrayList<PTRUser> getUsers() {

    ArrayList<PTRUser> users = new ArrayList<>();

    String getRequest = "get_users";
    Object response = sendGet( getRequest );

    if ( response instanceof JSONArray ) {
      users = createPTRUsers( (JSONArray) response );
    } else {
      Assert.fail( getNotAJsonError( getRequest, response.toString() ) );
    }

    return users;
  }

  /**
   * A helper method used to convert a JSONArray of users into an ArrayList of {@link PTRUser} objects.
   *
   * @param jsonArray A JSONArray of users to convert.
   * @return An ArrayList of the users.
   */
  private ArrayList<PTRUser> createPTRUsers( JSONArray jsonArray ) {

    ArrayList<PTRUser> users = new ArrayList<>();

    if ( jsonArray.size() > 0 ) {

      for ( Object obj : jsonArray ) {
        users.add( createPTRUser( (JSONObject) obj ) );
      }

    } else {
      Assert.fail( "The given JSONArray does not contain any data for users. Returning empty array list." );
    }

    return users;
  }

  /**
   * A helper method used to convert a single Object into a single {@link PTRUser}.
   *
   * @param jsonObj The object to convert.
   * @return The converted user.
   */
  private PTRUser createPTRUser( JSONObject jsonObj ) {

    PTRUser user = new PTRUser();

    if ( jsonObj != null && !jsonObj.isEmpty() ) {

      long userId = -1;

      // Check if the user account has an id
      if ( jsonObj.containsKey( "id" ) && jsonObj.get( "id" ) != null ) {
        user.setUserId( (long) jsonObj.get( "id" ) );
        userId = user.getUserId();
      } else {
        Assert.fail( "User object received does not have an id associated with it!" );
      }

      // Check if the user account has an email address
      if ( jsonObj.containsKey( "email" ) && jsonObj.get( "email" ) != null ) {
        user.setEmailAddress( (String) jsonObj.get( "email" ) );
      } else {
        LOGGER.debug( "User id '" + userId + "' does not have an email address associated with it!" );
      }

      // Check if the user account is active
      if ( jsonObj.containsKey( "is_active" ) && jsonObj.get( "is_active" ) != null ) {
        user.setActiveStatus( (boolean) jsonObj.get( "is_active" ) );
      } else {
        LOGGER.debug( "User id '" + userId + "' does not appear to be active!" );
      }

      // Check if the user account has a full name
      if ( jsonObj.containsKey( "name" ) && jsonObj.get( "name" ) != null ) {
        user.setFullname( (String) jsonObj.get( "name" ) );
      } else {
        LOGGER.debug( "User id '" + userId + "' does not have a full name associated with it!" );
      }

    } else {
      Assert.fail( "The user object provided was empty! Could not convert to a PTRUser object!" );
    }

    return user;
  }

}
