package net.fklj.richanemic;

import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.ProductOutOfStockException;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.data.Product;
import net.fklj.richanemic.data.Variant;
import net.fklj.richanemic.rdm.repository.ProductRepository;
import net.fklj.richanemic.service.AppService;
import net.fklj.richanemic.service.order.OrderTxService;
import net.fklj.richanemic.service.product.ProductTxService;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

@EnableTransactionManagement
public class TransactionTest extends BaseTest {

    @SpyBean
    private ProductTxService productService;

    @SpyBean
    private ProductRepository productRepository;

    @SpyBean
    private AppService appService;

    @Autowired
    private OrderTxService orderService;

    @Test
    public void testCreateOrder() throws CommerceException {
        Mockito.doAnswer(invocationOnMock -> {
            Product product = invocationOnMock.getArgument(0);
            if (product.getId() == PRODUCT3_Q9_ID) {
                throw new RuntimeException("mock");
            }
            return null;
        }).when(productRepository).saveProduct(any());

        OrderItem item1 = genItem(PRODUCT2_Q0_ID, P2_VAR2_Q0_ID, 1);
        OrderItem item2 = genItem(PRODUCT3_Q9_ID, P3_VAR1_Q1_ID, 1);
        try {
            appService.createOrder(USER1_ID, asList(item1, item2));
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
        int orderId = appService.createOrder(USER1_ID, asList(item1, item2));

        // mock failure for release quota
        Mockito.doAnswer(invocationOnMock -> {
            Product product = invocationOnMock.getArgument(0);
            if (product.getId() == PRODUCT3_Q9_ID && product.getSoldCount() == 0) {
                throw new RuntimeException("mock");
            }
            return null;
        }).when(productRepository).saveProduct(any());

        try {
            appService.cancelOrder(orderId);
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
