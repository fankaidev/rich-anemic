package net.fklj.richanemic.adm.service.product;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.adm.data.Product;
import net.fklj.richanemic.adm.data.Variant;
import net.fklj.richanemic.adm.repository.ProductRepository;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InvalidProductException;
import net.fklj.richanemic.data.CommerceException.InvalidVariantException;
import net.fklj.richanemic.data.CommerceException.ProductOutOfStockException;
import net.fklj.richanemic.data.CommerceException.VariantQuotaException;
import net.fklj.richanemic.data.ProductStatus;
import net.fklj.richanemic.data.VariantStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static net.fklj.richanemic.data.Constants.PRODUCT_MAX_PRICE;
import static net.fklj.richanemic.data.Constants.PRODUCT_QUOTA_INFINITY;

@Slf4j
@Service
public class ProductServiceImpl implements ProductTxService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Optional<Product> getProduct(int productId) {
        return productRepository.getProduct(productId);
    }

    @Override
    public Optional<Variant> getVariant(int variantId) {
        return productRepository.getVariant(variantId);
    }

    @Override
    public List<Variant> getVariantsOfProduct(int productId) {
        return productRepository.getVariantByProductId(productId);
    }

    @Override
    public Map<Integer, Product> getProducts(Collection<Integer> productIds) {
        return productRepository.getProducts(productIds);
    }

    /*************************** transaction ********************/

    private Product lock(int productId) throws InvalidProductException {
        return productRepository.lockProduct(productId).orElseThrow(InvalidProductException::new);
    }

    private Variant validateVariantOfProduct(int productId, int variantId)
            throws InvalidVariantException {
        Variant variant = productRepository.getVariant(variantId).orElseThrow(InvalidVariantException::new);
        if (variant.getProductId() != productId) {
            throw new InvalidVariantException();
        }
        return variant;
    }

    @Override
    public int createProduct(int price, int quota) throws CommerceException {
        if (price <= 0 || price > PRODUCT_MAX_PRICE) {
            log.error("create product with invalid price {}", price);
            throw new InvalidProductException();
        }
        if (quota < 0) {
            log.error("create product with invalid quota {}", quota);
            throw new InvalidProductException();
        }
        int productId = new Random().nextInt();
        Product product = Product.builder()
                .id(productId)
                .quota(quota)
                .price(price)
                .status(ProductStatus.INACTIVE)
                .build();
        productRepository.saveProduct(product);
        return productId;
    }

    @Override
    public int createVariant(int productId, int quota) throws CommerceException {
        Product product = lock(productId);
        if (quota < 0) {
            log.error("create variant with invalid quota {}", quota);
            throw new InvalidVariantException();
        }
        int variantId = new Random().nextInt();
        Variant variant = Variant.builder()
                .id(variantId)
                .productId(productId)
                .quota(quota)
                .status(VariantStatus.INACTIVE)
                .build();

        checkQuota(product, quota);

        productRepository.saveVariant(variant);
        return variantId;
    }

    private void checkQuota(@NonNull Product product, int requiredQuota) throws CommerceException {
        if (product.getQuota() == PRODUCT_QUOTA_INFINITY) {
            return;
        }
        if (requiredQuota == PRODUCT_QUOTA_INFINITY) {
            throw new VariantQuotaException();
        }
        List<Variant> variants = productRepository.getVariantByProductId(product.getId());
        int totalVariantQuota = variants.stream().map(Variant::getQuota).reduce(0, (a, b) -> a+b);
        if (totalVariantQuota + requiredQuota > product.getQuota()) {
            throw new VariantQuotaException();
        }
    }

    @Override
    public int createProductWithDefaultVariant(int price, int quantity)
            throws CommerceException {
        int productId = createProduct(price, quantity);
        createVariant(productId, quantity);
        return productId;
    }

    @Override
    public void activateProduct(int productId) throws InvalidProductException {
        lock(productId);
        productRepository.updateProductStatus(productId, ProductStatus.ACTIVE);
    }

    @Override
    public void inactivateProduct(int productId) throws InvalidProductException {
        lock(productId);
        productRepository.updateProductStatus(productId, ProductStatus.INACTIVE);
    }


    @Override
    public void activateVariant(int productId, int variantId) throws CommerceException {
        lock(productId);
        validateVariantOfProduct(productId, variantId);
        productRepository.updateVariantStatus(variantId, VariantStatus.ACTIVE);
    }

    @Override
    public void inactivateVariant(int productId, int variantId) throws CommerceException {
        lock(productId);
        validateVariantOfProduct(productId, variantId);
        productRepository.updateVariantStatus(variantId, VariantStatus.INACTIVE);
    }

    @Override
    public void useQuota(int productId, int variantId, int quantity) throws CommerceException {
        lock(productId);
        validateVariantOfProduct(productId, variantId);
        if (!productRepository.increaseVariantSoldCount(variantId, quantity)) {
            throw new ProductOutOfStockException();
        }
        if (!productRepository.increaseProductSoldCount(productId, quantity)) {
            throw new ProductOutOfStockException();
        }
    }

    @Override
    public void releaseQuota(int productId, int variantId, int quantity) throws CommerceException {
        lock(productId);
        validateVariantOfProduct(productId, variantId);
        productRepository.increaseVariantSoldCount(variantId, -quantity);
        productRepository.increaseProductSoldCount(productId, -quantity);
    }

}