package com.hv.pages.DataSources;

/**
 * Created by shanush on 1/3/2018.
 */
public interface IBaseSourceType {

    void createNewConnection(String databaseConnectionName, String databaseType, String databaseConnectionHostName, String databaseConnectionUserName, String databaseConnectionDBName);

    void addQuery(String query);
}
