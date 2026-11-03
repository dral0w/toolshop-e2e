package shop.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.math.BigDecimal;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class CatalogPage {

    private final ElementsCollection cards = $$("a[data-test^=product-]");
    private final ElementsCollection names = $$("[data-test=product-name]");
    private final ElementsCollection prices = $$("[data-test=product-price]");
    private final SelenideElement sort = $("[data-test=sort]");
    private final SelenideElement searchField = $("[data-test=search-query]");
    private final SelenideElement searchSubmit = $("[data-test=search-submit]");

    @Step("Открыть каталог")
    public CatalogPage openPage() {
        open("/");
        cards.shouldBe(sizeGreaterThan(0));
        return this;
    }

    public ElementsCollection cards() {
        return cards;
    }

    public ElementsCollection names() {
        return names;
    }

    @Step("Отсортировать каталог: {option}")
    public CatalogPage sortBy(String option) {
        sort.selectOptionByValue(option);
        cards.shouldBe(sizeGreaterThan(0));
        return this;
    }

    @Step("Найти «{query}»")
    public CatalogPage search(String query) {
        searchField.setValue(query);
        searchSubmit.click();
        return this;
    }

    @Step("Прочитать цены с карточек")
    public List<BigDecimal> visiblePrices() {
        return prices.texts().stream().map(Prices::parse).toList();
    }

    @Step("Открыть карточку товара {productId}")
    public ProductPage openProduct(String productId) {
        $("a[data-test=product-" + productId + "]").click();
        return new ProductPage();
    }
}
