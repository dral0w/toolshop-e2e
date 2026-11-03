package shop.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public final class Specs {

    private Specs() {
    }

    public static RequestSpecification request() {
        return new RequestSpecBuilder()
                .setBaseUri(Config.baseUri())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(allureFilter())
                // полный запрос и ответ лежат в Allure; в консоль — только URI,
                // а тело ответа печатается лишь когда сервер ответил ошибкой
                .addFilter(new ErrorLoggingFilter())
                .log(LogDetail.URI)
                .build();
    }

    public static RequestSpecification authorized(String accessToken) {
        return new RequestSpecBuilder()
                .addRequestSpecification(request())
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
    }

    public static ResponseSpecification ok() {
        return status(200);
    }

    public static ResponseSpecification created() {
        return status(201);
    }

    // Время ответа намеренно не проверяется: стенд общий и бесплатный, его задержки —
    // свойство окружения, а не контракта. Такая проверка даёт ложные падения.
    public static ResponseSpecification status(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .build();
    }

    private static AllureRestAssured allureFilter() {
        AllureRestAssured filter = new AllureRestAssured();
        filter.setRequestTemplate("http-request.ftl");
        filter.setResponseTemplate("http-response.ftl");
        return filter;
    }
}
