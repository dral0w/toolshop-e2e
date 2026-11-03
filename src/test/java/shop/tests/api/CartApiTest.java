package shop.tests.api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.api.CartApi;
import shop.api.ProductsApi;
import shop.model.Cart;
import shop.model.CartItem;
import shop.model.Product;
import shop.tests.BaseApiTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Toolshop API")
@Feature("Корзина")
@DisplayName("/carts — жизненный цикл корзины")
class CartApiTest extends BaseApiTest {

    private String cartId;
    private Product product;

    @BeforeEach
    void prepareCart() {
        cartId = CartApi.create();
        product = ProductsApi.page(1).data().getFirst();
    }

    @Test
    @Story("Создание корзины")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Новая корзина создаётся пустой")
    void newCartIsEmpty() {
        Cart cart = CartApi.get(cartId);

        assertAll(
                () -> assertNotNull(cart.id(), "у корзины должен быть id"),
                () -> assertEquals(cartId, cart.id(), "вернулась не та корзина"),
                () -> assertTrue(cart.cartItems().isEmpty(), "новая корзина должна быть пустой")
        );
    }

    @Test
    @Story("Добавление товара")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Добавленный товар попадает в корзину с нужным количеством")
    void addedProductAppearsInCart() {
        CartApi.addItem(cartId, product.id(), 2);

        Cart cart = CartApi.get(cartId);

        assertEquals(1, cart.cartItems().size(), "в корзине должна быть одна позиция");
        CartItem item = cart.cartItems().getFirst();
        assertAll(
                () -> assertEquals(product.id(), item.product().id(), "в корзине не тот товар"),
                () -> assertEquals(2, item.quantity(), "неверное количество")
        );
    }

    @Test
    @Story("Изменение количества")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Количество товара в корзине меняется")
    void productQuantityCanBeChanged() {
        CartApi.addItem(cartId, product.id(), 1);

        CartApi.updateQuantity(cartId, product.id(), 5);

        assertEquals(5, CartApi.get(cartId).cartItems().getFirst().quantity(),
                "количество должно было стать 5");
    }

    @Test
    @Story("Удаление товара")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Удалённый товар исчезает из корзины")
    void removedProductDisappearsFromCart() {
        CartApi.addItem(cartId, product.id(), 1);

        CartApi.removeItem(cartId, product.id());

        assertTrue(CartApi.get(cartId).cartItems().isEmpty(), "корзина должна опустеть");
    }
}
