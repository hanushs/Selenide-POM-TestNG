package com.hv.pages;

import com.codeborne.selenide.SelenideElement;
import org.apache.log4j.Logger;
import org.openqa.selenium.support.FindBy;



/**
 * Created by pshynin on 11/10/2017.
 */
public class LoginPage {
    private static final Logger LOGGER = Logger.getLogger(LoginPage.class);
    @FindBy(xpath = "//div[@id='eval-users-toggle']/div[text()='Login as an Evaluator']")
    protected SelenideElement btnLoginAsEvaluator;

    @FindBy(xpath = "//div[@id='role-admin-panel']//button[contains(@onclick,'Admin')]")
    protected SelenideElement btnLoginAsAdmin;

    @FindBy(xpath = "//div[@id='role-business-user-panel']//button[contains(@onclick,'Suzy')]")
    protected SelenideElement btnLoginAsSuzy;

    @FindBy(id = "j_username")
    protected SelenideElement userNameField;

    @FindBy(id = "j_password")
    protected SelenideElement userPasswordField;

    @FindBy( xpath = "//button[contains(., 'Login')]" )
    protected SelenideElement btnLogin;


    public void clickLoginAsEvaluator() {
        LOGGER.info("Clicking on btnLoginAsEvaluator");
        btnLoginAsEvaluator.click();
    }

    public void clickLoginAsAdmin(){
        LOGGER.info("Clicking on btnLoginAsAdmin");
        btnLoginAsAdmin.click();
    }

    public void clickLoginAsSuzy(){
        LOGGER.info("Clicking on btnLoginAsSuzy");
        btnLoginAsSuzy.click();
    }

    public void enterUserName(String userName){
        LOGGER.info("Entering userName " + userName);
        userNameField.val(userName);
    }

    public void enterUserPassword(String password){
        LOGGER.info("Entering password ");
        userPasswordField.val(password);
    }

    public void clickLoginButton(){
        LOGGER.info("Clicking Login Button ");
        btnLogin.pressEnter();
    }




}