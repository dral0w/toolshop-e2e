package shop.config;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Окружение прогона для главной страницы Allure-отчёта. */
public final class AllureEnvironment {

    private AllureEnvironment() {
    }

    public static void write() {
        Path results = Path.of(System.getProperty("allure.results.directory", "target/allure-results"));

        Properties environment = new Properties();
        environment.setProperty("API base URI", Config.baseUri());
        environment.setProperty("UI base URL", Config.uiBaseUrl());
        environment.setProperty("Credentials provided", String.valueOf(Config.credentials().isPresent()));
        environment.setProperty("Java", System.getProperty("java.version"));
        environment.setProperty("OS", System.getProperty("os.name"));

        try {
            Files.createDirectories(results);
            try (OutputStream out = Files.newOutputStream(results.resolve("environment.properties"))) {
                environment.store(out, "Allure environment");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("не удалось записать окружение для отчёта", e);
        }
    }
}
