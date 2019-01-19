package net.fklj.richanemic.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fklj.richanemic.data.OrderItemStatus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue
    protected int id;

    protected int orderId;

    protected int productId;

    protected int variantId;

    protected int quantity;

    protected OrderItemStatus status;

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
