package net.fklj.richanemic.adm.service.order;

import net.fklj.richanemic.adm.data.Order;
import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.adm.data.Payment;
import net.fklj.richanemic.adm.data.Product;
import net.fklj.richanemic.adm.data.Variant;
import net.fklj.richanemic.adm.repository.OrderRepository;
import net.fklj.richanemic.adm.repository.PaymentRepository;
import net.fklj.richanemic.adm.service.product.ProductService;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.CreateOrderException;
import net.fklj.richanemic.data.CommerceException.DuplicateProductException;
import net.fklj.richanemic.data.CommerceException.InactiveProductException;
import net.fklj.richanemic.data.CommerceException.InactiveVariantException;
import net.fklj.richanemic.data.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.data.CommerceException.ProductOutOfStockException;
import net.fklj.richanemic.data.CommerceException.VariantMismatchException;
import net.fklj.richanemic.data.CommerceException.VariantOutOfStockException;
import net.fklj.richanemic.data.OrderItemStatus;
import net.fklj.richanemic.data.OrderStatus;
import net.fklj.richanemic.data.ProductStatus;
import net.fklj.richanemic.data.VariantStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class OrderServiceImpl implements OrderTxService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductService productService;

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

    @Override
    public int create(int userId, List<OrderItem> items) throws CommerceException {
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
    public void cancel(Order order) {
        orderRepository.updateOrderStatus(order.getId(), OrderStatus.CANCELLED);

        for (OrderItem item : order.getItems()) {
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

    }

    @Override
    public void refundItem(OrderItem item) {
        orderRepository.updateOrderItemStatus(item.getId(), OrderItemStatus.REFUNDED);
    }

    @Override
    public Optional<Payment> getPaymentOfOrder(int orderId) {
        return paymentRepository.getPaymentOfOrder(orderId);
    }

    @Override
    public void pay(Order order, int couponId, int cashFee) {
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
