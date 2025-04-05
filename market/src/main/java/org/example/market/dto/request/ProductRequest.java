package org.example.market.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class ProductRequest {
    @NotEmpty
    private String productName;
    @NotEmpty
    private String categoryProduct;
    @Positive
    @NotNull
    private Integer quantity;
    @Positive
    @NotNull
    private BigDecimal productPrice;

    public ProductRequest(String productName, Integer quantity,BigDecimal productPrice, String categoryProduct) {
        this.productName = productName;
        this.categoryProduct = categoryProduct;
        this.quantity = quantity;
        this.productPrice = productPrice;

    }
}
