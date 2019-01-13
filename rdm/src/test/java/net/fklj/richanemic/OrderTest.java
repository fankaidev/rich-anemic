package net.fklj.richanemic;

import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.DuplicateProductException;
import net.fklj.richanemic.data.CommerceException.InactiveProductException;
import net.fklj.richanemic.data.CommerceException.InactiveVariantException;
import net.fklj.richanemic.data.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.data.CommerceException.ProductOutOfStockException;
import net.fklj.richanemic.data.CommerceException.VariantMismatchException;
import net.fklj.richanemic.data.Order;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.data.OrderStatus;
import net.fklj.richanemic.service.AppService;
import net.fklj.richanemic.service.order.OrderTxService;
import net.fklj.richanemic.service.product.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class OrderTest extends BaseTest {

    @Autowired
    private OrderTxService orderService;

    @Autowired
    private AppService appService;

    @Autowired
    private ProductService productService;

    @Test
    public void testCreateOrder() throws CommerceException {
        int quantity = 1;
        int orderId = createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, quantity);
        Order order = orderService.getOrder(orderId).orElseThrow(RuntimeException::new);
        assertThat(order.getId(), is(orderId));
        assertThat(order.getStatus(), is(OrderStatus.PENDING));
        assertThat(order.getUserId(), is(USER1_ID));
        assertThat(order.getItems(), hasSize(1));

        OrderItem item = order.getItems().get(0);
        assertThat(item.getQuantity(), is(quantity));
        assertThat(item.getOrderId(), is(orderId));
        assertThat(item.getProductId(), is(PRODUCT2_Q0_ID));
        assertThat(item.getVariantId(), is(P2_VAR2_Q0_ID));
    }

    @Test
    public void testCancelOrder() throws CommerceException {
        int quantity = 1;
        int orderId = createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, quantity);
        appService.cancelOrder(orderId);
        Order order = orderService.getOrder(orderId).orElseThrow(RuntimeException::new);
        assertThat(order.getStatus(), is(OrderStatus.CANCELLED));

        appService.cancelOrder(orderId); // allow double cancel
        assertThat(productService.getProduct(PRODUCT2_Q0_ID).get().getSoldCount(), is(0));
    }

    @Test
    public void testGetOrderItems() throws CommerceException {
        createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, 1);
        createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, 1);
        createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR3_Q1_ID, 1);
        List<OrderItem> productItems = orderService.getOrderItemsByProductId(PRODUCT2_Q0_ID);
        assertThat(productItems, hasSize(3));
        List<OrderItem> variantItems = orderService.getOrderItemsByVariantId(P2_VAR2_Q0_ID);
        assertThat(variantItems, hasSize(2));
        assertThat(orderService.getOrderItemsByVariantId(P1_VAR1_INACTIVE_ID), hasSize(0));
        int itemId = variantItems.get(0).getId();
        Optional<OrderItem> item = orderService.getOrderItem(itemId);
        assertTrue(item.isPresent());
        Map<Integer, OrderItem> itemMap = orderService.getOrderItemsByOrderItemIds(
                singletonList(itemId));
        assertThat(itemMap, hasKey(item.get().getId()));
    }

    @Test
    public void testCreateOrderWithMultiItems() throws CommerceException {
        OrderItem item1 = genItem(PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, 1);
        OrderItem item2 = genItem(PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
        int orderId = appService.createOrder(USER1_ID, Arrays.asList(item1, item2));
        Order order = orderService.getOrder(orderId).orElseThrow(RuntimeException::new);
        assertThat(order.getItems(), hasSize(2));
    }

    @Test(expected = InvalidQuantityException.class)
    public void testCreateOrderWithInactiveQuantity() throws CommerceException {
        createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, -1);
    }

    @Test(expected = InactiveProductException.class)
    public void testCreateOrderWithInactiveProduct() throws CommerceException {
        createOrder(USER1_ID, PRODUCT1_INACTIVE_ID, P1_VAR1_INACTIVE_ID, 1);
    }

    @Test(expected = InactiveVariantException.class)
    public void testCreateOrderWithInactiveVariant() throws CommerceException {
        createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR1_INACTIVE_ID, 1);
    }

    @Test(expected = ProductOutOfStockException.class)
    public void testCreateOrderWithProductOutOfStock() throws CommerceException {
        createOrder(USER1_ID, PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 10);
    }

    @Test(expected = ProductOutOfStockException.class)
    public void testCreateMultiOrderWithProductOutOfStock() throws CommerceException {
        createOrder(USER1_ID, PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
        createOrder(USER1_ID, PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 9);
    }

    @Test(expected = ProductOutOfStockException.class)
    public void testCreateOrderWithVariantOutOfStock() throws CommerceException {
        createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR3_Q1_ID, 2);
    }

    @Test(expected = ProductOutOfStockException.class)
    public void testCreateMultiOrderWithVariantOutOfStock() throws CommerceException {
        createOrder(USER1_ID, PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
        createOrder(USER1_ID, PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
    }

    @Test
    public void testCreateOrderAfterCancel() throws CommerceException {
        int orderId = createOrder(USER1_ID, PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
        appService.cancelOrder(orderId);
        createOrder(USER1_ID, PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
    }

    @Test(expected = VariantMismatchException.class)
    public void testCreateOrderWithMismatch() throws CommerceException {
        createOrder(USER1_ID, PRODUCT1_INACTIVE_ID, P2_VAR1_INACTIVE_ID, 2);
    }

    @Test(expected = DuplicateProductException.class)
    public void testCreateOrderWithDuplicates() throws CommerceException {
        OrderItem item1 = genItem(PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, 1);
        OrderItem item2 = genItem(PRODUCT2_Q0_ID, P2_VAR3_Q1_ID, 1);
        appService.createOrder(USER1_ID, Arrays.asList(item1, item2));
    }

}
