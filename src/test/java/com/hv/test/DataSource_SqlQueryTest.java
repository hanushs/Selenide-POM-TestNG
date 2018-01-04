package com.hv.test;


import com.codeborne.selenide.testng.annotations.Report;
import com.hv.pages.DataSources.DataSourceWizardPage;
import com.hv.pages.DataSources.ISQLQuery;
import com.hv.pages.PAZ.AnalyserReportPage;
import com.hv.pages.PAZ.ExportToCsvPage;
import com.hv.pages.base.HomePage;
import com.hv.pages.base.IReportOptions;
import com.hv.pages.base.LoginPage;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.sleep;
import static com.hv.pages.PAZ.AnalyserReportPage.SORT.HIGHtoLOW;
import static com.hv.pages.base.FilePage.FILETYPE;

/**
 * Created by shanush on 12/1/2017.
 */

// Automated Test: http://testrail.pentaho.com/index.php?/cases/view/65409

@Report
public class DataSource_SqlQueryTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(DataSource_SqlQueryTest.class);

    private HomePage homePage;
    private ISQLQuery isqlQuery;
    private DataSourceWizardPage dataSourceWizardPage;

    @BeforeClass
    public void login() {
        homePage = loginPage.loginAsEvaluator(LoginPage.USER.ADMIN);
    }

    @Test
    public void createDataSource() {
        dataSourceWizardPage = homePage.createNewDatasource();
        dataSourceWizardPage.enterDatasourceName(getTestData().get("DataSourceName"));
        isqlQuery = (ISQLQuery) dataSourceWizardPage.selectSourceType(DataSourceWizardPage.DataSourceType.valueOf(getTestData().get("DataSourceType")));
        isqlQuery.createNewConnection(getTestData().get("DBConnectionName"),
                getTestData().get("DBType"),
                getTestData().get("DBConnectionHostName"),
                getTestData().get("DBConnectionUserName"),
                getTestData().get("DBConnectionDBName"));
        sleep(2000);
        isqlQuery.addQuery(getTestData().get("Query"));
        dataSourceWizardPage.verifyButtonState();
    }


}

















