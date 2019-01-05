package net.fklj.richanemic.adm.service;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.adm.data.Order;
import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.adm.data.OrderStatus;
import net.fklj.richanemic.adm.data.Product;
import net.fklj.richanemic.adm.data.ProductStatus;
import net.fklj.richanemic.adm.data.Variant;
import net.fklj.richanemic.adm.data.VariantStatus;
import net.fklj.richanemic.adm.exception.CommerceException;
import net.fklj.richanemic.adm.exception.CommerceException.CreateOrderException;
import net.fklj.richanemic.adm.exception.CommerceException.DuplicateProductException;
import net.fklj.richanemic.adm.exception.CommerceException.InactiveProductException;
import net.fklj.richanemic.adm.exception.CommerceException.InactiveVariantException;
import net.fklj.richanemic.adm.exception.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.adm.exception.CommerceException.OrderNotFoundException;
import net.fklj.richanemic.adm.exception.CommerceException.ProductOutOfStockException;
import net.fklj.richanemic.adm.exception.CommerceException.VariantMismatchException;
import net.fklj.richanemic.adm.exception.CommerceException.VariantOutOfStockException;
import net.fklj.richanemic.adm.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    public int createOrder(int userId, List<OrderItem> items)
            throws CommerceException {
        int orderId = new Random().nextInt();

        validateItems(items);

        for (OrderItem item : items) {
            item.setOrderId(orderId);
            validate(item);
        }

        Order order = Order.builder()
                .userId(userId)
                .id(orderId)
                .items(items)
                .status(OrderStatus.PENDING)
                .build();
        orderRepository.saveOrder(order);
        return orderId;
    }

    public void cancelOrder(int orderId) throws OrderNotFoundException {
        Order order = orderRepository.getOrder(orderId)
                .orElseThrow(OrderNotFoundException::new);
        // TODO: check paid
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }
        for (OrderItem item : order.getItems()) {
            productService.releaseQuota(item.getProductId(), item.getVariantId(), item.getQuantity());
        }
        orderRepository.updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    private void validateItems(List<OrderItem> items) throws DuplicateProductException {
        // forbid same productId in items
        if (items.size() > items.stream().map(OrderItem::getProductId).distinct().count()) {
            throw new DuplicateProductException();
        }
    }

    private void validate(OrderItem item) throws CommerceException {
        if (item.getQuantity() < 0) {
            throw new InvalidQuantityException();
        }
        Variant variant = productService.getVariant(item.getVariantId())
                .orElseThrow(CreateOrderException::new);
        Product product = productService.getProduct(item.getProductId())
                .orElseThrow(CreateOrderException::new);
        if (variant.getProductId() != product.getId()) {
            throw new VariantMismatchException();
        }
        if (product.getStatus() == ProductStatus.INACTIVE) {
            throw new InactiveProductException();
        }
        if (product.isOutOfStock(item.getQuantity())) {
            throw new ProductOutOfStockException();
        }
        if (variant.getStatus() == VariantStatus.INACTIVE) {
            throw new InactiveVariantException();
        }
        if (variant.isOutOfStock(item.getQuantity())) {
            throw new VariantOutOfStockException();
        }

        productService.useQuota(product.getId(), variant.getId(), item.getQuantity());
    }

    public Optional<Order> getOrder(int orderId) {
        return orderRepository.getOrder(orderId);
    }

}
