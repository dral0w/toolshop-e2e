package shop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddToCartRequest(@JsonProperty("product_id") String productId, int quantity) {
}
