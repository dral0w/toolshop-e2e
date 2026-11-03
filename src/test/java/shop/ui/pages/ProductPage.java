package shop.ui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.math.BigDecimal;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class ProductPage {

    private final SelenideElement name = $("[data-test=product-name]");
    private final SelenideElement unitPrice = $("[data-test=unit-price]");
    private final SelenideElement description = $("[data-test=product-description]");
    private final SelenideElement quantity = $("[data-test=quantity]");
    private final SelenideElement increase = $("[data-test=increase-quantity]");
    private final SelenideElement addToCart = $("[data-test=add-to-cart]");
    private final SelenideElement outOfStock = $("[data-test=out-of-stock]");

    @Step("Открыть страницу товара {productId}")
    public ProductPage openPage(String productId) {
        open("/product/" + productId);
        name.shouldBe(visible);
        return this;
    }

    public SelenideElement name() {
        return name;
    }

    public SelenideElement description() {
        return description;
    }

    public SelenideElement outOfStock() {
        return outOfStock;
    }

    public SelenideElement addToCartButton() {
        return addToCart;
    }

    @Step("Прочитать цену товара")
    public BigDecimal price() {
        return Prices.parse(unitPrice.shouldBe(visible).getText());
    }

    @Step("Увеличить количество до {target}")
    public ProductPage increaseQuantityTo(int target) {
        for (int current = Integer.parseInt(quantity.getValue()); current < target; current++) {
            increase.click();
        }
        quantity.shouldHave(com.codeborne.selenide.Condition.value(String.valueOf(target)));
        return this;
    }

    @Step("Положить в корзину")
    public ProductPage addToCart() {
        addToCart.shouldBe(enabled).click();
        return this;
    }
}
