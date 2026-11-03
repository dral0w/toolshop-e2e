package shop.tests.ui;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import shop.tests.BaseUiTest;
import shop.ui.pages.LoginPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
@Epic("Toolshop UI")
@Feature("Авторизация")
@DisplayName("Форма входа")
class LoginUiTest extends BaseUiTest {

    private final LoginPage loginPage = new LoginPage();

    @Test
    @Story("Отказ во входе")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Неверные учётные данные показывают ошибку и не пускают внутрь")
    void invalidCredentialsAreRejected() {
        loginPage.openPage()
                .submitCredentials("nobody-" + System.nanoTime() + "@example.com", "definitely-wrong");

        loginPage.error()
                .shouldBe(visible)
                .shouldHave(text("Invalid email or password"));

        assertTrue(WebDriverRunner.url().contains("/auth/login"),
                "пользователь должен остаться на странице входа");
    }

    @Test
    @Story("Навигация")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("С формы входа доступна регистрация")
    void registrationIsReachableFromLogin() {
        loginPage.openPage();

        loginPage.registerLink().shouldBe(visible).click();

        assertTrue(WebDriverRunner.url().contains("/auth/register"),
                "ссылка должна вести на форму регистрации");
    }
}
