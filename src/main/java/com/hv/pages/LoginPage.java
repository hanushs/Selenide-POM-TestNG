package com.hv.pages;

import com.codeborne.selenide.SelenideElement;
import org.apache.log4j.Logger;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Selenide.page;


/**
 * Created by pshynin on 11/10/2017.
 */
public class LoginPage {
    private static final Logger LOGGER = Logger.getLogger(LoginPage.class);
    @FindBy(xpath = "//div[@id='eval-users-toggle']/div[text()='LoginService as an Evaluator']")
    protected SelenideElement btnLoginAsEvaluator;

    @FindBy(xpath = "//div[@id='role-admin-panel']//button[contains(@onclick,'Admin')]")
    protected SelenideElement btnLoginAsAdmin;

    @FindBy(xpath = "//div[@id='role-business-user-panel']//button[contains(@onclick,'Suzy')]")
    protected SelenideElement btnLoginAsSuzy;

    @FindBy(id = "j_username")
    protected SelenideElement userNameField;

    @FindBy(id = "j_password")
    protected SelenideElement userPasswordField;

    @FindBy( xpath = "//button[contains(., 'LoginService')]" )
    protected SelenideElement btnLogin;

    public enum USER {
        ADMIN, SUZY
    }

    public HomePage loginWithCredentials(String userName, String password) {
        LOGGER.info("Login to pentaho as " + userName);
        userNameField.val(userName);
        userPasswordField.val(password);
        btnLogin.pressEnter();
        return page(HomePage.class);
    }

    public HomePage loginAsEvaluator(USER user) {
        LOGGER.info("LoginService to Pentaho as " + user);
        btnLoginAsEvaluator.click();
        switch (user) {
            case ADMIN:
                btnLoginAsAdmin.click();
                break;
            case SUZY:
                btnLoginAsSuzy.click();
                break;
        }
        return page(HomePage.class);
    }




}