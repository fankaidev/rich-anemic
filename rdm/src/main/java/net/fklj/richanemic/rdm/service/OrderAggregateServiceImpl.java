package net.fklj.richanemic.rdm.service;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.CreateOrderException;
import net.fklj.richanemic.data.CommerceException.InvalidProductException;
import net.fklj.richanemic.data.CommerceException.InvalidVariantException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import net.fklj.richanemic.data.OrderStatus;
import net.fklj.richanemic.rdm.entity.Order;
import net.fklj.richanemic.rdm.entity.OrderItem;
import net.fklj.richanemic.rdm.entity.Product;
import net.fklj.richanemic.rdm.entity.Variant;
import net.fklj.richanemic.rdm.repository.OrderRepository;
import net.fklj.richanemic.rdm.repository.ProductRepository;
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
    private ProductRepository productRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createOrder(int userId, List<OrderItem> items)
            throws CommerceException {
        int orderId = new Random().nextInt();

        Order order = Order.builder()
                .userId(userId)
                .id(orderId)
                .items(items)
                .status(OrderStatus.PENDING)
                .orderRepository(orderRepository)
                .build();

        order.validate();
        for (OrderItem item : items) {
            item.setOrderId(orderId);
            Variant variant = productRepository.getVariant(item.getVariantId())
                    .orElseThrow(CreateOrderException::new);
            Product product = productRepository.getProduct(item.getProductId())
                    .orElseThrow(CreateOrderException::new);
            item.validate(product, variant);
        }

        return order.place();
    }
    

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(int orderId) throws CommerceException {
        Order order = getOrderOrThrow(orderId);

        // TODO: check paid
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }

        for (OrderItem item : order.getItems()) {
            Variant variant = productRepository.getVariant(item.getVariantId())
                    .orElseThrow(InvalidVariantException::new);
            Product product = productRepository.getProduct(item.getProductId())
                    .orElseThrow(InvalidProductException::new);
            variant.releaseQuota(item.getQuantity());
            product.releaseQuota(item.getQuantity());
        }

        order.cancel();

    }

    private Order getOrderOrThrow(int orderId) throws OrderNotFoundException {
        return orderRepository.getOrder(orderId).orElseThrow(OrderNotFoundException::new);
    }

}
