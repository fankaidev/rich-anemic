package net.fklj.richanemic.rdm.entity;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.CommerceException.InactiveVariantException;
import net.fklj.richanemic.data.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.data.CommerceException.InvalidVariantException;
import net.fklj.richanemic.data.CommerceException.ProductOutOfStockException;
import net.fklj.richanemic.data.Variant;
import net.fklj.richanemic.data.VariantStatus;
import net.fklj.richanemic.rdm.repository.ProductRepository;

import java.util.Random;

@Slf4j
@Setter
@NoArgsConstructor
public class VariantEntity extends Variant {

    private ProductRepository productRepository;

    public VariantEntity(int productId, int quota) throws InvalidVariantException {
        super(new Random().nextInt(), productId, quota, 0, VariantStatus.INACTIVE);
        if (quota < 0) {
            log.error("create variant with invalid quota {}", quota);
            throw new InvalidVariantException();
        }
    }

    public void activate() {
        this.status = VariantStatus.ACTIVE;
        save();
    }

    public void inactivate() {
        this.status = VariantStatus.INACTIVE;
        save();
    }

    public void useQuota(int quantity) throws ProductOutOfStockException, InactiveVariantException {
        if (status == VariantStatus.INACTIVE) {
            throw new InactiveVariantException();
        }
        if (isOutOfStock(quantity)) {
            throw new ProductOutOfStockException();
        }
        this.soldCount += quantity;
        save();
    }

    public void releaseQuota(int quantity) throws InvalidQuantityException {
        if (soldCount < quantity) {
            throw new InvalidQuantityException();
        }
        this.soldCount -= quantity;
        save();
    }

    private void save() {
        productRepository.saveVariant(this);
    }

}
