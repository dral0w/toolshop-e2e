package shop.tests.api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import shop.api.AuthApi;
import shop.config.Config;
import shop.model.TokenResponse;
import shop.tests.BaseApiTest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Toolshop API")
@Feature("Авторизация")
@DisplayName("Доступ к защищённым ресурсам")
class AuthorizationTest extends BaseApiTest {

    @ParameterizedTest(name = "GET {0} без токена → 401")
    @ValueSource(strings = {"/users", "/users/me"})
    @Story("Защита эндпоинтов")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Защищённые ресурсы недоступны без токена")
    void protectedEndpointsRejectAnonymousRequests(String path) {
        AuthApi.withoutToken(path, 401);
    }

    @Test
    @Story("Вход")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Вход с неизвестными учётными данными отклоняется с 401")
    void loginWithUnknownCredentialsIsRejected() {
        AuthApi.login("nobody-" + System.nanoTime() + "@example.com", "definitely-wrong", 401);
    }

    @Test
    @Story("Вход")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Вход с корректными данными возвращает Bearer-токен")
    void loginReturnsBearerToken() {
        Config.Credentials credentials = requireCredentials();

        TokenResponse token = AuthApi
                .login(credentials.email(), credentials.password(), 200)
                .body("access_token", not(emptyOrNullString()))
                .body("token_type", equalTo("Bearer"))
                .extract()
                .as(TokenResponse.class);

        assertAll(
                () -> assertEquals("Bearer", token.tokenType(), "тип токена"),
                () -> AuthApi.me(token.accessToken(), 200)
                        .body("email", equalTo(credentials.email()))
        );
    }

    /**
     * Учётные данные не хранятся в репозитории. Локально: -Dtoolshop.email=... -Dtoolshop.password=...
     * В CI: переменные окружения TOOLSHOP_EMAIL / TOOLSHOP_PASSWORD из GitHub Secrets.
     * Без них тест осознанно пропускается, а не падает.
     */
    private static Config.Credentials requireCredentials() {
        return Config.credentials().orElseGet(() -> {
            Assumptions.abort("учётные данные не заданы: -Dtoolshop.email / -Dtoolshop.password");
            return null;
        });
    }
}
