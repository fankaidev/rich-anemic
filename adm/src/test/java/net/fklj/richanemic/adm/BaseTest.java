package net.fklj.richanemic.adm;

import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.adm.repository.BalanceRepository;
import net.fklj.richanemic.adm.repository.CouponRepository;
import net.fklj.richanemic.adm.repository.OrderRepository;
import net.fklj.richanemic.adm.repository.PaymentRepository;
import net.fklj.richanemic.adm.repository.ProductRepository;
import net.fklj.richanemic.adm.service.BalanceService;
import net.fklj.richanemic.adm.service.CouponService;
import net.fklj.richanemic.adm.service.OrderAggregateService;
import net.fklj.richanemic.adm.service.OrderAggregateServiceImpl;
import net.fklj.richanemic.adm.service.PayService;
import net.fklj.richanemic.adm.service.ProductAggregateService;
import net.fklj.richanemic.adm.service.ProductAggregateServiceImpl;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.OrderItemStatus;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static java.util.Collections.singletonList;
import static net.fklj.richanemic.data.Constants.PRODUCT_QUOTA_INFINITY;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {
        TestDbConfiguration.class,
        ProductAggregateServiceImpl.class, OrderAggregateServiceImpl.class,
        ProductRepository.class, OrderRepository.class,
        BalanceService.class, BalanceRepository.class,
        CouponService.class, CouponRepository.class,
        PayService.class, PaymentRepository.class
})
public abstract class BaseTest {

    @Autowired
    private ProductAggregateService productService;

    @Autowired
    private OrderAggregateService orderService;

    @Autowired
    private CouponService couponService;

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

    protected int USER1_COUPON1_VALUE_10_ID;

    protected int USER1_COUPON2_VALUE_20_ID;

    @Before
    public void BaseTest() throws CommerceException {
        PRODUCT1_INACTIVE_ID = productService.createProduct(PRODUCT_PRICE, PRODUCT_QUOTA_INFINITY);
        P1_VAR1_INACTIVE_ID = productService.createVariant(PRODUCT1_INACTIVE_ID, PRODUCT_QUOTA_INFINITY);

        PRODUCT2_Q0_ID = productService.createProduct(PRODUCT_PRICE, PRODUCT_QUOTA_INFINITY);
        productService.activateProduct(PRODUCT2_Q0_ID);
        P2_VAR1_INACTIVE_ID = productService.createVariant(PRODUCT2_Q0_ID, PRODUCT_QUOTA_INFINITY);
        P2_VAR2_Q0_ID = productService.createVariant(PRODUCT2_Q0_ID, PRODUCT_QUOTA_INFINITY);
        productService.activateVariant(P2_VAR2_Q0_ID);
        P2_VAR3_Q1_ID = productService.createVariant(PRODUCT2_Q0_ID, 1);
        productService.activateVariant(P2_VAR3_Q1_ID);

        PRODUCT3_Q9_ID = productService.createProduct(PRODUCT_PRICE, 9);
        productService.activateProduct(PRODUCT3_Q9_ID);
        P3_VAR1_Q1_ID = productService.createVariant(PRODUCT3_Q9_ID, 1);
        productService.activateVariant(P3_VAR1_Q1_ID);
        P3_VAR2_Q2_ID = productService.createVariant(PRODUCT3_Q9_ID, 2);
        productService.activateVariant(P3_VAR1_Q1_ID);

        USER1_COUPON1_VALUE_10_ID = couponService.grantCoupon(USER1_ID, 10);
        USER1_COUPON2_VALUE_20_ID = couponService.grantCoupon(USER1_ID, 20);
    }


    protected int createOrder(int userId, int productId, int variantId, int quantity)
            throws CommerceException {
        OrderItem item = genItem(productId, variantId, quantity);
        List<OrderItem> items = singletonList(item);
        return orderService.createOrder(userId, items);
    }

    protected OrderItem genItem(int productId, int variantId, int quantity) {
        return OrderItem.builder()
                .productId(productId)
                .variantId(variantId)
                .quantity(quantity)
                .status(OrderItemStatus.PENDING)
                .build();
    }

}
