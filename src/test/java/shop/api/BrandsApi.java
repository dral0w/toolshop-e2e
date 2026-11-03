package shop.api;

import io.qameta.allure.Step;
import shop.model.Brand;

import java.util.List;

import static io.restassured.RestAssured.given;
import static shop.config.Specs.ok;
import static shop.config.Specs.request;

public final class BrandsApi {

    private BrandsApi() {
    }

    @Step("Получить список брендов")
    public static List<Brand> all() {
        return given()
                .spec(request())
                .when()
                .get("/brands")
                .then()
                .spec(ok())
                .extract()
                .jsonPath()
                .getList("", Brand.class);
    }
}
