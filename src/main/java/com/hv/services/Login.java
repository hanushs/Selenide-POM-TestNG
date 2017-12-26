package com.hv.services;

import com.hv.pages.HomePage;
import com.hv.pages.LoginPage;
import org.apache.log4j.Logger;
import static com.codeborne.selenide.Selenide.page;

/**
 * Created by shanush on 12/21/2017.
 */

public class Login {
    private static final Logger LOGGER = Logger.getLogger(Login.class);
    private LoginPage loginPage;

    public Login(LoginPage loginPage) {
        this.loginPage = loginPage;
    }

    public enum USER {
        ADMIN, SUZY
    }

    public HomePage loginAsEvaluator(USER user) {
        LOGGER.info("Login to Pentaho as " + user);
        loginPage.clickLoginAsEvaluator();
        switch (user) {
            case ADMIN:
                loginPage.clickLoginAsAdmin();
                break;
            case SUZY:
                loginPage.clickLoginAsSuzy();
                break;
        }
        return page(HomePage.class);
    }

    public HomePage loginWithCredentials(String userName, String password) {
        LOGGER.info("Login to pentaho as " + userName);
        loginPage.enterUserName(userName);
        loginPage.enterUserPassword(password);
        loginPage.clickLoginButton();
        return page(HomePage.class);
    }
}
