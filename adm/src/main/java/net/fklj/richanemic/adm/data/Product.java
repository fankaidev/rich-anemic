package net.fklj.richanemic.adm.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static net.fklj.richanemic.data.Constants.PRODUCT_QUOTA_INFINITY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private int id;

    private int price;

    private int quota;

    private int soldCount;

    private ProductStatus status;

    public boolean isOutOfStock(int quantity) {
        return quota != PRODUCT_QUOTA_INFINITY && quota < soldCount + quantity;
    }
}
