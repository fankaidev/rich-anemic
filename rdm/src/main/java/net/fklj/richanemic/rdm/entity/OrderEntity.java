package net.fklj.richanemic.rdm.entity;

import lombok.NoArgsConstructor;
import lombok.Setter;
import net.fklj.richanemic.data.CommerceException.CreateOrderException;
import net.fklj.richanemic.data.CommerceException.DuplicateProductException;
import net.fklj.richanemic.data.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import net.fklj.richanemic.data.Order;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.data.OrderStatus;
import net.fklj.richanemic.data.Payment;
import net.fklj.richanemic.rdm.repository.OrderRepository;
import net.fklj.richanemic.rdm.repository.PaymentRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Setter
@NoArgsConstructor
public class OrderEntity extends Order {

    private OrderRepository orderRepository;

    private PaymentRepository paymentRepository;

    public OrderEntity(int userId, List<OrderItem> items) throws CreateOrderException {
        super(new Random().nextInt(), userId, items, OrderStatus.PENDING);
        items.forEach(item -> item.setOrderId(id));
        validateOrder(items);
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

    public List<OrderItemEntity> getItemEntities() {
        return super.getItems().stream().map(item -> (OrderItemEntity)item).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean cancel() {
        if (getStatus() == OrderStatus.CANCELLED) {
            return false;
        }
        this.status = OrderStatus.CANCELLED;
        save();

        for (OrderItemEntity item : getItemEntities()) {
            item.cancel();
        }

        return true;
    }

    private void save() {
        orderRepository.updateOrderStatus(id, status);
    }

    @Transactional(rollbackFor = Exception.class)
    public void refundItem(int orderItemId) throws OrderNotFoundException {
        OrderItemEntity item = getItem(orderItemId).orElseThrow(OrderNotFoundException::new);
        item.refund();
    }

    private Optional<OrderItemEntity> getItem(int orderItemId) {
        return getItemEntities().stream().filter(item -> item.getId() == orderItemId).findAny();
    }

    @Transactional(rollbackFor = Exception.class)
    public void pay(int couponId, int cashFee) {
        Payment payment = Payment.builder()
                .id(new Random().nextInt())
                .orderId(getId())
                .userId(getUserId())
                .cashFee(cashFee)
                .couponId(couponId)
                .build();
        paymentRepository.savePayment(payment);

        for (OrderItemEntity item : getItemEntities()) {
            item.pay();
        }

        this.status = OrderStatus.PAID;
        save();
    }

}
