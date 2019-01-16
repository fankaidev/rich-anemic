package net.fklj.richanemic.rdm.entity.product;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InactiveProductException;
import net.fklj.richanemic.data.CommerceException.InvalidProductException;
import net.fklj.richanemic.data.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.data.CommerceException.InvalidVariantException;
import net.fklj.richanemic.data.CommerceException.VariantMismatchException;
import net.fklj.richanemic.data.CommerceException.VariantQuotaException;
import net.fklj.richanemic.data.Product;
import net.fklj.richanemic.data.ProductStatus;
import net.fklj.richanemic.data.Variant;
import net.fklj.richanemic.rdm.entity.AggregateRoot;
import net.fklj.richanemic.rdm.repository.VariantRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
import java.util.Random;

import static net.fklj.richanemic.data.Constants.PRODUCT_MAX_PRICE;
import static net.fklj.richanemic.data.Constants.PRODUCT_QUOTA_INFINITY;

@Slf4j
@Setter
@NoArgsConstructor
@Entity
public class ProductEntity extends Product implements AggregateRoot {

    @Id
    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public int getPrice() {
        return super.getPrice();
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
    public ProductStatus getStatus() {
        return super.getStatus();
    }

    private VariantRepository variantRepository;

    public ProductEntity(int id, int price, int quota, int soldCount, ProductStatus status) {
        super(id, price, quota, soldCount, status);
    }

    public ProductEntity(int price, int quota) throws InvalidProductException {
        this(new Random().nextInt(), price, quota, 0, ProductStatus.INACTIVE);
        if (price <= 0 || price > PRODUCT_MAX_PRICE) {
            log.error("create product with invalid price {}", price);
            throw new InvalidProductException();
        }
        if (quota < 0) {
            log.error("create product with invalid quota {}", quota);
            throw new InvalidProductException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int createVariant(int quota) throws CommerceException {
        checkQuota(quota);
        VariantEntity variant = new VariantEntity(id, quota);
        variantRepository.save(variant);
        return variant.getId();
    }

    private void checkQuota(int requiredQuota) throws CommerceException {
        if (getQuota() == PRODUCT_QUOTA_INFINITY) {
            return;
        }
        if (requiredQuota == PRODUCT_QUOTA_INFINITY) {
            throw new VariantQuotaException();
        }
        List<VariantEntity> variants = variantRepository.findByProductId(id);
        int totalVariantQuota = variants.stream().map(Variant::getQuota).reduce(0, (a, b) -> a+b);
        if (totalVariantQuota + requiredQuota > getQuota()) {
            throw new VariantQuotaException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void activate() {
        this.status = ProductStatus.ACTIVE;
    }

    @Transactional(rollbackFor = Exception.class)
    public void inactivate() {
        this.status = ProductStatus.INACTIVE;
    }

    @Transactional(rollbackFor = Exception.class)
    public void useQuota(int variantId, int quantity) throws CommerceException {
        if (status == ProductStatus.INACTIVE) {
            throw new InactiveProductException();
        }
        soldCount += quantity;

        VariantEntity variant = getVariant(variantId);
        variant.useQuota(quantity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void releaseQuota(int variantId, int quantity) throws CommerceException {
        if (soldCount < quantity) {
            throw new InvalidQuantityException();
        }
        soldCount -= quantity;

        VariantEntity variant = getVariant(variantId);
        variant.releaseQuota(quantity);
    }

    private VariantEntity getVariant(int variantId) throws CommerceException {
        VariantEntity variant = variantRepository.findById(variantId).orElseThrow(InvalidVariantException::new);
        if (variant.getProductId() != this.id) {
            throw new VariantMismatchException();
        }
        return variant;
    }

    @Transactional(rollbackFor = Exception.class)
    public void inactivateVariant(int variantId) throws CommerceException {
        VariantEntity variant = getVariant(variantId);
        variant.inactivate();
    }

    @Transactional(rollbackFor = Exception.class)
    public void activateVariant(int variantId) throws CommerceException {
        VariantEntity variant = getVariant(variantId);
        variant.activate();
    }
}
