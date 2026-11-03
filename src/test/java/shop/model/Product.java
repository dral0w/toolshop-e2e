package shop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Product(
        String id,
        String name,
        String description,
        BigDecimal price,
        Boolean isLocationOffer,
        Boolean isRental,
        Boolean inStock,
        Brand brand,
        Category category
) {
}
