package net.fklj.richanemic.adm.service;

import net.fklj.richanemic.adm.data.Coupon;
import net.fklj.richanemic.adm.data.Order;
import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.adm.data.Payment;
import net.fklj.richanemic.adm.data.Product;
import net.fklj.richanemic.adm.repository.OrderRepository;
import net.fklj.richanemic.adm.repository.PaymentRepository;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.CouponNotFoundException;
import net.fklj.richanemic.data.CommerceException.CouponUserdException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import net.fklj.richanemic.data.OrderItemStatus;
import net.fklj.richanemic.data.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static java.util.stream.Collectors.toList;
import static net.fklj.richanemic.data.Constants.VOID_COUPON_ID;

@Service
public class PayService {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private OrderAggregateService orderService;

    @Autowired
    private ProductAggregateService productService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional(rollbackFor = Exception.class)
    public void payOrder(int orderId, int couponId) throws CommerceException {
        Order order = orderService.getOrder(orderId)
                .orElseThrow(OrderNotFoundException::new);
        final int userId = order.getUserId();
        final int fee = getOrderFee(order);
        final int couponFee;
        if (couponId != VOID_COUPON_ID) {
            Coupon coupon = couponService.getCoupon(userId, couponId)
                    .orElseThrow(CouponNotFoundException::new);
            if (coupon.isUsed()) {
                throw new CouponUserdException();
            }
            couponFee = Math.min(coupon.getValue(), fee);
            couponService.useCoupon(couponId);
        } else {
            couponFee = 0;
        }
        final int cashFee = fee - couponFee;
        balanceService.use(userId, cashFee);

        Payment payment = Payment.builder()
                .id(new Random().nextInt())
                .orderId(orderId)
                .userId(userId)
                .cashFee(cashFee)
                .couponId(couponId)
                .build();
        paymentRepository.savePayment(payment);

        orderRepository.updateOrderStatus(orderId, OrderStatus.PAID);
        for (OrderItem item : order.getItems()) {
            orderRepository.updateOrderItemStatus(item.getId(), OrderItemStatus.PAID);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void refundOrderItem(int orderId, int orderItemId) throws CommerceException {
        Order order = orderService.getOrder(orderId)
                .orElseThrow(OrderNotFoundException::new);
        final int userId = order.getUserId();
        OrderItem item = order.getItems().stream().filter(it -> it.getId() == orderItemId).findAny()
                .orElseThrow(OrderNotFoundException::new);
        Payment payment = paymentRepository.getPaymentOfOrder(orderId)
                .orElseThrow(OrderNotFoundException::new);

        // don't refund coupon
        balanceService.deposit(userId, payment.getCashFee());
        orderRepository.updateOrderItemStatus(item.getId(), OrderItemStatus.REFUNDED);
    }

    private int getOrderFee(Order order) {
        List<Integer> productIds = order.getItems().stream().map(OrderItem::getProductId).collect(toList());
        Map<Integer, Product> productMap = productService.getProducts(productIds);
        return order.getItems().stream()
                .map(item -> getOrderItemFee(productMap, item))
                .reduce(0, (a, b) -> a + b);
    }

    private int getOrderItemFee(Map<Integer, Product> productMap, OrderItem item) {
        return item.getQuantity() * productMap.get(item.getProductId()).getPrice();
    }

    public Optional<Payment> getPaymentOfOrder(int orderId) {
        return paymentRepository.getPaymentOfOrder(orderId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void callbackVariant(int variantId) throws CommerceException {
        List<OrderItem> items = orderRepository.getOrderItemsByVariantId(variantId);
        for (OrderItem item : items) {
            // TODO: lock
            Order order = orderService.getOrder(item.getOrderId())
                    .orElseThrow(OrderNotFoundException::new);
            if (order.getStatus() == OrderStatus.PAID) {
                refundOrderItem(order.getId(), item.getId());
            } else {
                orderService.cancelOrder(order.getId());
            }
        }
        productService.inactivateVariant(variantId);
    }

}
