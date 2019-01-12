package net.fklj.richanemic.adm;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.adm.service.AppService;
import net.fklj.richanemic.adm.service.order.OrderTxService;
import net.fklj.richanemic.data.CommerceException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;

@Slf4j
@EnableTransactionManagement
public class PerformanceTest extends BaseTest {

    @Autowired
    private AppService appService;

    @Autowired
    private OrderTxService orderService;

    @Test
    public void testCreateOrder() throws CommerceException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        long startMillis = System.currentTimeMillis();

        for (int i = 0; i < 30; ++i) {
            executorService.submit(() -> {
                OrderItem item = genItem(PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, 1);
                try {
                    int orderId = appService.createOrder(USER1_ID, singletonList(item));
                    orderService.cancelWithEvent(orderId);
//                    appService.cancelOrder(orderId);
                } catch (CommerceException e) {
                    log.error("exception", e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.SECONDS);
        long endMillis = System.currentTimeMillis();
        log.info("cost - {} ms", endMillis - startMillis);
    }

}
