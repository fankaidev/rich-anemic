package net.fklj.richanemic.adm.service;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.adm.data.Order;
import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.adm.data.Payment;
import net.fklj.richanemic.adm.data.Product;
import net.fklj.richanemic.adm.repository.OrderRepository;
import net.fklj.richanemic.adm.repository.PaymentRepository;
import net.fklj.richanemic.adm.service.balance.BalanceTxService;
import net.fklj.richanemic.adm.service.coupon.CouponTxService;
import net.fklj.richanemic.adm.service.order.OrderTxService;
import net.fklj.richanemic.adm.service.product.ProductTxService;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import net.fklj.richanemic.data.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderTxService orderService;

    @Autowired
    private ProductTxService productService;

    @Autowired
    private BalanceTxService balanceService;

    @Autowired
    private CouponTxService couponService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public void callbackVariant(int productId, int variantId) throws CommerceException {
        List<OrderItem> items = orderRepository.getOrderItemsByVariantId(variantId);
        for (OrderItem item : items) {
            // TODO: lock
            Order order = orderService.getOrder(item.getOrderId())
                    .orElseThrow(OrderNotFoundException::new);
            if (order.getStatus() == OrderStatus.PAID) {
                refundOrderItem(order.getId(), item.getId());
            } else {
                cancelOrder(order.getId());
            }
        }
        productService.inactivateVariant(productId, variantId);
    }


    @Override
    public void payOrder(int orderId, int couponId) throws CommerceException {
        Order order = orderService.getOrder(orderId)
                .orElseThrow(OrderNotFoundException::new);
        final int userId = order.getUserId();
        final int fee = getOrderFee(order);
        final int couponFee = couponService.useCoupon(couponId);
        final int cashFee = fee - couponFee;
        balanceService.use(userId, cashFee);

        orderService.pay(orderId, couponId, cashFee);
    }

    @Override
    public void refundOrderItem(int orderId, int orderItemId) throws CommerceException {
        Order order = orderService.getOrder(orderId)
                .orElseThrow(OrderNotFoundException::new);
        final int userId = order.getUserId();
        OrderItem item = order.getItems().stream().filter(it -> it.getId() == orderItemId).findAny()
                .orElseThrow(OrderNotFoundException::new);
        Payment payment = paymentRepository.getPaymentOfOrder(orderId)
                .orElseThrow(OrderNotFoundException::new);

        orderService.refundItem(orderId, item);

        // don't refund coupon
        balanceService.deposit(userId, payment.getCashFee());
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

    @Override
    public void cancelOrder(int orderId) throws CommerceException {
        if (!orderService.cancel(orderId)) {
            return;
        }

        Order order = orderRepository.getOrder(orderId).orElseThrow(OrderNotFoundException::new);
        for (OrderItem item : order.getItems()) {
            productService.releaseQuota(item.getProductId(), item.getVariantId(), item.getQuantity());
        }
    }

    @Override
    public int createOrder(int userId, List<OrderItem> items)
            throws CommerceException {
        int orderId = orderService.create(userId, items);
        for (OrderItem item : items) {
            productService.useQuota(item.getProductId(), item.getVariantId(), item.getQuantity());
        }
        return orderId;
    }

}
