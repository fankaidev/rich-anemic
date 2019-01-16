package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.Product;
import net.fklj.richanemic.rdm.entity.product.ProductEntity;
import net.fklj.richanemic.rdm.entity.product.VariantEntity;
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

    public void saveProduct(ProductEntity product) {
        productRepository.save(product);
    }

    public Optional<ProductEntity> findProductById(int productId) {
        Optional<ProductEntity> product = productRepository.findById(productId);
        product.ifPresent(p -> p.setVariantRepository(variantRepository));
        return product;
    }

    public Optional<VariantEntity> getVariant(int variantId) {
        return variantRepository.findById(variantId);
    }

    public List<VariantEntity> getVariantByProductId(int productId) {
        return variantRepository.findByProductId(productId);
    }

    public Map<Integer, Product> findProductsByIds(Collection<Integer> productIds) {
        return  productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, o->o));
    }

    public Optional<ProductEntity> lock(int productId) {
        Optional<ProductEntity> productEntity = productRepository.lock(productId);
        productEntity.ifPresent(p -> p.setVariantRepository(variantRepository));
        return productEntity;
    }
}
