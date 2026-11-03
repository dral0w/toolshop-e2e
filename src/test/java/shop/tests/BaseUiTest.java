package shop.tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import shop.config.AllureEnvironment;
import shop.config.Browser;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public abstract class BaseUiTest {

    @BeforeAll
    static void setUpBrowser() {
        AllureEnvironment.write();
        Browser.configure();
        Browser.attachReportListener();
    }

    /** Корзина живёт в sessionStorage, поэтому браузер закрывается между тестами — так они независимы. */
    @AfterEach
    void closeBrowser() {
        closeWebDriver();
    }

    @AfterAll
    static void detachReportListener() {
        Browser.removeReportListener();
    }
}
