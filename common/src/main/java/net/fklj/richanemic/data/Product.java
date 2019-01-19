package net.fklj.richanemic.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.CommerceException.InactiveProductException;
import net.fklj.richanemic.data.CommerceException.InvalidProductException;
import net.fklj.richanemic.data.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.data.CommerceException.InvalidVariantException;
import net.fklj.richanemic.data.CommerceException.VariantMismatchException;
import net.fklj.richanemic.data.CommerceException.VariantQuotaException;
import net.fklj.richanemic.data.ProductStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.fklj.richanemic.data.Constants.PRODUCT_MAX_PRICE;
import static net.fklj.richanemic.data.Constants.PRODUCT_QUOTA_INFINITY;

@Slf4j
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    protected int id;

    protected int price;

    protected int quota;

    protected int soldCount;

    protected ProductStatus status;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected List<Variant> variants;

    public Product(int price1, int quota1) throws InvalidProductException {
        this(new Random().nextInt(), price1, quota1, 0, ProductStatus.INACTIVE, Collections.emptyList());
        if (price1 <= 0 || price1 > PRODUCT_MAX_PRICE) {
            log.error("create product with invalid price {}", price1);
            throw new InvalidProductException();
        }
        if (quota1 < 0) {
            log.error("create product with invalid quota {}", quota1);
            throw new InvalidProductException();
        }
    }

    public boolean isOutOfStock(int quantity) {
        return quota != PRODUCT_QUOTA_INFINITY && quota < soldCount + quantity;
    }


    @Transactional(rollbackFor = Exception.class)
    public int createVariant(int quota) throws CommerceException {
        checkQuota(quota);
        int productId = id;
        int quota1 = quota;
        Variant variant = new Variant(new Random().nextInt(), productId, quota1, 0,
                VariantStatus.INACTIVE) {
            {
                if (quota1 < 0) {
                    //            log.error("create variant with invalid quota {}", quota);
                    throw new InvalidVariantException();
                }
            }

        };
        variants.add(variant);
        return variant.getId();
    }

    private void checkQuota(int requiredQuota) throws CommerceException {
        if (getQuota() == PRODUCT_QUOTA_INFINITY) {
            return;
        }
        if (requiredQuota == PRODUCT_QUOTA_INFINITY) {
            throw new VariantQuotaException();
        }
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

        Variant variant = getVariant(variantId);
        variant.useQuota(quantity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void releaseQuota(int variantId, int quantity) throws CommerceException {
        if (soldCount < quantity) {
            throw new InvalidQuantityException();
        }
        soldCount -= quantity;

        Variant variant = getVariant(variantId);
        variant.releaseQuota(quantity);
    }

    private Variant getVariant(int variantId) throws CommerceException {
        Variant variant = variants.stream().filter(it -> it.getId() == variantId).findFirst().orElseThrow(InvalidVariantException::new);
        if (variant.getProductId() != this.id) {
            throw new VariantMismatchException();
        }
        return variant;
    }

    @Transactional(rollbackFor = Exception.class)
    public void inactivateVariant(int variantId) throws CommerceException {
        Variant variant = getVariant(variantId);
        variant.inactivate();
    }

    @Transactional(rollbackFor = Exception.class)
    public void activateVariant(int variantId) throws CommerceException {
        Variant variant = getVariant(variantId);
        variant.activate();
    }

}
