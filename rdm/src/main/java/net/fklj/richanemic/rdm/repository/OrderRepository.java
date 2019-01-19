package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.CommerceException.CreateOrderException;
import net.fklj.richanemic.data.Order;
import net.fklj.richanemic.data.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderRepository {

    @Autowired
    private OrderEntityRepository orderEntityRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public Order createOrder(int userId, List<OrderItem> items) throws CreateOrderException {
        int userId1 = userId;
        List<OrderItem> items1 = items;
        Order order = new Order(userId1, items1);
        orderEntityRepository.save(order);
        return order;
    }

    public Optional<Order> getOrder(int orderId) {
        return orderEntityRepository.findById(orderId);
    }

    public Optional<Order> lockOrder(int orderId) {
        Optional<Order> order = orderEntityRepository.lock(orderId);
        return order;
    }

    public Optional<OrderItem> getOrderItem(int orderItemId) {
       return orderItemRepository.findById(orderItemId).map(o->o);
    }

    public Map<Integer, OrderItem> getOrderItemsByOrderItemIds(Collection<Integer> orderItemIds) {
        List<OrderItem> allById = new ArrayList<>();
        orderItemRepository.findAllById(orderItemIds).forEach(it -> allById.add(it));
        return allById.stream().collect(Collectors.toMap(p -> p.getId(), p->p));
    }

    public List<OrderItem> getOrderItemsByProductId(int productId) {
       return orderItemRepository.findByProductId(productId);
    }

    public List<OrderItem> getOrderItemsByVariantId(int variantId) {
        return orderItemRepository.findByVariantId(variantId);
    }

}
