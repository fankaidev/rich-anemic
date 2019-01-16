package net.fklj.richanemic.rdm.entity.order;

import lombok.NoArgsConstructor;
import lombok.Setter;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.data.OrderItemStatus;
import net.fklj.richanemic.rdm.repository.OrderRepository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Setter
@NoArgsConstructor
@Entity
public class OrderItemEntity extends OrderItem {

    @Id
    @GeneratedValue
    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public int getOrderId() {
        return super.getOrderId();
    }

    @Override
    public int getProductId() {
        return super.getProductId();
    }

    @Override
    public int getVariantId() {
        return super.getVariantId();
    }

    @Override
    public int getQuantity() {
        return super.getQuantity();
    }

    @Override
    public OrderItemStatus getStatus() {
        return super.getStatus();
    }

    public void cancel() {
        this.status = OrderItemStatus.CANCELLED;
    }

    public void refund() {
        this.status = OrderItemStatus.REFUNDED;
    }

    public void pay() {
        this.status = OrderItemStatus.PAID;
    }

}
