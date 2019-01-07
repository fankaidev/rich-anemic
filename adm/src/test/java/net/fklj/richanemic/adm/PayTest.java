package net.fklj.richanemic.adm;

import net.fklj.richanemic.adm.service.BalanceService;
import net.fklj.richanemic.adm.service.PayService;
import net.fklj.richanemic.data.CommerceException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PayTest extends BaseTest {

    @Autowired
    private PayService payService;

    @Autowired
    private BalanceService balanceService;

    @Test
    public void test() throws CommerceException {
        int initAmount = 10000;
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount));

        int quantity = 5;
        int orderId = createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, quantity);
        payService.payOrder(orderId, 0);
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount - PRODUCT_PRICE * 5));
    }

    @Test
    public void testPayWithCoupon() throws CommerceException {
        int initAmount = 10000;
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount));

        int quantity = 5;
        int orderId = createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, quantity);
        payService.payOrder(orderId, USER1_COUPON2_VALUE_20_ID);
        final int couponFee = 20;
        final int cashFee = PRODUCT_PRICE * 5 - couponFee;
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount - cashFee));
    }

}
