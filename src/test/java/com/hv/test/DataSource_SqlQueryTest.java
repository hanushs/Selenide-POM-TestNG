package com.hv.test;


import com.codeborne.selenide.testng.annotations.Report;
import com.hv.pages.PAZ.AnalyserReportPage;
import com.hv.pages.PAZ.ExportToCsvPage;
import com.hv.pages.base.HomePage;
import com.hv.pages.base.IReportOptions;
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

    @BeforeClass
    public void login() {
        homePage = loginPage.loginWithCredentials(getTestData().get("UserName"), getTestData().get("UserPassword"));
    }
    

}

















