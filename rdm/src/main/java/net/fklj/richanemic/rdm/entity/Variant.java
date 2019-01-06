package net.fklj.richanemic.rdm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fklj.richanemic.data.VariantStatus;
import net.fklj.richanemic.rdm.repository.ProductRepository;

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

    private ProductRepository productRepository;

    public boolean isOutOfStock(int quantity) {
        return quota != PRODUCT_QUOTA_INFINITY && quota < soldCount + quantity;
    }

    public void activate() {
        this.status = VariantStatus.ACTIVE;
        save();
    }

    public void inactivate() {
        this.status = VariantStatus.INACTIVE;
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
        getProductRepository().saveVariant(this);
    }

    public net.fklj.richanemic.rdm.repository.ProductRepository getProductRepository() {return this.productRepository;}

    public void setProductRepository(
            net.fklj.richanemic.rdm.repository.ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}
