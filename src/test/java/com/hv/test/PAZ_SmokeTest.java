package com.hv.test;


import com.codeborne.selenide.testng.annotations.Report;

import static com.codeborne.selenide.Selenide.sleep;
import  static com.hv.pages.FilePage.FILETYPE;

import com.hv.pages.AnalyserReportPage;
import com.hv.pages.HomePage;
import com.hv.services.Analyzer;
import com.hv.services.Home;
import com.hv.services.Login;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Created by pshynin on 12/1/2017.
 */
@Report
public class PAZ_SmokeTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(PAZ_SmokeTest.class);
    private Login loginService;
    private HomePage homePage;
    private Home homeService;
    private AnalyserReportPage pazReport;
    private Analyzer analyzerService;


    @Parameters({"user", "password"})
    @BeforeClass
    public void login(String user, String password) {
        loginService = new Login(loginPage);
        homePage = loginService.loginWithCredentials(user, password);
    }


    @Test
    @Parameters({"datasource"})
    public void createNewAnalyzerReport(String datasource) {
        homeService = new Home(homePage);
        pazReport = (AnalyserReportPage) homeService.createNew(FILETYPE.XANALYZER);
        analyzerService = new Analyzer(pazReport);
        analyzerService.selectDataSourcePAZandOpen(datasource);
        analyzerService.isPazDefaultOpened();
        sleep(3000);
    }

//    @Test
//    public void addFieldsToReport(){
//        analyzerService.addFieldsToReport(){
//
//        }
//    }
}

















