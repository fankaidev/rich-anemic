package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.Product;
import net.fklj.richanemic.data.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductVariantRepository {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VariantRepository variantRepository;

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public Optional<Product> findProductById(int productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product;
    }

    public Optional<Variant> getVariant(int variantId) {
        return variantRepository.findById(variantId);
    }

    public List<Variant> getVariantByProductId(int productId) {
        return variantRepository.findByProductId(productId);
    }

    public Map<Integer, Product> findProductsByIds(Collection<Integer> productIds) {
        return  productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, o->o));
    }

    public Optional<Product> lock(int productId) {
        Optional<Product> productEntity = productRepository.lock(productId);
        return productEntity;
    }
}
