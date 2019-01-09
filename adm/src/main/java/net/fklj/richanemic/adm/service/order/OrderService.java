package net.fklj.richanemic.adm.service.order;

import net.fklj.richanemic.adm.data.Order;
import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.adm.data.Payment;

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

    Optional<Payment> getPaymentOfOrder(int orderId);

}
