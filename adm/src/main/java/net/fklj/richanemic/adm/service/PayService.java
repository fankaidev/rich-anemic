package net.fklj.richanemic.adm.service;

import net.fklj.richanemic.adm.data.Coupon;
import net.fklj.richanemic.adm.data.Order;
import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.adm.data.Product;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.CouponNotFoundException;
import net.fklj.richanemic.data.CommerceException.CouponUserdException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
public class PayService {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Transactional(rollbackFor = Exception.class)
    public void payOrder(int orderId, int couponId) throws CommerceException {
        Order order = orderService.getOrder(orderId)
                .orElseThrow(() -> new OrderNotFoundException());
        final int userId = order.getUserId();
        final int fee = getOrderFee(order);
        final int couponFee;
        if (couponId > 0) {
            Coupon coupon = couponService.getCoupon(userId, couponId).orElseThrow(() -> new CouponNotFoundException());
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

}
