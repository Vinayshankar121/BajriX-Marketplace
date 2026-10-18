package com.bajrix.marketplace.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ListingRequest {
    @NotNull private Long productId;
    @NotNull @DecimalMin(value = "0.01") @Digits(integer = 10, fraction = 2) private BigDecimal price;
    @NotNull @Min(0) private Integer stock;
    @NotNull @Min(1) private Integer minimumOrderQuantity;
    private boolean available = true;

    @AssertTrue(message = "Minimum order quantity cannot exceed available stock")
    public boolean isMinimumOrderWithinStock() {
        return !available || stock == null || stock == 0 || minimumOrderQuantity == null
                || minimumOrderQuantity <= stock;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getMinimumOrderQuantity() { return minimumOrderQuantity; }
    public void setMinimumOrderQuantity(Integer minimumOrderQuantity) { this.minimumOrderQuantity = minimumOrderQuantity; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
