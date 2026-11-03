package shop.config;

import java.util.Optional;

/**
 * Конфигурация берётся из системных свойств, с запасным вариантом в переменных окружения.
 * Учётные данные в репозиторий не попадают: локально — через -D, в CI — через GitHub Secrets.
 */
public final class Config {

    private Config() {
    }

    public static String baseUri() {
        return resolve("toolshop.baseUri", "TOOLSHOP_BASE_URI")
                .orElse("https://api.practicesoftwaretesting.com");
    }

    public static String uiBaseUrl() {
        return resolve("toolshop.uiBaseUrl", "TOOLSHOP_UI_BASE_URL")
                .orElse("https://practicesoftwaretesting.com");
    }

    public static Optional<Credentials> credentials() {
        Optional<String> email = resolve("toolshop.email", "TOOLSHOP_EMAIL");
        Optional<String> password = resolve("toolshop.password", "TOOLSHOP_PASSWORD");

        if (email.isPresent() && password.isPresent()) {
            return Optional.of(new Credentials(email.get(), password.get()));
        }
        return Optional.empty();
    }

    private static Optional<String> resolve(String systemProperty, String environmentVariable) {
        return Optional
                .ofNullable(System.getProperty(systemProperty))
                .or(() -> Optional.ofNullable(System.getenv(environmentVariable)))
                .map(String::trim)
                .filter(value -> !value.isEmpty());
    }

    public record Credentials(String email, String password) {
    }
}
