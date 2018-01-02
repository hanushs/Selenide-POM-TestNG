package com.hv.pages.PAZ;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.hv.pages.base.FilePage;
import com.hv.pages.base.IReportOptions;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * Created by pshynin on 11/15/2017.
 */
public class AnalyserReportPage extends FilePage implements IReportOptions {
    private static final Logger LOGGER = Logger.getLogger(AnalyserReportPage.class);

    //Select DataSource Window
    @FindBy(xpath = "//*[@id='datasources']/*")
    protected ElementsCollection datasources;

    protected SelenideElement datasourceItem(String datasourceName) {
        return $(By.cssSelector("#datasources>option[title*='" + datasourceName + "']"));
    }

    @FindBy(id = "btnNext")
    protected SelenideElement btnOk;

    //PAZ report default layout elements!
    @FindBy(xpath = "//div[@class='dialog-content']")
    protected SelenideElement pazDatasourceWindow;

    @FindBy(id = "fieldListWrapperContentPane")
    protected SelenideElement paz_contentPane;

    @FindBy(id = "layoutPanelWrapper")
    protected SelenideElement paz_layoutPanel;

    @FindBy(id = "reportContainer")
    protected SelenideElement paz_reportContainer;

    @FindBy(id = "reportBtns")
    protected SelenideElement paz_reportButtons;
    //__________________________________________________

    @FindBy(id = "cmdFieldAdd_text")
    protected SelenideElement contextMenuAddToReport;

    @FindBy(id = "PM:measSortHiLow_text")
    protected SelenideElement sortHighToLow;

    @FindBy(id = "PM:measSortLowHi_text")
    protected SelenideElement sortLowToHigh;

    @FindBy(id = "dlgTitle")
    protected SelenideElement sortAlertDialog;

    @FindBy(id = "dlgBtnCancel")
    protected SelenideElement btnOKAlert;


    //Report Options
    @FindBy(id = "RO_showColumnGrandTotal")
    protected static SelenideElement chkShowColumnGrandTotal;

    @FindBy(xpath = "//button[text()='OK']")
    protected SelenideElement btnOK;

    @FindBy(id = "exportMenuItem")
    private SelenideElement exportMenuElem;

    @FindBy(id = "cmdPDF")
    private SelenideElement exportPdfMenuElem;

    @FindBy(id = "cmdCSV")
    private SelenideElement exportCsvMenuElem;

    @FindBy(id = "cmdExcel")
    private SelenideElement exportExcelMenuElem;

    /**
     * This method returns field element
     *
     * @param fieldName name of the field to select
     * @return found field element
     */
    protected SelenideElement fieldItem(String fieldName) {
        return $(By.xpath("//div[contains(@class,'field') and text()='" + fieldName + "' and not(contains(@formula,'[Measures]'))]"));
    }

    protected SelenideElement pivotTableColumnHeader(String columnHeaderName) {
        return $(By.xpath("//div[@class='columnHeaders pivotTableColumnHeaderContainer']//div[contains(.,'" + columnHeaderName + "')]"));

    }

    public enum SORT {
        LOWtoHIGH, HIGHtoLOW
    }

    public enum PAZFIELDADDWORKFLOW {
        DOUBLE_CLICK, RIGHT_CLICK, D_N_D
    }

    public enum EXPORTYPE {
        PDF, CSV, EXCEL
    }

    public enum PanelItem {
        LAYOUT_COLUMNS("Columns"),
        LAYOUT_MEASURES("Measures"),
        LAYOUT_ROWS("Rows");
        private String name;

        PanelItem(String name) {
            this.name = name;
        }
    }

    public void makeClickable() {
        // magic fix to make element clickable if div is on the front
        try {
            while (!$$(By.cssSelector("body > div[style*='display: block']")).isEmpty()) {
                $$(By.cssSelector("body > div[style*='display: block']")).get(0).click();
                LOGGER.info("make clickable");
            }
        } catch (Exception e) {
            LOGGER.warn("'Exception appeared during makeClickable operation: " + e.getMessage());
        }
    }

    public void addFieldToReport(String field, PAZFIELDADDWORKFLOW workflow) {
        addFieldToReport(field, workflow, PanelItem.LAYOUT_ROWS);
    }

    public void addFieldToReport(String field, PAZFIELDADDWORKFLOW workflow, PanelItem panelItem) {
        SelenideElement element = fieldItem(field);
        switch (workflow) {
            case DOUBLE_CLICK:
                LOGGER.info("Adding field " + field + " to report via Double Click");
                element.doubleClick();
                break;
            case RIGHT_CLICK:
                LOGGER.info("Adding field " + field + " to report via Right Click");
                element.contextClick();
                contextMenuAddToReport.click();
                break;
            case D_N_D:
                LOGGER.info("Adding field " + field + " to report via Drag And Drop");
                fieldDragAndDrop(field, panelItem);
                break;
        }
    }

    public void verifyFieldAdded(String field) {
        String assertIfDragged = "//div[@class='gem-label'][contains (., '" + field + "')]";
        SelenideElement addedRow = $(By.xpath(assertIfDragged));
        addedRow.waitUntil(Condition.visible, 20000);
    }

    public void fieldDragAndDrop(String fieldDrag, PanelItem fieldDropTo) {
        // layout to drop
        String dropRowLevel = "";
        String dropLevelHere = "Drop Level Here";
        String dropMeasureHere = "Drop Measure Here";

        if (fieldDropTo.equals(PanelItem.LAYOUT_ROWS)) {
            dropRowLevel = "//div[contains(@id,'rows_ui')]/div[contains (., '" + dropLevelHere + "')]";
        } else if (fieldDropTo.equals(PanelItem.LAYOUT_COLUMNS)) {
            dropRowLevel = "//div[contains(@id,'columns_ui')]/div[contains (., '" + dropLevelHere + "')]";
        } else if (fieldDropTo.equals(PanelItem.LAYOUT_MEASURES)) {
            dropRowLevel = "//div[contains(@id,'measures_ui')]/div[contains (., '" + dropMeasureHere + "')]";
        } else {
            Assert.fail("parameter fieldDropTo is not supported: " + fieldDropTo);
        }

        // field to drag
        String dragRowLevel = "//div[@id='fieldListTreeContent']//div[@dndtype][contains (., '" + fieldDrag + "')]";

        SelenideElement listField = $(By.xpath(dragRowLevel));
        SelenideElement dropRows = $(By.xpath(dropRowLevel));

        Actions dragAndDrop = new Actions(getWebDriver());
        Action action =
                dragAndDrop.clickAndHold(listField.getWrappedElement()).moveByOffset(5, 5).release(dropRows.getWrappedElement()).build();
        try {
            action.perform();
        } catch (MoveTargetOutOfBoundsException e) {
            LOGGER.error("Exception occurs during drag and drop!: " + e.toString());
            dragAndDrop.release().build().perform();
        }
    }

    public void sortColumn(String columnHeaderName, SORT sort) {
        switchToReportFrame();
        switch (sort) {
            case LOWtoHIGH:
                pivotTableColumnHeader(columnHeaderName).contextClick();
                sortLowToHigh.hover().click();
                break;
            case HIGHtoLOW:
                pivotTableColumnHeader(columnHeaderName).contextClick();
                sortHighToLow.hover().click();
                break;
        }
    }

    public void handleAlert() {
        sortAlertDialog.shouldHave(Condition.text("Alert"));
        btnOKAlert.click();
    }

    public void openMoreActionsAndOptions() {
        switchToReportFrame();
        if (btnMoreActions.isDisplayed()) {
            btnMoreActions.click();
            if (!menuMoreActions.isDisplayed()) {
                btnMoreActions.click();
                if (!menuMoreActions.isDisplayed()) {
                    throw new RuntimeException("The more actions menu never opened!");
                }
            }
        }
    }

    public IReportOptions openReportOptions() {
        btnReportOptionsMoreMenu.click();
        return page(AnalyserReportPage.class);
    }

    public FilePage exportAsFileType(EXPORTYPE type) {
        exportMenuElem.click();
        switch (type) {
            case PDF:
                exportPdfMenuElem.click();
                return page(ExportToPDFPage.class);
            case CSV:
                exportCsvMenuElem.click();
                return page(ExportToCsvPage.class);
            case EXCEL:
                exportExcelMenuElem.click();
                return page(ExportToExcelPage.class);
            default:
                throw new IllegalArgumentException("Used ansupported export type: " + type);
        }

    }

    public void checkGrandTotalForColumns() {
        if (!chkShowColumnGrandTotal.is(Condition.checked)) {
            chkShowColumnGrandTotal.click();
        }
        chkShowColumnGrandTotal.shouldBe(Condition.checked);
    }

    public void clickOkReportOptions() {
        btnOK.click();
    }

    public void selectDataSourcePAZandOpen(String dataSource) {
        makeClickable();
        switchToDefault();
        switchToReportFrame();
        datasourceItem(dataSource).doubleClick();
        loading();
        switchToDefault();
    }

    public void isPazDefaultOpened() {
        LOGGER.info("Verifying, that default PAZ report opened.");
        switchToDefault();
        pazReportTab("Analysis Report").should(Condition.visible);
        switchToReportFrame();
        paz_contentPane.shouldBe(Condition.visible);
        paz_layoutPanel.shouldBe(Condition.visible);
        paz_reportContainer.shouldBe(Condition.visible);
        paz_reportButtons.shouldBe(Condition.visible);
    }

}
