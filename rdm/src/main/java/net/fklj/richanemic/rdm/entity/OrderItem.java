package net.fklj.richanemic.rdm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.CreateOrderException;
import net.fklj.richanemic.data.CommerceException.InactiveProductException;
import net.fklj.richanemic.data.CommerceException.InactiveVariantException;
import net.fklj.richanemic.data.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.data.CommerceException.ProductOutOfStockException;
import net.fklj.richanemic.data.CommerceException.VariantMismatchException;
import net.fklj.richanemic.data.CommerceException.VariantOutOfStockException;
import net.fklj.richanemic.data.ProductStatus;
import net.fklj.richanemic.data.VariantStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private int id;

    private int orderId;

    private int productId;

    private int variantId;

    private int quantity;

    public void validate(Product product, Variant variant) throws CommerceException {
        if (quantity < 0) {
            throw new InvalidQuantityException();
        }
        if (variant.getProductId() != product.getId()) {
            throw new VariantMismatchException();
        }
        if (product.getStatus() == ProductStatus.INACTIVE) {
            throw new InactiveProductException();
        }
        if (product.isOutOfStock(quantity)) {
            throw new ProductOutOfStockException();
        }
        if (variant.getStatus() == VariantStatus.INACTIVE) {
            throw new InactiveVariantException();
        }
        if (variant.isOutOfStock(quantity)) {
            throw new VariantOutOfStockException();
        }

        product.useQuota(quantity);
        variant.useQuota(quantity);
    }

}
