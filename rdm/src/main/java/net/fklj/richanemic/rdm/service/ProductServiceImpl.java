package net.fklj.richanemic.rdm.service;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.rdm.entity.Variant;
import net.fklj.richanemic.rdm.entity.Product;
import net.fklj.richanemic.rdm.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Optional<Product> getProduct(int productId) {
        return productRepository.getProduct(productId);
    }

    @Override
    public Optional<Variant> getVariant(int variantId) {
        return productRepository.getVariant(variantId);
    }

    @Override
    public List<Variant> getVariantsOfProduct(int productId) {
        return productRepository.getVariantByProductId(productId);
    }

}
