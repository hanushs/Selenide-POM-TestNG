package com.hv.test;


import com.codeborne.selenide.testng.annotations.Report;
import com.hv.pages.PAZ.AnalyserReportPage;
import com.hv.pages.base.HomePage;
import com.hv.pages.base.IReportOptions;
import com.hv.pages.PAZ.ExportToCsvPage;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.sleep;
import static com.hv.pages.PAZ.AnalyserReportPage.SORT.HIGHtoLOW;
import static com.hv.pages.base.FilePage.FILETYPE;

/**
 * Created by pshynin on 12/1/2017.
 */
@Report
public class PAZ_SmokeTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(PAZ_SmokeTest.class);

    private HomePage homePage;
    public IReportOptions reportOptions;
    private AnalyserReportPage pazReport;
    private ExportToCsvPage exportToCsvPage;

    @Parameters({"user", "password"})
    @BeforeClass
    public void login(String user, String password) {
        homePage = loginPage.loginWithCredentials(user, password);
    }
    
    @Test
    @Parameters({"datasource"})
    public void createNewAnalyzerReport(String datasource) {
        pazReport = (AnalyserReportPage) homePage.createNew(FILETYPE.XANALYZER);
        pazReport.selectDataSourcePAZandOpen(datasource);
        pazReport.isPazDefaultOpened();
        sleep(3000);
    }

    @Test
    @Parameters({"fieldsToRows"})
    public void addFieldsToReportWithDoubleClick(String fieldsToRows) {
        pazReport.addFieldToReport(fieldsToRows, AnalyserReportPage.PAZFIELDADDWORKFLOW.DOUBLE_CLICK);
        pazReport.verifyFieldAdded(fieldsToRows);
    }

    @Test
    @Parameters({"fieldsToRows1"})
    public void addFieldsToReportWithRightClick(String fieldsToRows1) {
        pazReport.addFieldToReport(fieldsToRows1, AnalyserReportPage.PAZFIELDADDWORKFLOW.RIGHT_CLICK);
        pazReport.verifyFieldAdded(fieldsToRows1);
    }

    @Test
    @Parameters({"fieldsToMeasures"})
    public void addFieldsToReportWithDnD(String fieldsToMeasures) {
        pazReport.addFieldToReport(fieldsToMeasures, AnalyserReportPage.PAZFIELDADDWORKFLOW.D_N_D, AnalyserReportPage.PanelItem.LAYOUT_MEASURES);
        pazReport.verifyFieldAdded(fieldsToMeasures);
    }

    @Test
    @Parameters({"fieldToSort"})
    public void sortFields(String fieldToSort) {
        pazReport.sortColumn(fieldToSort, HIGHtoLOW);
        pazReport.handleAlert();
    }

    @Test
    public void openReportOptions() {
        pazReport.openMoreActionsAndOptions();
        reportOptions = pazReport.openReportOptions();
        reportOptions.checkGrandTotalForColumns();
        reportOptions.clickOkReportOptions();
    }

    @Test
    @Parameters({"reportNameToSave","filePathToSave"})
    public void saveReportWithName(String reportNameTosave, String filePathToSave){
        pazReport.saveReport(reportNameTosave,filePathToSave);
    }

    @Test
    @Parameters({"exportType"})
    public void exportAs(AnalyserReportPage.EXPORTYPE type){
        pazReport.openMoreActionsAndOptions();
        exportToCsvPage=(ExportToCsvPage) pazReport.exportAsFileType(type);
        exportToCsvPage.exportWithOption();
        sleep(2000);
    }



}

















