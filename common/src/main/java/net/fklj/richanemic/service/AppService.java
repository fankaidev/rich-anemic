package net.fklj.richanemic.service;

import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.data.CommerceException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * domain service
 */
public interface AppService {

    @Transactional(rollbackFor = Exception.class)
    int createOrder(int userId, List<OrderItem> items) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void payOrder(int orderId, int couponId) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void cancelOrder(int orderId) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void refundOrderItem(int orderId, int orderItemId) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void callbackVariant(int productId, int variantId) throws CommerceException;

}
