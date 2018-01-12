package com.hv.pages.DataSources;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.hv.pages.base.BasePage;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.*;

/**
 * Created by shanush on 1/3/2018.
 */
public class DataSourceWizardPage extends BasePage implements ISQLQuery {
    private static final Logger LOGGER = Logger.getLogger(DataSourceWizardPage.class);

    @FindBy(xpath = "//div[@class='gwt-Label' and contains(.,'Select Source Type')]")
    protected SelenideElement selectedDataSourceType;

    @FindBy(id = "datasourceName")
    protected SelenideElement dataSourceNameValue;

    private SelenideElement selectDatasourceType(DataSourceType type) {
        return $(By.xpath("//div[text()='" + type + "']"));
    }

    // Database Connection __________________
    @FindBy(xpath = "//div[@class='Caption' and contains(.,'Database Connection') ]")
    protected SelenideElement dbConnectionWindowLabel;

    @FindBy(css = "#addConnection .pentaho-addbutton")
    protected SelenideElement btnAddConnection;

    @FindBy(id = "connection-name-text")
    protected SelenideElement dbConnectionName;

    protected SelenideElement databaseType(String databaseType) {
        return $(By.xpath("//div[@class='gwt-Label' and contains(.,'" + databaseType + "')]"));
    }

    @FindBy(id = "server-host-name-text")
    protected SelenideElement dbConnectionHostName;

    @FindBy(id = "database-name-text")
    protected SelenideElement dbConnectionDBName;


    @FindBy(id = "username-text")
    protected SelenideElement dbConnectionUserName;

    @FindBy(id = "test-button")
    protected SelenideElement btnDBConnectionTest;

    @FindBy(xpath = "//div[@class='gwt-HTML gwt-Label' and contains(.,'succeeded')]")
    protected SelenideElement testConnectionSuccess;

    @FindBy(xpath = "//button[text()='OK']")
    public SelenideElement btnTestConnectionOK;

    @FindBy(id = "general-datasource-window_accept")
    protected SelenideElement btnOK;


    @FindBy(id = "overwriteConnectionConfirmationDialog_accept")
    protected SelenideElement btnOkOverWriteConnectionDS;

    @FindBy(xpath = "//div[text()='Overwrite Connection Confirmation']")
    protected SelenideElement lblOverWriteConnectionDS;


    //_________________________________________

    // SQL Query
    @FindBy(id = "query")
    protected SelenideElement txtAreaQuery;

    // Back,Next, Finish, Cancel buttons

    @FindBy(id = "main_wizard_window_extra1")
    protected SelenideElement btnBack;

    @FindBy(id = "main_wizard_window_extra2")
    protected SelenideElement btnNext;

    @FindBy(id = "main_wizard_window_accept")
    protected SelenideElement btnFinish;

    @FindBy(id = "main_wizard_window_cancel")
    protected SelenideElement btnCancel;

    @FindBy(id = "preview")
    protected SelenideElement btnPreviewData;

    //_________________________________________

    // DS Model________________________________
    @FindBy(xpath = "//div[@class='dialogTopCenterInner' and contains(.,'Data Source Created')]")
    protected SelenideElement windowDSCreated;

    public ElementsCollection modelRadioButtons() {
        return $$(By.cssSelector("#modelerDecision [type=radio]"));
    }

    public SelenideElement modelLabel(String id) {
        return $(By.xpath("//label[@for='" + id + "']"));
    }

    @FindBy(id = "summaryDialog_accept")
    public SelenideElement btnOKModel;

    public SelenideElement model(String labelText) {
        return $(By.xpath("//label[text()='" + labelText + "']"));
    }


    //__________________________________________

    @FindBy(id = "overwriteDialog_accept")
    protected SelenideElement btnOkOverWriteDS;

    @FindBy(xpath = "//div[text()='Overwrite Data Source']")
    protected SelenideElement lblOverWriteDS;
    private static final String MODEL_RADIO_BUTTON_DEFAULT = "Keep default model";
    private static final String MODEL_RADIO_BUTTON_CUSTOMIZE = "Customize model now";

    public enum DSMODEL {
        CUSTOM, DEFAULT
    }

    //DataSource types when creating datasource.
    public enum DataSourceType {
        CSVFile("CSV File"), SQLQuery("SQL Query"), DatabaseTables("Database Table(s)");
        private String dataSourceActualName;

        DataSourceType(String dataSourceActualName) {
            this.dataSourceActualName = dataSourceActualName;
        }

        public String getDataSourceActualName() {
            return this.dataSourceActualName;
        }
    }


    public void enterDatasourceName(String dataSourceName) {
        dataSourceNameValue.val(dataSourceName);
    }

    public IBaseSourceType selectSourceType(DataSourceType type) {
        selectedDataSourceType.getWrappedElement().click();
        WebElement element = $(By.xpath("//div[text()='" + type.getDataSourceActualName() + "']")).getWrappedElement();
        if (element.isDisplayed()) {
            element.click();
        } else {
            throw new NoSuchElementException(element + " is not displayed");
        }
        return page(DataSourceWizardPage.class);
    }

    @Override
    public void createNewConnection(String databaseConnectionName, String dbType, String databaseConnectionHostName, String databaseConnectionUserName, String databaseConnectionDBName) {
        btnAddConnection.click();
        dbConnectionWindowLabel.waitUntil(Condition.visible, 5000);
        if (dbConnectionWindowLabel.is(Condition.exist)) {
            dbConnectionName.val(databaseConnectionName);
            databaseType(dbType).getWrappedElement().click();
            dbConnectionHostName.val(databaseConnectionHostName);
            dbConnectionDBName.val(databaseConnectionDBName);
            dbConnectionUserName.val(databaseConnectionUserName);
            btnDBConnectionTest.click();
            testConnectionSuccess.waitUntil(Condition.visible, 5000);
            btnTestConnectionOK.click();
        } else {
            throw new RuntimeException("Datasource Connection Window no opened.");
        }
        btnOK.click();
        if (!lblOverWriteConnectionDS.is(Condition.visible)) {
            sleep(1000);
        }
        if (lblOverWriteConnectionDS.is(Condition.visible)) {
            btnOkOverWriteConnectionDS.click();
        }
    }

    @Override
    public void addQuery(String query) {
        txtAreaQuery.val(query);
    }

    public void verifyButtonState() {
        btnBack.shouldBe(Condition.disabled);
        btnNext.shouldBe(Condition.disabled);
        btnFinish.shouldBe(Condition.enabled);
        btnCancel.shouldBe(Condition.enabled);
        btnPreviewData.shouldBe(Condition.enabled);
    }

    public void finishWizard() {
        if (btnFinish.isDisplayed() && !btnFinish.isEnabled()) {
            LOGGER.error("Finish button is not enabled!");
        }
        btnFinish.click();
        if(!lblOverWriteDS.is(Condition.visible)){
            sleep(500);
        }
        if (lblOverWriteDS.is(Condition.visible)) {
            btnOkOverWriteDS.click();
        }
    }


    public boolean isDefaultModelSelected() {
        String id = null;
        for (int i = 0; i < modelRadioButtons().size(); i++) {
            if (modelRadioButtons().get(i).isSelected()) {
                id = modelRadioButtons().get(i).getAttribute("id");
            }
        }
        return modelLabel(id).getText().equals(MODEL_RADIO_BUTTON_DEFAULT);
    }

    public void setModel(DSMODEL model) {
        windowDSCreated.waitUntil(Condition.visible, 5000);
        switch (model) {
            case DEFAULT:
                if (!isDefaultModelSelected()) {
                    model(MODEL_RADIO_BUTTON_DEFAULT).click();
                }
                break;
            case CUSTOM:
                if (isDefaultModelSelected()) {
                    model(MODEL_RADIO_BUTTON_CUSTOMIZE).click();
                }
                break;
        }
        btnOKModel.click();
    }

}
