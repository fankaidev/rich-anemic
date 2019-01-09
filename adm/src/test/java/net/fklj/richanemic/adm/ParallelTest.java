package net.fklj.richanemic.adm;

import net.fklj.richanemic.adm.service.AppService;
import net.fklj.richanemic.adm.service.balance.BalanceService;
import net.fklj.richanemic.adm.service.coupon.CouponService;
import net.fklj.richanemic.adm.service.order.OrderTxService;
import net.fklj.richanemic.data.CommerceException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.fklj.richanemic.data.Constants.VOID_COUPON_ID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@EnableTransactionManagement
public class ParallelTest extends BaseTest {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private OrderTxService orderService;

    @Autowired
    private AppService appService;

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    @Test
    public void testPay() throws CommerceException, InterruptedException {
        int initAmount = 10000;
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount));

        int quantity = 1000;
        int orderId1 = createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, quantity);
        int orderId2 = createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, quantity);

        executor.execute(() -> {
            try {
                appService.payOrder(orderId1, VOID_COUPON_ID);
            } catch (CommerceException e) {
                e.printStackTrace();
            }
        });
        executor.execute(() -> {
            try {
                appService.payOrder(orderId2, VOID_COUPON_ID);
            } catch (CommerceException e) {
                e.printStackTrace();
            }
        });

        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(0));
    }

}
