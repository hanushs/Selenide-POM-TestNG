package com.hv.test;

import com.codeborne.selenide.testng.annotations.Report;
import com.hv.pages.PAZ.AnalyserReportPage;
import com.hv.pages.PAZ.ExportToCsvPage;
import com.hv.pages.base.IReportOptions;
import com.hv.pages.base.LoginPage;
import com.hv.pages.base.MenuPage;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static com.hv.pages.PAZ.AnalyserReportPage.SORT.HIGHtoLOW;
import static com.hv.pages.base.FilePage.FILETYPE;

/**
 * Created by shanush on 12/1/2017.
 */

// Automated Test: http://testrail.pentaho.com/index.php?/cases/view/64925


public class PAZ_SmokeTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(PAZ_SmokeTest.class);

    private MenuPage menuPage;
    public IReportOptions reportOptions;
    private AnalyserReportPage pazReport;
    private ExportToCsvPage exportToCsvPage;

    @Test
    public void login() {
            LOGGER.info("Opening " + baseUrl + " in " + browser + " browser");
            loginPage = open(baseUrl, LoginPage.class);
            menuPage = loginPage.loginWithCredentials(getTestData().get("UserName"), getTestData().get("UserPassword"));
    }

    //Steps: 1
    @Test
    public void createNewAnalyzerReport() {
        pazReport = (AnalyserReportPage) menuPage.createNew(FILETYPE.XANALYZER);
        pazReport.selectDataSourcePAZandOpen(getTestData().get("DatasourceName"));
        pazReport.isPazDefaultOpened();
        sleep(3000);
    }

    //Steps: 2
    @Test(dependsOnMethods = "createNewAnalyzerReport")
    public void addFieldsToReportWithDoubleClick() {
        String fieldToRows = getTestData().get("FieldsToMove").split(";")[0];
        pazReport.addFieldToReport(fieldToRows, AnalyserReportPage.PAZFIELDADDWORKFLOW.DOUBLE_CLICK);
        pazReport.verifyFieldAdded(fieldToRows);
    }
    //Steps: 3
    @Test(dependsOnMethods = "createNewAnalyzerReport")
    public void addFieldsToReportWithRightClick() {
        String fieldToRows1 = getTestData().get("FieldsToMove").split(";")[1];
        pazReport.addFieldToReport(fieldToRows1, AnalyserReportPage.PAZFIELDADDWORKFLOW.RIGHT_CLICK);
        pazReport.verifyFieldAdded(fieldToRows1);
    }

    //Steps: 4
    @Test(dependsOnMethods = "createNewAnalyzerReport")
    public void addFieldsToReportWithDnD() {
        String fieldsToMeasures = getTestData().get("FieldsToMove").split(";")[2];
        pazReport.addFieldToReport(fieldsToMeasures, AnalyserReportPage.PAZFIELDADDWORKFLOW.D_N_D, AnalyserReportPage.PanelItem.LAYOUT_MEASURES);
        pazReport.verifyFieldAdded(fieldsToMeasures);
    }

    //Steps: 5-6
    @Test(dependsOnMethods = "addFieldsToReportWithDnD")
    public void sortFields() {
        pazReport.sortColumn(getTestData().get("FieldsToSort"), HIGHtoLOW);
        pazReport.handleAlert();
    }

    //Steps: 7
    @Test(dependsOnMethods = "createNewAnalyzerReport")
    public void openReportOptions() {
        pazReport.openMoreActionsAndOptions();
        reportOptions = pazReport.openReportOptions();
        reportOptions.checkGrandTotalForColumns();
        reportOptions.clickOkReportOptions();
    }

    //Steps: 8
    @Test(dependsOnMethods = "createNewAnalyzerReport")
    public void saveReportWithName(){
        pazReport.saveReport(getTestData().get("ReportNameToSave"),getTestData().get("FilePathToSave"));
    }

    //Steps: 22-23
    @Test(dependsOnMethods = "createNewAnalyzerReport")
    public void exportAs(){
        pazReport.openMoreActionsAndOptions();
        exportToCsvPage=(ExportToCsvPage) pazReport.exportAsFileType(AnalyserReportPage.EXPORTYPE.CSV);
        exportToCsvPage.exportWithOption();
        sleep(2000);
    }



}

















