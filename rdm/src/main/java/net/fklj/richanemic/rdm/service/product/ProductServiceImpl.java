package net.fklj.richanemic.rdm.service.product;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InvalidProductException;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.data.Product;
import net.fklj.richanemic.data.Variant;
import net.fklj.richanemic.event.OrderCancelledEvent;
import net.fklj.richanemic.rdm.repository.ProductVariantRepository;
import net.fklj.richanemic.service.product.ProductTxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductTxService {

    @Autowired
    private ProductVariantRepository productRepository;

    @Override
    public Optional<Product> getProduct(int productId) {
        return productRepository.findProductById(productId).map(o->o);
    }

    @Override
    public Optional<Variant> getVariant(int variantId) {
        return productRepository.getVariant(variantId).map(o -> o);
    }

    @Override
    public List<Variant> getVariantsOfProduct(int productId) {
        return new ArrayList<>(productRepository.getVariantByProductId(productId));
    }

    @Override
    public Map<Integer, Product> getProducts(Collection<Integer> productIds) {
        return productRepository.findProductsByIds(productIds);
    }

    /*************************** transaction ********************/

    private Product lock(int productId) throws InvalidProductException {
        Product product = productRepository.lock(productId).orElseThrow(InvalidProductException::new);

        // mock delay
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            // do nothing
        }

        return product;
    }

    @Override
    public int createProduct(int price, int quota) throws CommerceException {
        int price1 = price;
        int quota1 = quota;
        Product product = new Product(price1, quota1);
        productRepository.saveProduct(product);
        return product.getId();
    }

    @Override
    public int createVariant(int productId, int quota) throws CommerceException {
        Product product = lock(productId);
        return product.createVariant(quota);
    }

    @Override
    public int createProductWithDefaultVariant(int price, int quantity) throws CommerceException {
        int productId = createProduct(price, quantity);
        createVariant(productId, quantity);
        return productId;
    }

    @Override
    public void activateProduct(int productId) throws InvalidProductException {
        lock(productId).activate();
    }

    @Override
    public void inactivateProduct(int productId) throws InvalidProductException {
        lock(productId).inactivate();
    }

    @Override
    public void activateVariant(int productId, int variantId) throws CommerceException {
        lock(productId).activateVariant(variantId);
    }

    @Override
    public void inactivateVariant(int productId, int variantId) throws CommerceException {
        lock(productId).inactivateVariant(variantId);
    }

    @Override
    public void useQuota(int productId, int variantId, int quantity) throws CommerceException {
        lock(productId).useQuota(variantId, quantity);
    }

    @Override
    public void releaseQuota(int productId, int variantId, int quantity) throws CommerceException {
        lock(productId).releaseQuota(variantId, quantity);
    }

    // TODO: idempotent
    @Override
    @EventListener
    public void onOrderCancelled(OrderCancelledEvent event) throws CommerceException {
        log.info("on order cancelled {}", event.getOrder().getId());
        for (OrderItem item : event.getOrder().getItems()) {
            releaseQuota(item.getProductId(), item.getVariantId(), item.getQuantity());
        }
    }

}
