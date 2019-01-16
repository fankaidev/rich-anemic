package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.CommerceException.CreateOrderException;
import net.fklj.richanemic.data.Order;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.rdm.entity.order.OrderEntity;
import net.fklj.richanemic.rdm.entity.order.OrderItemEntity;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private PaymentRepository paymentRepository;

    public OrderEntity createOrder(int userId, List<OrderItem> items) throws CreateOrderException {
        OrderEntity order = new OrderEntity(userId, items);
        for (OrderItem item : order.getItems()) {
            OrderItemEntity entity = new OrderItemEntity();
            BeanUtils.copyProperties(item, entity);
            orderItemRepository.save(entity);
        }

        orderEntityRepository.save(order);
        return order;
    }

    public Optional<Order> getOrder(int orderId) {
        Optional<OrderEntity> order = orderEntityRepository.findById(orderId);
        List<OrderItemEntity> items = orderItemRepository.findByOrderId(orderId);
        order.ifPresent(o -> o.setItems(items));
        order.ifPresent(o -> o.setPaymentRepository(paymentRepository));
        return order.map(o -> o);
    }

    public Optional<OrderEntity> lockOrder(int orderId) {
        Optional<OrderEntity> order = orderEntityRepository.lock(orderId);
        List<OrderItemEntity> items = orderItemRepository.findByOrderId(orderId);
        order.ifPresent(o -> o.setItems(items));
        order.ifPresent(o -> o.setPaymentRepository(paymentRepository));
        return order;
    }

    public Optional<OrderItem> getOrderItem(int orderItemId) {
       return orderItemRepository.findById(orderItemId).map(o->o);
    }

    public Map<Integer, OrderItem> getOrderItemsByOrderItemIds(Collection<Integer> orderItemIds) {
        List<OrderItemEntity> allById = new ArrayList<>();
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
