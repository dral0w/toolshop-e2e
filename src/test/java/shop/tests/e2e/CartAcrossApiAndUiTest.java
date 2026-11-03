package shop.tests.e2e;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import shop.api.CartApi;
import shop.api.ProductsApi;
import shop.model.Cart;
import shop.model.CartItem;
import shop.model.Product;
import shop.tests.BaseUiTest;
import shop.ui.components.Header;
import shop.ui.pages.CartPage;
import shop.ui.pages.ProductPage;

import java.math.BigDecimal;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Смешанные сценарии: состояние готовится и проверяется по разные стороны системы.
 * Корзина фронтенда привязана к ключу cart_id в sessionStorage, поэтому корзину,
 * созданную запросами, можно отдать браузеру и наоборот.
 */
@Tag("e2e")
@Epic("Toolshop E2E")
@Feature("Корзина: API и UI")
@DisplayName("Корзина одинакова со стороны API и со стороны браузера")
class CartAcrossApiAndUiTest extends BaseUiTest {

    private static final int QUANTITY = 3;

    @Test
    @Story("Состояние готовится через API")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Корзина, собранная запросами, целиком отображается в интерфейсе")
    @Description("""
            Корзина создаётся и наполняется через API, затем её идентификатор отдаётся браузеру.
            Интерфейс должен показать тот же товар, то же количество и верные суммы —
            без единого клика на подготовку данных.
            """)
    void cartFilledViaApiIsRenderedInUi() {
        Product product = ProductsApi.firstInStock();
        String cartId = CartApi.create();
        CartApi.addItem(cartId, product.id(), QUANTITY);

        handCartOverToBrowser(cartId);

        CartPage cart = new CartPage().openPage();
        BigDecimal expectedLine = product.price().multiply(BigDecimal.valueOf(QUANTITY));

        cart.titles().first().shouldHave(exactText(product.name()));
        cart.quantityOf(0).shouldHave(value(String.valueOf(QUANTITY)));
        assertAll(
                () -> assertEquals(0, expectedLine.compareTo(cart.linePrice(0)),
                        "сумма позиции должна быть ценой, умноженной на количество"),
                () -> assertEquals(0, expectedLine.compareTo(cart.total()),
                        "итог корзины должен совпадать с суммой единственной позиции")
        );
    }

    @Test
    @Story("Состояние создаётся в UI")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Товар, добавленный в браузере, виден в корзине через API")
    @Description("""
            Обратное направление: товар кладётся в корзину кликами, после чего корзина
            читается запросом по идентификатору из sessionStorage. Проверяем, что
            фронтенд и бэкенд видят одно и то же.
            """)
    void productAddedInUiIsVisibleThroughApi() {
        Product product = ProductsApi.firstInStock();

        new ProductPage().openPage(product.id()).addToCart();
        new Header().cartLink().shouldBe(visible);

        Cart cart = CartApi.get(cartIdFromBrowser());

        assertEquals(1, cart.cartItems().size(), "в корзине должна быть одна позиция");
        CartItem item = cart.cartItems().getFirst();
        assertAll(
                () -> assertEquals(product.id(), item.product().id(), "в корзину попал не тот товар"),
                () -> assertEquals(1, item.quantity(), "количество должно быть 1")
        );
    }

    @Step("Отдать браузеру корзину {cartId}, подготовленную через API")
    private void handCartOverToBrowser(String cartId) {
        open("/"); // sessionStorage доступен только после загрузки страницы нужного origin
        executeJavaScript(
                "sessionStorage.setItem('cart_id', arguments[0]); sessionStorage.setItem('cart_quantity', arguments[1]);",
                cartId, String.valueOf(QUANTITY));
    }

    @Step("Прочитать идентификатор корзины из sessionStorage")
    private String cartIdFromBrowser() {
        String cartId = executeJavaScript("return sessionStorage.getItem('cart_id');");
        assertNotNull(cartId, "после добавления товара браузер должен хранить cart_id");
        return cartId;
    }
}
