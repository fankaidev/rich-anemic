package net.fklj.richanemic.adm.service;

import net.fklj.richanemic.data.CommerceException;
import org.springframework.transaction.annotation.Transactional;

public interface ProductAggregateService extends ProductService {

    @Transactional(rollbackFor = Exception.class)
    int createProduct(int price, int quota) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    int createVariant(int productId, int quota) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    int createProductWithDefaultVariant(int price, int quantity) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void activateProduct(int productId);

    @Transactional(rollbackFor = Exception.class)
    void inactivateProduct(int productId);

    @Transactional(rollbackFor = Exception.class)
    void activateVariant(int variantId);

    @Transactional(rollbackFor = Exception.class)
    void inactivateVariant(int variantId);

    @Transactional(rollbackFor = Exception.class)
    void useQuota(int productId, int variantId, int quantity) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void releaseQuota(int productId, int variantId, int quantity);

}
