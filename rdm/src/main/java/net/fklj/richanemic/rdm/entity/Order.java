package net.fklj.richanemic.rdm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.DuplicateProductException;
import net.fklj.richanemic.data.OrderStatus;
import net.fklj.richanemic.rdm.repository.OrderRepository;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private int id;

    private int userId;

    private List<OrderItem> items;

    private OrderStatus status;

    private OrderRepository orderRepository;

    public void cancel() throws CommerceException {
        orderRepository.updateOrderStatus(id, OrderStatus.CANCELLED);
    }

    public int place() throws CommerceException {
        orderRepository.saveOrder(this);
        return id;
    }

    public void validate() throws DuplicateProductException {
        // forbid same productId in items
        if (items.size() > items.stream().map(OrderItem::getProductId).distinct().count()) {
            throw new DuplicateProductException();
        }
    }

}
