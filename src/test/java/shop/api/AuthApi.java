package shop.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import shop.model.LoginRequest;

import static io.restassured.RestAssured.given;
import static shop.config.Specs.authorized;
import static shop.config.Specs.request;
import static shop.config.Specs.status;

public final class AuthApi {

    private AuthApi() {
    }

    @Step("Войти под {email}")
    public static ValidatableResponse login(String email, String password, int expectedStatus) {
        return given()
                .spec(request())
                .body(new LoginRequest(email, password))
                .when()
                .post("/users/login")
                .then()
                .spec(status(expectedStatus));
    }

    @Step("Запросить профиль по токену")
    public static ValidatableResponse me(String accessToken, int expectedStatus) {
        return given()
                .spec(authorized(accessToken))
                .when()
                .get("/users/me")
                .then()
                .spec(status(expectedStatus));
    }

    @Step("Запросить защищённый ресурс {path} без токена")
    public static ValidatableResponse withoutToken(String path, int expectedStatus) {
        return given()
                .spec(request())
                .when()
                .get(path)
                .then()
                .spec(status(expectedStatus));
    }
}
