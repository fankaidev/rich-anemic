package net.fklj.richanemic.rdm.entity.order;

import lombok.NoArgsConstructor;
import lombok.Setter;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.data.OrderItemStatus;
import net.fklj.richanemic.rdm.repository.OrderRepository;

@Setter
@NoArgsConstructor
public class OrderItemEntity extends OrderItem {

    private OrderRepository orderRepository;

    public void cancel() {
        this.status = OrderItemStatus.CANCELLED;
        save();
    }

    public void refund() {
        this.status = OrderItemStatus.REFUNDED;
        save();
    }

    public void pay() {
        this.status = OrderItemStatus.PAID;
        save();
    }

    private void save() {
        orderRepository.saveOrderItem(this);
    }

}
