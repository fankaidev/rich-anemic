package net.fklj.richanemic.adm;

import net.fklj.richanemic.adm.data.Coupon;
import net.fklj.richanemic.adm.data.Order;
import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.adm.data.Payment;
import net.fklj.richanemic.adm.service.BalanceService;
import net.fklj.richanemic.adm.service.CouponService;
import net.fklj.richanemic.adm.service.OrderService;
import net.fklj.richanemic.adm.service.PayService;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.OrderItemStatus;
import net.fklj.richanemic.data.OrderStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static net.fklj.richanemic.data.Constants.VOID_COUPON_ID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PayTest extends BaseTest {

    @Autowired
    private PayService payService;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private OrderService orderService;

    @Test
    public void testPay() throws CommerceException {
        int initAmount = 10000;
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount));

        int quantity = 5;
        int orderId = createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, quantity);
        payService.payOrder(orderId, VOID_COUPON_ID);
        int cashFee = PRODUCT_PRICE * 5;
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount -
                cashFee));

        Payment payment = payService.getPaymentOfOrder(orderId).get();
        assertThat(payment.getCashFee(), is(cashFee));
        assertThat(payment.getOrderId(), is(orderId));
        assertThat(payment.getCouponId(), is(VOID_COUPON_ID));
        assertThat(payment.getUserId(), is(USER1_ID));

        Order order = orderService.getOrder(orderId).get();
        assertThat(order.getStatus(), is(OrderStatus.PAID));

    }

    @Test
    public void testPayWithCoupon() throws CommerceException {
        int initAmount = 10000;
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount));

        int quantity = 5;
        int orderId = createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, quantity);
        payService.payOrder(orderId, USER1_COUPON2_VALUE_20_ID);
        final int couponFee = 20;
        final int cashFee = PRODUCT_PRICE * quantity - couponFee;
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount - cashFee));

        Order order = orderService.getOrder(orderId).get();
        assertThat(order.getStatus(), is(OrderStatus.PAID));
        Coupon coupon = couponService.getCoupon(USER1_ID, USER1_COUPON2_VALUE_20_ID).get();
        assertThat(coupon.isUsed(), is(true));
    }


    @Test
    public void testRefund() throws CommerceException {
        int initAmount = 10000;
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount));

        int quantity = 5;
        int orderId = createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, quantity);
        payService.payOrder(orderId, USER1_COUPON2_VALUE_20_ID);

        Order order = orderService.getOrder(orderId).get();
        assertThat(order.getStatus(), is(OrderStatus.PAID));
        OrderItem item = order.getItems().get(0);
        payService.refundOrderItem(orderId, item.getId());

        Order afterRefund = orderService.getOrder(orderId).get();
        assertThat(afterRefund.getStatus(), is(OrderStatus.PAID));
        assertThat(afterRefund.getItems().get(0).getStatus(), is(OrderItemStatus.REFUNDED));

        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount));

        Coupon coupon = couponService.getCoupon(USER1_ID, USER1_COUPON2_VALUE_20_ID).get();
        assertThat(coupon.isUsed(), is(true));
    }

}
