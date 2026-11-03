package shop.tests.api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.api.BrandsApi;
import shop.api.ProductsApi;
import shop.model.Brand;
import shop.model.Product;
import shop.model.ProductPage;
import shop.tests.BaseApiTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Toolshop API")
@Feature("Каталог товаров")
@DisplayName("GET /products — каталог")
class ProductsApiTest extends BaseApiTest {

    @Test
    @Story("Контракт ответа")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Ответ каталога соответствует JSON Schema")
    void catalogMatchesJsonSchema() {
        ProductsApi.pageResponse(1)
                .body(matchesJsonSchemaInClasspath("schemas/product-page.json"));
    }

    @Test
    @Story("Пагинация")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Страницы каталога не повторяются и отдают корректные метаданные")
    void catalogIsPaginated() {
        ProductPage first = ProductsApi.page(1);
        ProductPage second = ProductsApi.page(2);

        assertAll(
                () -> assertEquals(1, first.currentPage(), "номер первой страницы"),
                () -> assertEquals(2, second.currentPage(), "номер второй страницы"),
                () -> assertTrue(first.total() > 0, "каталог не должен быть пустым"),
                () -> assertFalse(first.data().isEmpty(), "на первой странице должны быть товары"),
                () -> assertTrue(first.data().size() <= first.perPage(), "товаров не больше, чем per_page"),
                () -> assertNotSamePage(first, second)
        );
    }

    @Test
    @Story("Сортировка")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Сортировка по возрастанию цены действительно сортирует")
    void catalogSortsByPriceAscending() {
        List<Product> products = ProductsApi.sorted("price,asc").data();

        assertFalse(products.isEmpty(), "выдача не должна быть пустой");

        for (int i = 1; i < products.size(); i++) {
            BigDecimal previous = products.get(i - 1).price();
            BigDecimal current = products.get(i).price();
            assertTrue(previous.compareTo(current) <= 0,
                    "цены должны идти по возрастанию, но %s стоит перед %s".formatted(previous, current));
        }
    }

    @Test
    @Story("Фильтрация")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Фильтр по бренду отдаёт товары только этого бренда")
    void catalogFiltersByBrand() {
        Brand brand = BrandsApi.all().getFirst();

        List<Product> products = ProductsApi.byBrand(brand.id()).data();

        assertFalse(products.isEmpty(), "у бренда %s должны быть товары".formatted(brand.name()));
        products.forEach(product -> assertEquals(brand.id(), product.brand().id(),
                "товар «%s» не принадлежит бренду %s".formatted(product.name(), brand.name())));
    }

    @Test
    @Story("Поиск")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Поиск отдаёт товары, относящиеся к запросу")
    void searchReturnsRelevantProducts() {
        String query = "pliers";

        List<Product> found = ProductsApi.search(query).data();

        assertFalse(found.isEmpty(), "по запросу «%s» должны быть результаты".formatted(query));
        found.forEach(product -> assertTrue(mentions(product, query),
                "товар «%s» не относится к запросу «%s»".formatted(product.name(), query)));
    }

    @Test
    @Story("Контракт ответа")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Несуществующий товар отдаёт 404")
    void unknownProductReturnsNotFound() {
        ProductsApi.byId("does-not-exist-123", 404);
    }

    private static boolean mentions(Product product, String query) {
        String haystack = (product.name() + " " + String.valueOf(product.description()))
                .toLowerCase(Locale.ROOT);
        return haystack.contains(query.toLowerCase(Locale.ROOT));
    }

    private static void assertNotSamePage(ProductPage first, ProductPage second) {
        if (first.lastPage() < 2) {
            return; // в каталоге всего одна страница — сравнивать нечего
        }
        assertFalse(first.data().getFirst().id().equals(second.data().getFirst().id()),
                "вторая страница повторяет первую");
    }
}
