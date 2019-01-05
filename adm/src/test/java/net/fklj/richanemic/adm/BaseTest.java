package net.fklj.richanemic.adm;

import net.fklj.richanemic.adm.exception.CommerceException;
import net.fklj.richanemic.adm.repository.OrderRepository;
import net.fklj.richanemic.adm.repository.ProductRepository;
import net.fklj.richanemic.adm.service.OrderService;
import net.fklj.richanemic.adm.service.ProductService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static net.fklj.richanemic.adm.data.Constants.PRODUCT_QUOTA_INFINITY;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {
        TestDbConfiguration.class,
        ProductService.class, OrderService.class,
        ProductRepository.class, OrderRepository.class
})
public abstract class BaseTest {

    @Autowired
    private ProductService productService;

    protected int PRODUCT1_INACTIVE_ID;
    protected int P1_VAR1_INACTIVE_ID;

    protected int PRODUCT2_Q0_ID;
    protected int P2_VAR1_INACTIVE_ID;
    protected int P2_VAR2_Q0_ID;
    protected int P2_VAR3_Q1_ID;

    protected int PRODUCT3_Q9_ID;
    protected int P3_VAR1_Q1_ID;
    protected int P3_VAR2_Q2_ID;

    protected final int USER1_ID = 999;

    @Before
    public void BaseTest() throws CommerceException {
        PRODUCT1_INACTIVE_ID = productService.createProduct(10, PRODUCT_QUOTA_INFINITY);
        P1_VAR1_INACTIVE_ID = productService.createVariant(PRODUCT1_INACTIVE_ID, PRODUCT_QUOTA_INFINITY);

        PRODUCT2_Q0_ID = productService.createProduct(10, PRODUCT_QUOTA_INFINITY);
        productService.activateProduct(PRODUCT2_Q0_ID);
        P2_VAR1_INACTIVE_ID = productService.createVariant(PRODUCT2_Q0_ID, PRODUCT_QUOTA_INFINITY);
        P2_VAR2_Q0_ID = productService.createVariant(PRODUCT2_Q0_ID, PRODUCT_QUOTA_INFINITY);
        productService.activateVariant(P2_VAR2_Q0_ID);
        P2_VAR3_Q1_ID = productService.createVariant(PRODUCT2_Q0_ID, 1);
        productService.activateVariant(P2_VAR3_Q1_ID);

        PRODUCT3_Q9_ID = productService.createProduct(10, 9);
        productService.activateProduct(PRODUCT3_Q9_ID);
        P3_VAR1_Q1_ID = productService.createVariant(PRODUCT3_Q9_ID, 1);
        productService.activateVariant(P3_VAR1_Q1_ID);
        P3_VAR2_Q2_ID = productService.createVariant(PRODUCT3_Q9_ID, 2);
        productService.activateVariant(P3_VAR1_Q1_ID);
    }

}
