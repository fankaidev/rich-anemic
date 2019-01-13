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
import net.fklj.richanemic.rdm.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import static net.fklj.richanemic.data.Constants.PRODUCT_MAX_PRICE;
import static net.fklj.richanemic.data.Constants.PRODUCT_QUOTA_INFINITY;

@Slf4j
@Setter
@NoArgsConstructor
public class ProductEntity extends Product implements AggregateRoot {

    private ProductRepository productRepository;

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
        Variant variant = new VariantEntity(id, quota);
        productRepository.saveVariant(variant);
        return variant.getId();
    }

    private void checkQuota(int requiredQuota) throws CommerceException {
        if (getQuota() == PRODUCT_QUOTA_INFINITY) {
            return;
        }
        if (requiredQuota == PRODUCT_QUOTA_INFINITY) {
            throw new VariantQuotaException();
        }
        List<Variant> variants = productRepository.getVariantByProductId(id);
        int totalVariantQuota = variants.stream().map(Variant::getQuota).reduce(0, (a, b) -> a+b);
        if (totalVariantQuota + requiredQuota > getQuota()) {
            throw new VariantQuotaException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void activate() {
        this.status = ProductStatus.ACTIVE;
        save();
    }

    @Transactional(rollbackFor = Exception.class)
    public void inactivate() {
        this.status = ProductStatus.INACTIVE;
        save();
    }

    @Transactional(rollbackFor = Exception.class)
    public void useQuota(int variantId, int quantity) throws CommerceException {
        if (status == ProductStatus.INACTIVE) {
            throw new InactiveProductException();
        }
        soldCount += quantity;

        VariantEntity variant = getVariant(variantId);
        variant.useQuota(quantity);
        save();
    }

    @Transactional(rollbackFor = Exception.class)
    public void releaseQuota(int variantId, int quantity) throws CommerceException {
        if (soldCount < quantity) {
            throw new InvalidQuantityException();
        }
        soldCount -= quantity;

        VariantEntity variant = getVariant(variantId);
        variant.releaseQuota(quantity);
        save();
    }

    private VariantEntity getVariant(int variantId) throws CommerceException {
        VariantEntity variant = productRepository.getVariantEntity(variantId).orElseThrow(InvalidVariantException::new);
        if (variant.getProductId() != this.id) {
            throw new VariantMismatchException();
        }
        return variant;
    }

    private void save() {
        productRepository.saveProduct(this);
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
