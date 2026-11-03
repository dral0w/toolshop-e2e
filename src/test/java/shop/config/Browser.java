package shop.config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;

/**
 * Настройки браузера. Значения по умолчанию рассчитаны на CI (Chrome, headless);
 * локально переопределяются через -Dselenide.browser=edge и т.п.
 */
public final class Browser {

    private Browser() {
    }

    public static void configure() {
        Configuration.baseUrl = Config.uiBaseUrl();
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10_000;
        Configuration.pageLoadTimeout = 60_000;

        if (System.getProperty("selenide.browser") == null) {
            Configuration.browser = "chrome";
        }
        if (System.getProperty("selenide.headless") == null) {
            Configuration.headless = true;
        }
    }

    public static void attachReportListener() {
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true)
                .savePageSource(false));
    }

    public static void removeReportListener() {
        SelenideLogger.removeListener("allure");
    }
}
