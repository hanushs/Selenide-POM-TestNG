package com.hv.test;


import com.codeborne.selenide.testng.annotations.Report;

import static com.codeborne.selenide.Selenide.sleep;
import  static com.hv.pages.FilePage.FILETYPE;

import com.hv.pages.AnalyserReportPage;
import com.hv.pages.HomePage;
import com.hv.services.Analyzer;
import com.hv.services.Home;
import com.hv.services.Login;
import com.hv.utils.BaseTest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

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


    @Parameters({"user","password"})
    @BeforeClass
    public void login(String user, String password){
        loginService = new Login(loginPage);
        homePage = loginService.loginWithCredentials(user,password);
    }


    @Test
    public void createNewAnalyzerReport(){
        homeService = new Home(homePage);
        pazReport = (AnalyserReportPage) homeService.createNew(FILETYPE.XANALYZER);
        analyzerService = new Analyzer(pazReport);
        analyzerService.selectDataSourcePAZandOpen("SteelWheelsSales");
        analyzerService.isPazDefaultOpened();
        sleep(3000);

    }














//    @Test(dependsOnMethods = "login")
//    public void createNewReport() {
//        dataSource = home.createNew("Analysis report");
//        dataSource.selectDialog().shouldHave(text("Select Data Source"));
//    }
//
//    @Test(dependsOnMethods = "createNewReport")
//    public void selectDataSource() {
//        report = dataSource.selectDataSource("SteelWheels");
//        report.widgitLable().shouldHave(text("Analysis Report"));
//    }
//
//    @Test(dependsOnMethods = "selectDataSource")
//    public void addFields() {
//        report.addField("", "Rows");
//        report.addField("", "Columns");
//        report.addField("", "Measures");
//    }


}
