package net.fklj.richanemic;

import net.fklj.richanemic.BaseTest.SpringConfig;
import net.fklj.richanemic.data.Balance;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.data.OrderItemStatus;
import net.fklj.richanemic.rdm.repository.BalanceRepository;
import net.fklj.richanemic.service.AppService;
import net.fklj.richanemic.service.coupon.CouponTxService;
import net.fklj.richanemic.service.product.ProductTxService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Id;
import java.util.List;

import static java.util.Collections.singletonList;
import static net.fklj.richanemic.data.Constants.PRODUCT_QUOTA_INFINITY;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@DataJpaTest
@AutoConfigurationPackage
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ContextConfiguration(classes = {
        SpringConfig.class
})
public abstract class BaseTest {

    @Configuration
    @ComponentScan("net.fklj.richanemic.rdm")
    public static class SpringConfig {}

    @Autowired
    private ProductTxService productService;

    @Autowired
    private AppService appService;

    @Autowired
    private CouponTxService couponService;

    @Autowired
    private BalanceRepository balanceRepository;

    protected int PRODUCT1_INACTIVE_ID;
    protected int P1_VAR1_INACTIVE_ID;

    protected int PRODUCT2_Q0_ID;
    protected int P2_VAR1_INACTIVE_ID;
    protected int P2_VAR2_Q0_ID;
    protected int P2_VAR3_Q1_ID;

    protected int PRODUCT3_Q9_ID;
    protected int P3_VAR1_Q1_ID;
    protected int P3_VAR2_Q2_ID;

    protected final int PRODUCT_PRICE = 10;

    protected final int USER1_ID = 999;

    protected int USER1_COUPON_10_ID;

    protected int USER1_COUPON_20_ID;

    @Before
    public void BaseTest() throws CommerceException {
        PRODUCT1_INACTIVE_ID = productService.createProduct(PRODUCT_PRICE, PRODUCT_QUOTA_INFINITY);
        P1_VAR1_INACTIVE_ID = productService.createVariant(PRODUCT1_INACTIVE_ID, PRODUCT_QUOTA_INFINITY);

        PRODUCT2_Q0_ID = productService.createProduct(PRODUCT_PRICE, PRODUCT_QUOTA_INFINITY);
        productService.activateProduct(PRODUCT2_Q0_ID);
        P2_VAR1_INACTIVE_ID = productService.createVariant(PRODUCT2_Q0_ID, PRODUCT_QUOTA_INFINITY);
        P2_VAR2_Q0_ID = productService.createVariant(PRODUCT2_Q0_ID, PRODUCT_QUOTA_INFINITY);
        productService.activateVariant(PRODUCT2_Q0_ID, P2_VAR2_Q0_ID);
        P2_VAR3_Q1_ID = productService.createVariant(PRODUCT2_Q0_ID, 1);
        productService.activateVariant(PRODUCT2_Q0_ID, P2_VAR3_Q1_ID);

        PRODUCT3_Q9_ID = productService.createProduct(PRODUCT_PRICE, 9);
        productService.activateProduct(PRODUCT3_Q9_ID);
        P3_VAR1_Q1_ID = productService.createVariant(PRODUCT3_Q9_ID, 1);
        productService.activateVariant(PRODUCT3_Q9_ID, P3_VAR1_Q1_ID);
        P3_VAR2_Q2_ID = productService.createVariant(PRODUCT3_Q9_ID, 2);
        productService.activateVariant(PRODUCT3_Q9_ID, P3_VAR1_Q1_ID);

        USER1_COUPON_10_ID = couponService.grantCoupon(USER1_ID, 10);
        USER1_COUPON_20_ID = couponService.grantCoupon(USER1_ID, 20);

        Balance balance = new Balance();
        balance.setUserId(USER1_ID);
        balance.setAmount(10000);
        balanceRepository.save(balance);
    }


    protected int createOrder(int userId, int productId, int variantId, int quantity)
            throws CommerceException {
        OrderItem item = genItem(productId, variantId, quantity);
        List<OrderItem> items = singletonList(item);
        return appService.createOrder(userId, items);
    }

    protected OrderItem genItem(int productId, int variantId, int quantity) {
        OrderItem item = OrderItem.builder()
                .productId(productId)
                .variantId(variantId)
                .quantity(quantity)
                .status(OrderItemStatus.PENDING)
                .build();

        OrderItem entity = new OrderItem();
        BeanUtils.copyProperties(item, entity);
        return entity;
    }

}
