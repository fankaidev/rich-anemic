package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.rdm.entity.order.OrderItemEntity;

import java.util.List;

public interface OrderItemRepository extends BaseRepository<OrderItemEntity, Integer> {

    List<OrderItemEntity> findByOrderId(int orderId);

    List<OrderItem> findByProductId(int productId);

    List<OrderItem> findByVariantId(int variantId);

}
