package net.fklj.richanemic.adm.service.order;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.Order;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.data.Payment;
import net.fklj.richanemic.event.OrderCancelledEvent;
import net.fklj.richanemic.adm.repository.OrderRepository;
import net.fklj.richanemic.adm.repository.PaymentRepository;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.CreateOrderException;
import net.fklj.richanemic.data.CommerceException.DuplicateProductException;
import net.fklj.richanemic.data.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import net.fklj.richanemic.data.OrderItemStatus;
import net.fklj.richanemic.data.OrderStatus;
import net.fklj.richanemic.service.order.OrderTxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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

    private Order lock(int orderId) throws OrderNotFoundException {
        return orderRepository.lockOrder(orderId).orElseThrow(OrderNotFoundException::new);
    }

    @Override
    public int create(int userId, List<OrderItem> items) throws CommerceException {
        Order order = prepareOrder(userId, items);
        validateOrder(items);
        orderRepository.saveOrder(order);
        return order.getId();
    }

    private Order prepareOrder(int userId, List<OrderItem> items) {
        int orderId = new Random().nextInt();
        items.forEach(item -> item.setOrderId(orderId));
        return Order.builder()
                .userId(userId)
                .id(orderId)
                .items(items)
                .status(OrderStatus.PENDING)
                .build();
    }

    @Override
    public boolean cancel(int orderId) throws OrderNotFoundException {
        Order order = lock(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return false;
        }
        orderRepository.updateOrderStatus(order.getId(), OrderStatus.CANCELLED);

        for (OrderItem item : order.getItems()) {
            orderRepository.updateOrderItemStatus(item.getId(), OrderItemStatus.CANCELLED);
        }

        return true;
    }

    @Override
    public void cancelWithEvent(int orderId) throws OrderNotFoundException {
        cancel(orderId);
        Order cancelled = getOrder(orderId).get();
        eventPublisher.publishEvent(new OrderCancelledEvent(this, cancelled));
    }

    private void validateOrder(List<OrderItem> items) throws CreateOrderException {
        // forbid same productId in items
        if (items.size() > items.stream().map(OrderItem::getProductId).distinct().count()) {
            throw new DuplicateProductException();
        }

        for (OrderItem item : items) {
            if (item.getQuantity() < 0) {
                throw new InvalidQuantityException();
            }
        }
    }

    @Override
    public void refundItem(int orderId, int orderItemId) throws OrderNotFoundException {
        Order order = lock(orderId);
        OrderItem item = order.getItems().stream().filter(it -> it.getId() == orderItemId).findAny()
                .orElseThrow(OrderNotFoundException::new);
        orderRepository.updateOrderItemStatus(item.getId(), OrderItemStatus.REFUNDED);
    }

    @Override
    public Optional<Payment> getPaymentOfOrder(int orderId) {
        return paymentRepository.getPaymentOfOrder(orderId);
    }

    @Override
    public void pay(int orderId, int couponId, int cashFee) throws OrderNotFoundException {
        Order order = lock(orderId);
        Payment payment = Payment.builder()
                .id(new Random().nextInt())
                .orderId(order.getId())
                .userId(order.getUserId())
                .cashFee(cashFee)
                .couponId(couponId)
                .build();
        paymentRepository.savePayment(payment);

        orderRepository.updateOrderStatus(order.getId(), OrderStatus.PAID);
        for (OrderItem item : order.getItems()) {
            orderRepository.updateOrderItemStatus(item.getId(), OrderItemStatus.PAID);
        }
    }

}
