package net.fklj.richanemic.rdm.service;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InvalidProductException;
import net.fklj.richanemic.data.ProductStatus;
import net.fklj.richanemic.rdm.entity.Product;
import net.fklj.richanemic.rdm.entity.Variant;
import net.fklj.richanemic.rdm.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static net.fklj.richanemic.data.Constants.PRODUCT_MAX_PRICE;

@Slf4j
@Service
public class ProductAggregateServiceImpl extends ProductServiceImpl implements ProductAggregateService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
    public int createVariant(int productId, int quota) throws CommerceException {
        return getProductOrThrow(productId).createVariant(quota);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createProductWithDefaultVariant(int price, int quantity)
            throws CommerceException {
        int productId = createProduct(price, quantity);
        createVariant(productId, quantity);
        return productId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateProduct(int productId) throws InvalidProductException {
        getProductOrThrow(productId).activate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inactivateProduct(int productId) throws InvalidProductException {
        getProductOrThrow(productId).inactivate();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateVariant(int variantId) throws InvalidProductException {
        getVariantOrThrow(variantId).activate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inactivateVariant(int variantId) throws InvalidProductException {
        getVariantOrThrow(variantId).inactivate();
    }

    private Product getProductOrThrow(int productId) throws InvalidProductException {
        return productRepository.getProduct(productId).orElseThrow(InvalidProductException::new);
    }

    private Variant getVariantOrThrow(int variantId) throws InvalidProductException {
        return productRepository.getVariant(variantId).orElseThrow(InvalidProductException::new);
    }

}
