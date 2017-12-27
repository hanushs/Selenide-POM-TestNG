package com.hv.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * Created by pshynin on 11/15/2017.
 */
public class AnalyserReportPage extends FilePage {
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

    @FindBy( id = "cmdFieldAdd_text" )
    protected SelenideElement contextMenuAddToReport;


    /**
     * This method returns field element
     *
     * @param fieldName name of the field to select
     * @return found field element
     */
    protected SelenideElement fieldItem(String fieldName) {
        return $(By.xpath("//div[contains(@class,'field') and text()='" + fieldName + "' and not(contains(@formula,'[Measures]'))]"));
    }

    public enum PAZFIELDADDWORKFLOW {
        DOUBLE_CLICK, RIGHT_CLICK, D_N_D,
    }

    public enum PanelItem {
        LAYOUT_COLUMNS("Columns"),
        LAYOUT_MEASURES("Measures"),
        LAYOUT_ROWS("Rows");

        private String name;

        private PanelItem(String name) {
            this.name = name;
        }

    }

    public void clickPAZDataSource(String datasourceName) {
        switchToDefault();
        switchToReportFrame();
        datasourceItem(datasourceName).doubleClick();
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

    public void clickOkPAZDataSource() {
        LOGGER.info("Pressing OK for PAZ datasource");
        btnOk.click();
        pazDatasourceWindow.shouldNotBe(Condition.visible);
    }

    public void isPazContentPaneExist() {
        LOGGER.info("Verifying if PAZ report content pane present");
        paz_contentPane.shouldBe(Condition.visible);
    }

    public void isPazLayoutPanelExist() {
        LOGGER.info("Verifying if PAZ report layout panel exist.");
        paz_layoutPanel.shouldBe(Condition.visible);
    }

    public void isPazReportContainerExist() {
        LOGGER.info("Verifying if PAZ report content container exist.");
        paz_reportContainer.shouldBe(Condition.visible);
    }

    public void isPazReportButtonsExist() {
        LOGGER.info("Verifying if PAZ report buttons panel exist.");
        paz_reportButtons.shouldBe(Condition.visible);
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

    public void verifyFieldAdded(String field){
        String assertIfDragged = "//div[@class='gem-label'][contains (., '" + field + "')]";
        SelenideElement addedRow = $(By.xpath(assertIfDragged));
        addedRow.waitUntil(Condition.visible,20000);
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

//        listField.dragAndDropTo(dropRows);

        Actions dragAndDrop = new Actions( getWebDriver() );
        Action action =
                dragAndDrop.clickAndHold( listField.getWrappedElement()).moveByOffset( 5, 5 ).release( dropRows.getWrappedElement() ).build();
        try {
            action.perform();
        } catch ( MoveTargetOutOfBoundsException e ) {
            LOGGER.error( "Exception occurs during drag and drop!: " + e.toString() );
            dragAndDrop.release().build().perform();
        }
    }
}
