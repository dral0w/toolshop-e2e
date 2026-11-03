package shop.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import shop.model.ProductPage;

import static io.restassured.RestAssured.given;
import static shop.config.Specs.ok;
import static shop.config.Specs.request;
import static shop.config.Specs.status;

public final class ProductsApi {

    private ProductsApi() {
    }

    @Step("Получить страницу каталога {page}")
    public static ProductPage page(int page) {
        return pageResponse(page).extract().as(ProductPage.class);
    }

    @Step("Запросить страницу каталога {page}")
    public static ValidatableResponse pageResponse(int page) {
        return given()
                .spec(request())
                .queryParam("page", page)
                .when()
                .get("/products")
                .then()
                .spec(ok());
    }

    @Step("Отсортировать каталог: {sort}")
    public static ProductPage sorted(String sort) {
        return given()
                .spec(request())
                .queryParam("sort", sort)
                .queryParam("page", 1)
                .when()
                .get("/products")
                .then()
                .spec(ok())
                .extract()
                .as(ProductPage.class);
    }

    @Step("Отфильтровать каталог по бренду {brandId}")
    public static ProductPage byBrand(String brandId) {
        return given()
                .spec(request())
                .queryParam("by_brand", brandId)
                .queryParam("page", 1)
                .when()
                .get("/products")
                .then()
                .spec(ok())
                .extract()
                .as(ProductPage.class);
    }

    @Step("Найти товары по запросу «{query}»")
    public static ProductPage search(String query) {
        return given()
                .spec(request())
                .queryParam("q", query)
                .when()
                .get("/products/search")
                .then()
                .spec(ok())
                .extract()
                .as(ProductPage.class);
    }

    @Step("Запросить товар {productId}")
    public static ValidatableResponse byId(String productId, int expectedStatus) {
        return given()
                .spec(request())
                .when()
                .get("/products/{id}", productId)
                .then()
                .spec(status(expectedStatus));
    }
}
