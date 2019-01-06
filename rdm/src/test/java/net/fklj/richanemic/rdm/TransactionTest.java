package net.fklj.richanemic.rdm;

import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.ProductOutOfStockException;
import net.fklj.richanemic.rdm.entity.OrderItem;
import net.fklj.richanemic.rdm.entity.Product;
import net.fklj.richanemic.rdm.entity.Variant;
import net.fklj.richanemic.rdm.repository.ProductRepository;
import net.fklj.richanemic.rdm.service.OrderAggregateService;
import net.fklj.richanemic.rdm.service.ProductAggregateService;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@EnableTransactionManagement
public class TransactionTest extends BaseTest {

    @SpyBean
    private ProductAggregateService productService;

    @SpyBean
    private ProductRepository productRepository;

    @Autowired
    private OrderAggregateService orderService;

    @Test
    public void testCreateOrder() throws CommerceException {
        Mockito.doThrow(new RuntimeException()).when(productRepository).saveProduct(
                argThat(product -> product.getId() == PRODUCT3_Q9_ID));

        OrderItem item1 = genItem(PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, 1);
        OrderItem item2 = genItem(PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
        try {
            orderService.createOrder(USER1_ID, asList(item1, item2));
        } catch (Exception e) {
            // do nothing
        }

        validateSoldCount(PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, 0);
        validateSoldCount(PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 0);
    }

    @Test
    public void testCancelOrder() throws CommerceException {
        OrderItem item1 = genItem(PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, 1);
        OrderItem item2 = genItem(PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
        int orderId = orderService.createOrder(USER1_ID, asList(item1, item2));

        Mockito.doThrow(new RuntimeException()).when(productRepository).saveProduct(
                argThat(product -> product.getId() == PRODUCT3_Q9_ID));
        try {
            orderService.cancelOrder(orderId);
        } catch (Exception e) {
            // do nothing
        }

        validateSoldCount(PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, 1);
        validateSoldCount(PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
    }

    private void validateSoldCount(int productId, int variantId, int expectedCount) {
        Product product = productService.getProduct(productId).get();
        Variant variant = productService.getVariant(variantId).get();
        assertThat(product.getSoldCount(), is(expectedCount));
        assertThat(variant.getSoldCount(), is(expectedCount));
    }

}
