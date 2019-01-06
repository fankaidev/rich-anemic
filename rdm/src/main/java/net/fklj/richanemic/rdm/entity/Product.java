package net.fklj.richanemic.rdm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InvalidVariantException;
import net.fklj.richanemic.data.CommerceException.VariantQuotaException;
import net.fklj.richanemic.data.ProductStatus;
import net.fklj.richanemic.data.VariantStatus;
import net.fklj.richanemic.rdm.repository.OrderRepository;
import net.fklj.richanemic.rdm.repository.ProductRepository;

import java.util.List;
import java.util.Random;

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

    private OrderRepository orderRepository;

    private ProductRepository productRepository;

    public boolean isOutOfStock(int quantity) {
        return quota != PRODUCT_QUOTA_INFINITY && quota < soldCount + quantity;
    }

    public void activate() {
        this.status = ProductStatus.ACTIVE;
        save();
    }

    public void inactivate() {
        this.status = ProductStatus.INACTIVE;
        save();
    }

    public void useQuota(int quantity) {
        soldCount += quantity;
        save();
    }

    public void releaseQuota(int quantity) {
        soldCount -= quantity;
        save();
    }

    private void save() {
        getProductRepository().saveProduct(this);
    }

    public ProductRepository getProductRepository() {return this.productRepository;}

    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public int createVariant(int quota) throws CommerceException {
        if (quota < 0) {
            throw new InvalidVariantException();
        }
        int variantId = new Random().nextInt();
        Variant variant = Variant.builder()
                .id(variantId)
                .productId(id)
                .quota(quota)
                .status(VariantStatus.INACTIVE)
                .build();

        checkQuotaForVariant(quota);

        productRepository.saveVariant(variant);
        return variantId;
    }

    private void checkQuotaForVariant(int requiredQuota) throws CommerceException {
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

}
