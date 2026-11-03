package shop.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.math.BigDecimal;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class CartPage {

    private final ElementsCollection titles = $$("[data-test=product-title]");
    private final ElementsCollection quantities = $$("[data-test=product-quantity]");
    private final ElementsCollection linePrices = $$("[data-test=line-price]");
    private final SelenideElement total = $("[data-test=cart-total]");
    private final SelenideElement proceed = $("[data-test=proceed-1]");

    @Step("Открыть корзину")
    public CartPage openPage() {
        open("/checkout");
        return this;
    }

    public ElementsCollection titles() {
        return titles;
    }

    public SelenideElement quantityOf(int index) {
        return quantities.get(index);
    }

    @Step("Прочитать сумму позиции {index}")
    public BigDecimal linePrice(int index) {
        return Prices.parse(linePrices.get(index).shouldBe(visible).getText());
    }

    @Step("Прочитать итог корзины")
    public BigDecimal total() {
        return Prices.parse(total.shouldBe(visible).getText());
    }

    public SelenideElement proceedButton() {
        return proceed;
    }
}
