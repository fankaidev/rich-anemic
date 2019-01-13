package net.fklj.richanemic.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fklj.richanemic.data.VariantStatus;

import static net.fklj.richanemic.data.Constants.PRODUCT_QUOTA_INFINITY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Variant {

    protected int id;

    protected int productId;

    protected int quota;

    protected int soldCount;

    protected VariantStatus status;

    public boolean isOutOfStock(int quantity) {
        return quota != PRODUCT_QUOTA_INFINITY && quota < soldCount + quantity;
    }

}
