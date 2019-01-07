package net.fklj.richanemic.adm.service;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.adm.data.Order;
import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.adm.data.Product;
import net.fklj.richanemic.adm.data.Variant;
import net.fklj.richanemic.adm.repository.OrderRepository;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.CreateOrderException;
import net.fklj.richanemic.data.CommerceException.DuplicateProductException;
import net.fklj.richanemic.data.CommerceException.InactiveProductException;
import net.fklj.richanemic.data.CommerceException.InactiveVariantException;
import net.fklj.richanemic.data.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import net.fklj.richanemic.data.CommerceException.ProductOutOfStockException;
import net.fklj.richanemic.data.CommerceException.VariantMismatchException;
import net.fklj.richanemic.data.CommerceException.VariantOutOfStockException;
import net.fklj.richanemic.data.OrderItemStatus;
import net.fklj.richanemic.data.OrderStatus;
import net.fklj.richanemic.data.ProductStatus;
import net.fklj.richanemic.data.VariantStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class OrderAggregateServiceImpl extends OrderServiceImpl implements OrderAggregateService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductAggregateService productService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createOrder(int userId, List<OrderItem> items)
            throws CommerceException {
        int orderId = new Random().nextInt();

        validateOrder(items);
        for (OrderItem item : items) {
            item.setOrderId(orderId);
            validateEachItem(item);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(int orderId) throws OrderNotFoundException {
        Order order = orderRepository.getOrder(orderId)
                .orElseThrow(OrderNotFoundException::new);
        // TODO: check paid
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }

        orderRepository.updateOrderStatus(orderId, OrderStatus.CANCELLED);

        for (OrderItem item : order.getItems()) {
            productService.releaseQuota(item.getProductId(), item.getVariantId(), item.getQuantity());
            orderRepository.updateOrderItemStatus(item.getId(), OrderItemStatus.CANCELLED);
        }
    }

    private void validateOrder(List<OrderItem> items) throws DuplicateProductException {
        // forbid same productId in items
        if (items.size() > items.stream().map(OrderItem::getProductId).distinct().count()) {
            throw new DuplicateProductException();
        }
    }

    private void validateEachItem(OrderItem item) throws CommerceException {
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

}
