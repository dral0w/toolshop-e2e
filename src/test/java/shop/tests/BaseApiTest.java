package shop.tests;

import org.junit.jupiter.api.BeforeAll;
import shop.config.Config;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Кладёт окружение прогона на главную страницу Allure-отчёта. */
public abstract class BaseApiTest {

    @BeforeAll
    static void writeAllureEnvironment() throws IOException {
        Path results = Path.of(System.getProperty("allure.results.directory", "target/allure-results"));
        Files.createDirectories(results);

        Properties environment = new Properties();
        environment.setProperty("Base URI", Config.baseUri());
        environment.setProperty("Credentials provided", String.valueOf(Config.credentials().isPresent()));
        environment.setProperty("Java", System.getProperty("java.version"));
        environment.setProperty("OS", System.getProperty("os.name"));

        try (OutputStream out = Files.newOutputStream(results.resolve("environment.properties"))) {
            environment.store(out, "Allure environment");
        }
    }
}
