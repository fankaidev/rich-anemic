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
public class Variant {

    private int id;

    private int productId;

    private int quota;

    private int soldCount;

    private VariantStatus status;

    public boolean isOutOfStock(int quantity) {
        return quota != PRODUCT_QUOTA_INFINITY && quota < soldCount + quantity;
    }

}
