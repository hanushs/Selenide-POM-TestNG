package com.hv.test;


import com.codeborne.selenide.testng.annotations.Report;
import com.hv.pages.DataSources.DataSourceWizardPage;
import com.hv.pages.DataSources.ISQLQuery;
import com.hv.pages.DataSources.ManageDataSourcesPage;
import com.hv.pages.Utils.DataParser;
import com.hv.pages.base.HomePage;
import com.hv.pages.base.MenuPage;
import com.hv.pages.base.LoginPage;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Selenide.open;
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
    @Parameters({"dataFilePath"})
    public void login(String dataFilePath, final ITestContext testContext) {
        //parsing data for TestName to be used in test class.
        testData = DataParser.getTestData(dataFilePath, testContext.getName());
        LOGGER.info("Opening " + baseUrl + " in " + browser + " browser");
        loginPage = open(baseUrl, LoginPage.class);
        menuPage = loginPage.loginAsEvaluator(LoginPage.USER.ADMIN);
    }

    //Steps: 1-2,7
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
    //Steps: 8
    @Test(dependsOnMethods = "createDataSource")
    public void verifyDatasouceCreated() {
        HomePage homePage = page(HomePage.class);
        manageDataSourcesPage = homePage.openManageDatasource();
        manageDataSourcesPage.verifyDScreated(getTestData().get("DataSourceName"));
    }

}

















