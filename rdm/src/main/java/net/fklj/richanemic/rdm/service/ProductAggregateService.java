package net.fklj.richanemic.rdm.service;

import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InvalidProductException;
import org.springframework.transaction.annotation.Transactional;

public interface ProductAggregateService extends ProductService {

    @Transactional(rollbackFor = Exception.class)
    int createProduct(int price, int quota) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    int createVariant(int productId, int quota) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    int createProductWithDefaultVariant(int price, int quantity) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void activateProduct(int productId) throws InvalidProductException;

    @Transactional(rollbackFor = Exception.class)
    void inactivateProduct(int productId) throws InvalidProductException;

    @Transactional(rollbackFor = Exception.class)
    void activateVariant(int variantId) throws InvalidProductException;

    @Transactional(rollbackFor = Exception.class)
    void inactivateVariant(int variantId) throws InvalidProductException;

}
