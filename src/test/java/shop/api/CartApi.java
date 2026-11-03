package shop.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import shop.model.AddToCartRequest;
import shop.model.Cart;

import static io.restassured.RestAssured.given;
import static shop.config.Specs.created;
import static shop.config.Specs.ok;
import static shop.config.Specs.request;
import static shop.config.Specs.status;

public final class CartApi {

    private CartApi() {
    }

    @Step("Создать корзину")
    public static String create() {
        return given()
                .spec(request())
                .when()
                .post("/carts")
                .then()
                .spec(created())
                .extract()
                .path("id");
    }

    @Step("Положить в корзину товар {productId} в количестве {quantity}")
    public static ValidatableResponse addItem(String cartId, String productId, int quantity) {
        return given()
                .spec(request())
                .body(new AddToCartRequest(productId, quantity))
                .when()
                .post("/carts/{cartId}", cartId)
                .then()
                .spec(ok());
    }

    @Step("Изменить количество товара {productId} на {quantity}")
    public static ValidatableResponse updateQuantity(String cartId, String productId, int quantity) {
        return given()
                .spec(request())
                .body(new AddToCartRequest(productId, quantity))
                .when()
                .put("/carts/{cartId}/product/quantity", cartId)
                .then()
                .spec(ok());
    }

    @Step("Удалить товар {productId} из корзины")
    public static ValidatableResponse removeItem(String cartId, String productId) {
        return given()
                .spec(request())
                .when()
                .delete("/carts/{cartId}/product/{productId}", cartId, productId)
                .then()
                .spec(status(204));
    }

    @Step("Прочитать корзину {cartId}")
    public static Cart get(String cartId) {
        return given()
                .spec(request())
                .when()
                .get("/carts/{cartId}", cartId)
                .then()
                .spec(ok())
                .extract()
                .as(Cart.class);
    }
}
