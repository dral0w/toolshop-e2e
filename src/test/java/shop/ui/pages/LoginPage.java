package shop.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginPage {

    private final SelenideElement form = $("[data-test=login-form]");
    private final SelenideElement email = $("[data-test=email]");
    private final SelenideElement password = $("[data-test=password]");
    private final SelenideElement submit = $("[data-test=login-submit]");
    private final SelenideElement error = $("[data-test=login-error]");
    private final SelenideElement registerLink = $("[data-test=register-link]");

    @Step("Открыть форму входа")
    public LoginPage openPage() {
        open("/auth/login");
        form.shouldBe(visible);
        return this;
    }

    @Step("Отправить форму входа для {userEmail}")
    public LoginPage submitCredentials(String userEmail, String userPassword) {
        email.setValue(userEmail);
        password.setValue(userPassword);
        submit.click();
        return this;
    }

    public SelenideElement error() {
        return error;
    }

    public SelenideElement registerLink() {
        return registerLink;
    }
}
