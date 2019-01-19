package net.fklj.richanemic.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fklj.richanemic.data.CommerceException.CreateOrderException;
import net.fklj.richanemic.data.CommerceException.DuplicateProductException;
import net.fklj.richanemic.data.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Entity
@Table(name = "`order`", indexes = {@Index(name = "order_userId",  columnList="userId", unique = false)})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    protected int id;

    protected int userId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected List<OrderItem> items;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected Set<Payment> payments;

    protected OrderStatus status;

    public Order(int userId, List<OrderItem> items) throws CreateOrderException {
        this(new Random().nextInt(), userId, items, Collections.emptySet(), OrderStatus.PENDING);
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


    @Transactional(rollbackFor = Exception.class)
    public boolean cancel() {
        if (getStatus() == OrderStatus.CANCELLED) {
            return false;
        }
        this.status = OrderStatus.CANCELLED;

        for (OrderItem item : items) {
            item.cancel();
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void refundItem(int orderItemId) throws OrderNotFoundException {
        OrderItem item = getItem(orderItemId).orElseThrow(OrderNotFoundException::new);
        item.refund();
    }

    private Optional<OrderItem> getItem(int orderItemId) {
        return items.stream().filter(item -> item.getId() == orderItemId).findAny();
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
        payments.add(payment);

        for (OrderItem item : items) {
            item.pay();
        }

        this.status = OrderStatus.PAID;
    }


}
