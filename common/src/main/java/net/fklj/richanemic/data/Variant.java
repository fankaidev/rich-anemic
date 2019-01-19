package net.fklj.richanemic.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fklj.richanemic.data.CommerceException.InactiveVariantException;
import net.fklj.richanemic.data.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.data.CommerceException.ProductOutOfStockException;

import javax.persistence.Entity;
import javax.persistence.Id;

import static net.fklj.richanemic.data.Constants.PRODUCT_QUOTA_INFINITY;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Variant {

    @Id
    protected int id;

    protected int productId;

    protected int quota;

    protected int soldCount;

    protected VariantStatus status;

    public boolean isOutOfStock(int quantity) {
        return quota != PRODUCT_QUOTA_INFINITY && quota < soldCount + quantity;
    }

    public void activate() {
        this.status = VariantStatus.ACTIVE;
    }

    public void inactivate() {
        this.status = VariantStatus.INACTIVE;
    }

    public void useQuota(int quantity) throws ProductOutOfStockException, InactiveVariantException {
        if (status == VariantStatus.INACTIVE) {
            throw new InactiveVariantException();
        }
        if (isOutOfStock(quantity)) {
            throw new ProductOutOfStockException();
        }
        this.soldCount += quantity;
    }

    public void releaseQuota(int quantity) throws InvalidQuantityException {
        if (soldCount < quantity) {
            throw new InvalidQuantityException();
        }
        this.soldCount -= quantity;
    }
}
