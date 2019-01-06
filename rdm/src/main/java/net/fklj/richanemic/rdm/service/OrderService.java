package net.fklj.richanemic.rdm.service;

import net.fklj.richanemic.rdm.entity.Order;
import net.fklj.richanemic.rdm.entity.OrderItem;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {

    Optional<Order> getOrder(int orderId);

    Optional<OrderItem> getOrderItem(int orderItemId);

    Map<Integer, OrderItem> getOrderItemsByOrderItemIds(Collection<Integer> orderItemIds);

    List<OrderItem> getOrderItemsByProductId(int productId);

    List<OrderItem> getOrderItemsByVariantId(int variantId);
}
