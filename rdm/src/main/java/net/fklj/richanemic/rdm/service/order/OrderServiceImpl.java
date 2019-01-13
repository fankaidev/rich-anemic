package net.fklj.richanemic.rdm.service.order;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import net.fklj.richanemic.data.Order;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.data.Payment;
import net.fklj.richanemic.event.OrderCancelledEvent;
import net.fklj.richanemic.rdm.entity.OrderEntity;
import net.fklj.richanemic.rdm.repository.OrderRepository;
import net.fklj.richanemic.rdm.repository.PaymentRepository;
import net.fklj.richanemic.service.order.OrderTxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderTxService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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

    /***************** transaction *******************/

    private OrderEntity lock(int orderId) throws OrderNotFoundException {
        return orderRepository.lockOrder(orderId).orElseThrow(OrderNotFoundException::new);
    }

    @Override
    public int create(int userId, List<OrderItem> items) throws CommerceException {
       return orderRepository.createOrder(userId, items).getId();
    }

    @Override
    public boolean cancel(int orderId) throws OrderNotFoundException {
        OrderEntity order = lock(orderId);
        return order.cancel();
    }

    @Override
    public void cancelWithEvent(int orderId) throws OrderNotFoundException {
        cancel(orderId);
        Order cancelled = getOrder(orderId).get();
        eventPublisher.publishEvent(new OrderCancelledEvent(this, cancelled));
    }

    @Override
    public void refundItem(int orderId, int orderItemId) throws OrderNotFoundException {
        lock(orderId).refundItem(orderItemId);
    }

    @Override
    public Optional<Payment> getPaymentOfOrder(int orderId) {
        return paymentRepository.getPaymentOfOrder(orderId);
    }

    @Override
    public void pay(int orderId, int couponId, int cashFee) throws OrderNotFoundException {
        OrderEntity order = lock(orderId);
        order.pay(couponId, cashFee);
    }

}
