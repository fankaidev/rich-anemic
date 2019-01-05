package net.fklj.richanemic.adm;

import net.fklj.richanemic.adm.data.Order;
import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.adm.data.OrderStatus;
import net.fklj.richanemic.adm.exception.CommerceException;
import net.fklj.richanemic.adm.exception.CommerceException.DuplicateProductException;
import net.fklj.richanemic.adm.exception.CommerceException.InactiveProductException;
import net.fklj.richanemic.adm.exception.CommerceException.InactiveVariantException;
import net.fklj.richanemic.adm.exception.CommerceException.InvalidQuantityException;
import net.fklj.richanemic.adm.exception.CommerceException.ProductOutOfStockException;
import net.fklj.richanemic.adm.exception.CommerceException.VariantMismatchException;
import net.fklj.richanemic.adm.exception.CommerceException.VariantOutOfStockException;
import net.fklj.richanemic.adm.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
public class OrderTest extends BaseTest {

    @Autowired
    private OrderService orderService;

    private int createOrder(int userId, int productId, int variantId, int quantity)
            throws CommerceException {
        OrderItem item = genItem(productId, variantId, quantity);
        List<OrderItem> items = Collections.singletonList(item);
        return orderService.createOrder(userId, items);
    }

    private OrderItem genItem(int productId, int variantId, int quantity) {
        return OrderItem.builder()
                    .productId(productId)
                    .variantId(variantId)
                    .quantity(quantity)
                    .build();
    }

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
        orderService.cancelOrder(orderId);
        Order order = orderService.getOrder(orderId).orElseThrow(RuntimeException::new);
        assertThat(order.getStatus(), is(OrderStatus.CANCELLED));

        orderService.cancelOrder(orderId); // allow double cancel
    }

    @Test
    public void testCreateOrderWithMultiItems() throws CommerceException {
        OrderItem item1 = genItem(PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, 1);
        OrderItem item2 = genItem(PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
        int orderId = orderService.createOrder(USER1_ID, Arrays.asList(item1, item2));
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

    @Test(expected = VariantOutOfStockException.class)
    public void testCreateOrderWithVariantOutOfStock() throws CommerceException {
        createOrder(USER1_ID, PRODUCT2_Q0_ID, P2_VAR3_Q1_ID, 2);
    }

    @Test(expected = VariantOutOfStockException.class)
    public void testCreateMultiOrderWithVariantOutOfStock() throws CommerceException {
        createOrder(USER1_ID, PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
        createOrder(USER1_ID, PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
    }

    @Test
    public void testCreateOrderAfterCancel() throws CommerceException {
        int orderId = createOrder(USER1_ID, PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
        orderService.cancelOrder(orderId);
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
        orderService.createOrder(USER1_ID, Arrays.asList(item1, item2));
    }

}
