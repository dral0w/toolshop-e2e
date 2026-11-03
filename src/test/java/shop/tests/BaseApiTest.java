package shop.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import shop.config.AllureEnvironment;

@Tag("api")
public abstract class BaseApiTest {

    @BeforeAll
    static void writeAllureEnvironment() {
        AllureEnvironment.write();
    }
}
