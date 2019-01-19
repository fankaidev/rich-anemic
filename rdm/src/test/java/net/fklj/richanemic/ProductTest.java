package net.fklj.richanemic;

import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InvalidProductException;
import net.fklj.richanemic.data.CommerceException.InvalidVariantException;
import net.fklj.richanemic.data.CommerceException.ProductOutOfStockException;
import net.fklj.richanemic.data.CommerceException.VariantMismatchException;
import net.fklj.richanemic.data.CommerceException.VariantQuotaException;
import net.fklj.richanemic.data.Product;
import net.fklj.richanemic.data.ProductStatus;
import net.fklj.richanemic.data.Variant;
import net.fklj.richanemic.data.VariantStatus;
import net.fklj.richanemic.service.product.ProductTxService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.fklj.richanemic.data.Constants.PRODUCT_MAX_PRICE;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ProductTest extends BaseTest {

    @Autowired
    private ProductTxService productService;

    @Test
    public void testCreateProduct() throws CommerceException {
        int productId = productService.createProduct(22, 10);

        Optional<Product> product = productService.getProduct(productId);
        assertTrue(product.isPresent());
        assertThat(product.get().getId(), is(productId));
        assertThat(productService.getVariantsOfProduct(productId), emptyIterable());
    }

    @Test
    public void testGetInvalid() throws CommerceException {
        Optional<Product> product = productService.getProduct(-1);
        assertFalse(product.isPresent());

        Optional<Variant> variant = productService.getVariant(-1);
        assertFalse(variant.isPresent());

        List<Variant> variants = productService.getVariantsOfProduct(-1);
        assertThat(variants, emptyIterable());

        Map<Integer, Product> productMap = productService.getProducts(Collections.singletonList(-1));
        assertThat(productMap.size(), is (0));
    }

    @Test
    public void testCreateProductWithDefaultVariant() throws CommerceException {
        int productId = productService.createProductWithDefaultVariant(33, 100);

        Optional<Product> product = productService.getProduct(productId);
        assertTrue(product.isPresent());
        assertThat(product.get().getId(), is(productId));
        List<Variant> variants = productService.getVariantsOfProduct(productId);
        assertThat(variants, hasSize(1));
        assertThat(variants.get(0).getProductId(), is(productId));
    }

    @Test
    public void testCreateVariant() throws CommerceException {
        int variantId = productService.createVariant(PRODUCT1_INACTIVE_ID, 1);

        Optional<Variant> variant = productService.getVariant(variantId);
        assertTrue(variant.isPresent());
        assertThat(variant.get().getId(), is(variantId));
        assertThat(productService.getVariantsOfProduct(PRODUCT1_INACTIVE_ID), hasSize(2));
    }

    @Test(expected = InvalidProductException.class)
    public void testCreateVariantForInvalidProduct() throws CommerceException {
        productService.createVariant(-1, 1);
    }

    @Test(expected = InvalidVariantException.class)
    public void testProductVariantMismatch() throws CommerceException {
        productService.activateVariant(PRODUCT1_INACTIVE_ID, P2_VAR1_INACTIVE_ID);
    }

    @Test
    public void testStatus() throws CommerceException {
        productService.activateProduct(PRODUCT1_INACTIVE_ID);
        assertThat(productService.getProduct(PRODUCT1_INACTIVE_ID).get().getStatus(),
                is(ProductStatus.ACTIVE));
        productService.inactivateProduct(PRODUCT1_INACTIVE_ID);
        assertThat(productService.getProduct(PRODUCT1_INACTIVE_ID).get().getStatus(),
                is(ProductStatus.INACTIVE));

        productService.activateVariant(PRODUCT1_INACTIVE_ID, P1_VAR1_INACTIVE_ID);
        assertThat(productService.getVariant(P1_VAR1_INACTIVE_ID).get().getStatus(),
                is(VariantStatus.ACTIVE));
        productService.inactivateVariant(PRODUCT1_INACTIVE_ID, P1_VAR1_INACTIVE_ID);
        assertThat(productService.getVariant(P1_VAR1_INACTIVE_ID).get().getStatus(),
                is(VariantStatus.INACTIVE));
    }

    @Test
    public void testGetVariants() {
        Optional<Variant> p1v1 = productService.getVariant(P1_VAR1_INACTIVE_ID);
        assertTrue(p1v1.isPresent());
        assertThat(p1v1.get().getId(), is(P1_VAR1_INACTIVE_ID));
        assertThat(p1v1.get().getProductId(), is(PRODUCT1_INACTIVE_ID));

        assertThat(productService.getVariantsOfProduct(PRODUCT1_INACTIVE_ID), hasSize(1));
        assertThat(productService.getVariantsOfProduct(PRODUCT2_Q0_ID), hasSize(3));
    }

    @Test(expected = InvalidProductException.class)
    public void testCreateProductPriceTooLow() throws CommerceException {
        productService.createProduct(-1, 1);
    }

    @Test(expected = InvalidProductException.class)
    public void testCreateProductPriceTooHigh() throws CommerceException {
        productService.createProduct(PRODUCT_MAX_PRICE + 1, 1);
    }

    @Test(expected = InvalidProductException.class)
    public void testCreateProductInvalidQuota() throws CommerceException {
        productService.createProduct(1, -1);
    }

    @Test(expected = InvalidVariantException.class)
    public void testCreateVariantInvalidQuota() throws CommerceException {
        productService.createVariant(PRODUCT1_INACTIVE_ID, -1);
    }

    @Test(expected = VariantQuotaException.class)
    public void testCreateVariantExceedsQuota() throws CommerceException {
        productService.createVariant(PRODUCT3_Q9_ID, 10);
    }

    @Test(expected = VariantQuotaException.class)
    public void testCreateVariantExceedsQuota2() throws CommerceException {
        productService.createVariant(PRODUCT3_Q9_ID, 0);
    }

    @Test(expected = ProductOutOfStockException.class)
    public void testInvalidUseQuota() throws CommerceException {
        productService.useQuota(PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 2);
    }

}
