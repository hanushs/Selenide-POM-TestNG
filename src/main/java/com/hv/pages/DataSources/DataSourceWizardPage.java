package com.hv.pages.DataSources;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.hv.pages.base.BasePage;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.Selenide.sleep;

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
}
