package shop.tests.ui;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import shop.api.ProductsApi;
import shop.model.Product;
import shop.tests.BaseUiTest;
import shop.ui.pages.CatalogPage;

import java.math.BigDecimal;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.allMatch;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
@Epic("Toolshop UI")
@Feature("Каталог")
@DisplayName("Каталог в браузере")
class CatalogUiTest extends BaseUiTest {

    private final CatalogPage catalog = new CatalogPage();

    @Test
    @Story("Отображение товаров")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("На главной отображаются карточки товаров с названием и ценой")
    void catalogShowsProductCards() {
        catalog.openPage();

        catalog.cards().shouldBe(sizeGreaterThan(0));
        catalog.names().shouldBe(allMatch("название не пустое",
                element -> !element.getText().isBlank()));

        assertTrue(catalog.visiblePrices().stream().allMatch(price -> price.signum() > 0),
                "все цены должны быть положительными");
    }

    @Test
    @Story("Сортировка")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("При сортировке по цене каталог показывает то же, что отдаёт API")
    @Description("""
            Эталоном служит ответ API на тот же запрос сортировки. Сравнение всего списка
            целиком заодно решает проблему асинхронной перерисовки: Selenide дожидается,
            пока содержимое страницы совпадёт с ожидаемым, вместо чтения устаревшей выдачи.
            """)
    void catalogMatchesApiOrderWhenSortedByPrice() {
        List<String> expectedOrder = ProductsApi.sorted("price,asc").data()
                .stream().map(Product::name).toList();

        catalog.openPage().sortBy("price,asc");

        catalog.names().shouldHave(exactTexts(expectedOrder));

        List<BigDecimal> prices = catalog.visiblePrices();
        assertFalse(prices.isEmpty(), "каталог не должен быть пустым");
        for (int i = 1; i < prices.size(); i++) {
            assertTrue(prices.get(i - 1).compareTo(prices.get(i)) <= 0,
                    "цены должны идти по возрастанию, но %s стоит перед %s"
                            .formatted(prices.get(i - 1), prices.get(i)));
        }
    }

    @Test
    @Story("Поиск")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Выдача поиска в браузере совпадает с выдачей API")
    @Description("""
            Поиск учитывает не только название, но и описание товара, поэтому проверять
            вхождение строки в название неверно: по запросу «pliers» законно находятся
            «Bolt Cutters». Эталон — ответ API на тот же запрос.
            """)
    void searchResultsMatchApi() {
        String query = "pliers";
        List<String> expected = ProductsApi.search(query).data()
                .stream().map(Product::name).toList();

        catalog.openPage().search(query);

        catalog.names().shouldHave(exactTexts(expected));
    }
}
