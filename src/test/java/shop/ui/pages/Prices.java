package shop.ui.pages;

import java.math.BigDecimal;

/** Цены в интерфейсе выводятся как «$14.15», в API — числом. */
public final class Prices {

    private Prices() {
    }

    public static BigDecimal parse(String displayed) {
        return new BigDecimal(displayed.replace("$", "").replace(",", "").trim());
    }
}
