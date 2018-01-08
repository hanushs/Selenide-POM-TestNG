package com.hv.test;


import com.codeborne.selenide.testng.annotations.Report;
import com.hv.pages.DataSources.DataSourceWizardPage;
import com.hv.pages.DataSources.ISQLQuery;
import com.hv.pages.DataSources.ManageDataSourcesPage;
import com.hv.pages.base.HomePage;
import com.hv.pages.base.MenuPage;
import com.hv.pages.base.LoginPage;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.Selenide.sleep;

/**
 * Created by shanush on 12/1/2017.
 */

// Automated Test: http://testrail.pentaho.com/index.php?/cases/view/65409

@Report
public class DataSource_SqlQueryTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(DataSource_SqlQueryTest.class);

    private MenuPage menuPage;
    private ISQLQuery isqlQuery;
    private DataSourceWizardPage dataSourceWizardPage;
    private ManageDataSourcesPage manageDataSourcesPage;

    @BeforeClass
    public void login() {
        menuPage = loginPage.loginAsEvaluator(LoginPage.USER.ADMIN);
    }

    //steps 1-2,7
    @Test
    public void createDataSource() {
        dataSourceWizardPage = menuPage.createNewDatasource();
        dataSourceWizardPage.enterDatasourceName(getTestData().get("DataSourceName"));
        isqlQuery = (ISQLQuery) dataSourceWizardPage.selectSourceType(DataSourceWizardPage.DataSourceType.valueOf(getTestData().get("DataSourceType")));
        isqlQuery.createNewConnection(getTestData().get("DBConnectionName"),
                getTestData().get("DBType"),
                getTestData().get("DBConnectionHostName"),
                getTestData().get("DBConnectionUserName"),
                getTestData().get("DBConnectionDBName"));
        isqlQuery.addQuery(getTestData().get("Query"));
        dataSourceWizardPage.verifyButtonState();
        dataSourceWizardPage.finishWizard();
        dataSourceWizardPage.setModel(DataSourceWizardPage.DSMODEL.DEFAULT);
        sleep(2000);
    }

    @Test(dependsOnMethods = "createDataSource")
    public void verifyDatasouceCreated() {
        HomePage homePage = page(HomePage.class);
        manageDataSourcesPage = homePage.openManageDatasource();
        manageDataSourcesPage.verifyDScreated(getTestData().get("DataSourceName"));
    }

}

















