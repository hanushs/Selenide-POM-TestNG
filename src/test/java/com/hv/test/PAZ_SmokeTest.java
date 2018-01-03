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

// Automated Test: http://testrail.pentaho.com/index.php?/cases/view/64925

@Report
public class PAZ_SmokeTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(PAZ_SmokeTest.class);

    private HomePage homePage;
    public IReportOptions reportOptions;
    private AnalyserReportPage pazReport;
    private ExportToCsvPage exportToCsvPage;

    @BeforeClass
    public void login() {
        homePage = loginPage.loginWithCredentials(getTestData().get("UserName"), getTestData().get("UserPassword"));
    }
    
    @Test
    public void createNewAnalyzerReport() {
        pazReport = (AnalyserReportPage) homePage.createNew(FILETYPE.XANALYZER);
        pazReport.selectDataSourcePAZandOpen(getTestData().get("DatasourceName"));
        pazReport.isPazDefaultOpened();
        sleep(3000);
    }

    @Test(dependsOnMethods = "createNewAnalyzerReport",groups = "fieldAdd")
    public void addFieldsToReportWithDoubleClick() {
        String fieldToRows = getTestData().get("FieldsToMove").split(";")[0];
        pazReport.addFieldToReport(fieldToRows, AnalyserReportPage.PAZFIELDADDWORKFLOW.DOUBLE_CLICK);
        pazReport.verifyFieldAdded(fieldToRows);
    }

    @Test(dependsOnMethods = "createNewAnalyzerReport",groups = "fieldAdd")
    public void addFieldsToReportWithRightClick() {
        String fieldToRows1 = getTestData().get("FieldsToMove").split(";")[1];
        pazReport.addFieldToReport(fieldToRows1, AnalyserReportPage.PAZFIELDADDWORKFLOW.RIGHT_CLICK);
        pazReport.verifyFieldAdded(fieldToRows1);
    }

    @Test(dependsOnMethods = "createNewAnalyzerReport",groups = "fieldAdd")
    public void addFieldsToReportWithDnD() {
        String fieldsToMeasures = getTestData().get("FieldsToMove").split(";")[2];
        pazReport.addFieldToReport(fieldsToMeasures, AnalyserReportPage.PAZFIELDADDWORKFLOW.D_N_D, AnalyserReportPage.PanelItem.LAYOUT_MEASURES);
        pazReport.verifyFieldAdded(fieldsToMeasures);
    }

    @Test(dependsOnMethods = "addFieldsToReportWithDnD",dependsOnGroups = "fieldAdd")
    public void sortFields() {
        pazReport.sortColumn(getTestData().get("FieldsToSort"), HIGHtoLOW);
        pazReport.handleAlert();
    }

    @Test(dependsOnMethods = "createNewAnalyzerReport")
    public void openReportOptions() {
        pazReport.openMoreActionsAndOptions();
        reportOptions = pazReport.openReportOptions();
        reportOptions.checkGrandTotalForColumns();
        reportOptions.clickOkReportOptions();
    }

    @Test(dependsOnMethods = "createNewAnalyzerReport")
    public void saveReportWithName(){
        pazReport.saveReport(getTestData().get("ReportNameToSave"),getTestData().get("FilePathToSave"));
    }

    @Test(dependsOnMethods = "createNewAnalyzerReport")
    public void exportAs(){
        AnalyserReportPage.EXPORTYPE type = AnalyserReportPage.EXPORTYPE.valueOf(getTestData().get("ExportType"));
        pazReport.openMoreActionsAndOptions();
        exportToCsvPage=(ExportToCsvPage) pazReport.exportAsFileType(type);
        exportToCsvPage.exportWithOption();
        sleep(2000);
    }



}

















