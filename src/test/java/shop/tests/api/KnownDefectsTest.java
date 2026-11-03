package shop.tests.api;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.tests.BaseApiTest;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static shop.config.Specs.request;
import static shop.config.Specs.status;

/**
 * Тесты фиксируют поведение, которое приложение демонстрирует сейчас и которое
 * считается дефектным. Они зелёные: их задача — поймать момент, когда поведение
 * изменится, и служить воспроизведением для баг-репорта.
 */
@Epic("Toolshop API")
@Feature("Известные дефекты")
@DisplayName("Зафиксированные дефекты контракта")
class KnownDefectsTest extends BaseApiTest {

    @Test
    @Story("Авторизация проверяется позже валидации")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание товара без токена отдаёт 500 вместо 401")
    @Description("""
            Анонимный POST /products с синтаксически корректным телом завершается
            внутренней ошибкой сервера. Ожидаемое поведение — 401 Unauthorized:
            авторизация должна проверяться до обработки тела запроса.
            Текущее поведение зафиксировано, чтобы отследить починку.
            """)
    void anonymousProductCreationFailsWithServerError() {
        Map<String, Object> product = Map.of(
                "name", "Probe",
                "description", "Проверка порядка проверок: авторизация или валидация",
                "price", 9.99,
                "category_id", "999999",
                "brand_id", "999999",
                "product_image_id", "999999",
                "is_location_offer", false,
                "is_rental", false
        );

        given()
                .spec(request())
                .body(product)
                .when()
                .post("/products")
                .then()
                .spec(status(500)); // ожидаемо должно быть 401
    }
}
