package net.fklj.richanemic.rdm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.CreateOrderException;
import net.fklj.richanemic.data.CommerceException.DuplicateProductException;
import net.fklj.richanemic.data.CommerceException.InactiveProductException;
import net.fklj.richanemic.data.CommerceException.InactiveVariantException;
import net.fklj.richanemic.data.CommerceException.InvalidProductException;
import net.fklj.richanemic.data.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.data.CommerceException.InvalidVariantException;
import net.fklj.richanemic.data.CommerceException.ProductOutOfStockException;
import net.fklj.richanemic.data.CommerceException.VariantMismatchException;
import net.fklj.richanemic.data.CommerceException.VariantOutOfStockException;
import net.fklj.richanemic.data.OrderStatus;
import net.fklj.richanemic.data.ProductStatus;
import net.fklj.richanemic.data.VariantStatus;
import net.fklj.richanemic.rdm.repository.OrderRepository;
import net.fklj.richanemic.rdm.repository.ProductRepository;

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
