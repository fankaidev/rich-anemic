package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.OrderItem;

import java.util.List;

public interface OrderItemRepository extends BaseRepository<OrderItem, Integer> {

    List<OrderItem> findByOrderId(int orderId);

    List<OrderItem> findByProductId(int productId);

    List<OrderItem> findByVariantId(int variantId);

}
