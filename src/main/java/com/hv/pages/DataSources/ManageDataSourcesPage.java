package com.hv.pages.DataSources;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by shanush on 1/8/2018.
 */
public class ManageDataSourcesPage  {
    private static final Logger LOGGER = Logger.getLogger(ManageDataSourcesPage.class);


    private SelenideElement expectedDS(String expectedDatasource) {
        return $(By.xpath("//div[text()='" + expectedDatasource + "']"));
    }


    public void verifyDScreated(String expectedDatasource) {
        expectedDS(expectedDatasource).shouldBe(Condition.exist);
    }

}


