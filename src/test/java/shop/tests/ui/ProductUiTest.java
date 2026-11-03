package shop.tests.ui;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import shop.api.ProductsApi;
import shop.model.Product;
import shop.tests.BaseUiTest;
import shop.ui.pages.ProductPage;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("ui")
@Epic("Toolshop UI")
@Feature("Карточка товара")
@DisplayName("Страница товара")
class ProductUiTest extends BaseUiTest {

    private final ProductPage productPage = new ProductPage();

    @Test
    @Story("Отображение товара")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Название и цена на странице совпадают с данными API")
    void productPageMatchesApiData() {
        Product expected = ProductsApi.firstInStock();

        productPage.openPage(expected.id());

        productPage.name().shouldHave(exactText(expected.name()));
        productPage.description().shouldBe(visible);
        assertEquals(0, expected.price().compareTo(productPage.price()),
                "цена на странице должна совпадать с ценой из API");
    }

    @Test
    @Story("Товар не в наличии")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Товар не в наличии нельзя положить в корзину")
    void outOfStockProductCannotBeAdded() {
        Product outOfStock = ProductsApi.firstOutOfStock().orElseGet(() -> {
            Assumptions.abort("на первой странице каталога нет товаров не в наличии");
            return null;
        });

        productPage.openPage(outOfStock.id());

        productPage.outOfStock().shouldBe(visible);
        productPage.addToCartButton().shouldBe(disabled);
    }
}
