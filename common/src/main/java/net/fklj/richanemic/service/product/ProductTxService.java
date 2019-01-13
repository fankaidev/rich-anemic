package net.fklj.richanemic.service.product;

import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InvalidProductException;
import net.fklj.richanemic.event.OrderCancelledEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

public interface ProductTxService extends ProductService {

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
    void activateVariant(int productId, int variantId) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void inactivateVariant(int productId, int variantId) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void useQuota(int productId, int variantId, int quantity) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void releaseQuota(int productId, int variantId, int quantity) throws CommerceException;

    @EventListener
    @Transactional(rollbackFor = Exception.class)
    void onOrderCancelled(OrderCancelledEvent event) throws CommerceException;
}
