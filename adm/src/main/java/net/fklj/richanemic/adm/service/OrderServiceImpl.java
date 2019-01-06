package net.fklj.richanemic.adm.service;

import net.fklj.richanemic.adm.data.Order;
import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.adm.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Optional<Order> getOrder(int orderId) {
        return orderRepository.getOrder(orderId);
    }

    @Override
    public Optional<OrderItem> getOrderItem(int orderItemId) {
        return orderRepository.getOrderItem(orderItemId);
    }

    @Override
    public Map<Integer, OrderItem> getOrderItemsByOrderItemIds(Collection<Integer> orderItemIds) {
        return orderRepository.getOrderItemsByOrderItemIds(orderItemIds);
    }

    @Override
    public List<OrderItem> getOrderItemsByProductId(int productId) {
        return orderRepository.getOrderItemsByProductId(productId);
    }

    @Override
    public List<OrderItem> getOrderItemsByVariantId(int variantId) {
        return orderRepository.getOrderItemsByVariantId(variantId);
    }

}
