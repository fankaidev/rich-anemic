package net.fklj.richanemic.rdm.entity.product;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.CommerceException.InactiveVariantException;
import net.fklj.richanemic.data.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.data.CommerceException.InvalidVariantException;
import net.fklj.richanemic.data.CommerceException.ProductOutOfStockException;
import net.fklj.richanemic.data.Variant;
import net.fklj.richanemic.data.VariantStatus;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Random;

@Slf4j
@Setter
@NoArgsConstructor
@Entity
public class VariantEntity extends Variant {

    @Id
    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public int getProductId() {
        return super.getProductId();
    }

    @Override
    public int getQuota() {
        return super.getQuota();
    }

    @Override
    public int getSoldCount() {
        return super.getSoldCount();
    }

    @Override
    public VariantStatus getStatus() {
        return super.getStatus();
    }

    public VariantEntity(int productId, int quota) throws InvalidVariantException {
        super(new Random().nextInt(), productId, quota, 0, VariantStatus.INACTIVE);
        if (quota < 0) {
            log.error("create variant with invalid quota {}", quota);
            throw new InvalidVariantException();
        }
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
