package shop.ui.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class Header {

    private final SelenideElement cart = $("[data-test=nav-cart]");
    private final SelenideElement cartQuantity = $("[data-test=cart-quantity]");
    private final SelenideElement signIn = $("[data-test=nav-sign-in]");

    public SelenideElement cartLink() {
        return cart;
    }

    public SelenideElement cartQuantity() {
        return cartQuantity;
    }

    public SelenideElement signIn() {
        return signIn;
    }

    @Step("Перейти в корзину из шапки")
    public void openCart() {
        cart.click();
    }
}
