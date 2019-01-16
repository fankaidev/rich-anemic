package net.fklj.richanemic.rdm.service;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.CouponNotFoundException;
import net.fklj.richanemic.data.CommerceException.InvalidProductException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import net.fklj.richanemic.data.Order;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.data.OrderStatus;
import net.fklj.richanemic.data.Payment;
import net.fklj.richanemic.data.Product;
import net.fklj.richanemic.rdm.entity.balance.BalanceEntity;
import net.fklj.richanemic.rdm.entity.coupon.CouponEntity;
import net.fklj.richanemic.rdm.entity.order.OrderEntity;
import net.fklj.richanemic.rdm.entity.product.ProductEntity;
import net.fklj.richanemic.rdm.repository.BalanceRepository;
import net.fklj.richanemic.rdm.repository.CouponRepository;
import net.fklj.richanemic.rdm.repository.OrderRepository;
import net.fklj.richanemic.rdm.repository.PaymentRepository;
import net.fklj.richanemic.rdm.repository.ProductVariantRepository;
import net.fklj.richanemic.rdm.repository.VariantRepository;
import net.fklj.richanemic.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static net.fklj.richanemic.data.Constants.VOID_COUPON_ID;

@Slf4j
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductVariantRepository productRepository;

    @Autowired
    private VariantRepository variantRepository;

    @Override
    public void callbackVariant(int productId, int variantId) throws CommerceException {
        ProductEntity product = productRepository.lock(productId).orElseThrow(
                InvalidProductException::new);
        List<OrderItem> items = orderRepository.getOrderItemsByVariantId(variantId);
        for (OrderItem item : items) {
            OrderEntity order = orderRepository.lockOrder(item.getOrderId())
                    .orElseThrow(OrderNotFoundException::new);
            if (order.getStatus() == OrderStatus.PAID) {
                refundOrderItem(order.getId(), item.getId());
            } else {
                cancelOrder(order.getId());
            }
        }

        product.inactivateVariant(variantId);
    }


    @Override
    public void payOrder(int orderId, int couponId) throws CommerceException {
        OrderEntity order = orderRepository.lockOrder(orderId)
                .orElseThrow(OrderNotFoundException::new);
        BalanceEntity balance = balanceRepository.lock(order.getUserId()).get();
        CouponEntity coupon = couponId == VOID_COUPON_ID ? CouponEntity.VOID_COUPON :
                couponRepository.lock(couponId).orElseThrow(CouponNotFoundException::new);

        final int fee = getOrderFee(order);
        final int couponFee = coupon.use();
        final int cashFee = fee - couponFee;
        balance.use(cashFee);
        order.pay(couponId, cashFee);
    }

    @Override
    public void refundOrderItem(int orderId, int orderItemId) throws CommerceException {
        OrderEntity order = orderRepository.lockOrder(orderId)
                .orElseThrow(OrderNotFoundException::new);
        final int userId = order.getUserId();
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(OrderNotFoundException::new);

        order.refundItem(orderItemId);

        // don't refund coupon
        BalanceEntity balance = balanceRepository.lock(userId).get();
        balance.deposit(payment.getCashFee());
    }


    private int getOrderFee(Order order) {
        List<Integer> productIds = order.getItems().stream().map(OrderItem::getProductId).collect(toList());
        Map<Integer, Product> productMap = productRepository.findProductsByIds(productIds);
        return order.getItems().stream()
                .map(item -> getOrderItemFee(productMap, item))
                .reduce(0, (a, b) -> a + b);
    }

    private int getOrderItemFee(Map<Integer, Product> productMap, OrderItem item) {
        return item.getQuantity() * productMap.get(item.getProductId()).getPrice();
    }

    @Override
    public void cancelOrder(int orderId) throws CommerceException {
        OrderEntity order = orderRepository.lockOrder(orderId).orElseThrow(OrderNotFoundException::new);
        if (!order.cancel()) {
            return;
        }
        List<? extends OrderItem> sortedItems = new ArrayList<>(order.getItems());
        sortedItems.sort(Comparator.<OrderItem>comparingInt(OrderItem::getProductId));
        for (OrderItem item : sortedItems) {
            // lock in product order
            ProductEntity product = productRepository.lock(item.getProductId())
                    .orElseThrow(InvalidProductException::new);
            product.releaseQuota(item.getVariantId(), item.getQuantity());
        }
    }

    @Override
    public int createOrder(int userId, List<OrderItem> items)
            throws CommerceException {
        log.info("txx= {}", TransactionSynchronizationManager.getCurrentTransactionName());

        OrderEntity order = orderRepository.createOrder(userId, items);
        List<? extends OrderItem> sortedItems = new ArrayList<>(order.getItems());
        sortedItems.sort(Comparator.<OrderItem>comparingInt(OrderItem::getProductId));
        for (OrderItem item : sortedItems) {
            // lock in product order
            ProductEntity product = productRepository.lock(item.getProductId())
                    .orElseThrow(InvalidProductException::new);
            product.useQuota(item.getVariantId(), item.getQuantity());
        }
        return order.getId();
    }

}
