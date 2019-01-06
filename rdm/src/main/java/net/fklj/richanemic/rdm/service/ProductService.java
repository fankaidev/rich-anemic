package net.fklj.richanemic.rdm.service;

import net.fklj.richanemic.rdm.entity.Variant;
import net.fklj.richanemic.rdm.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Optional<Product> getProduct(int productId);

    Optional<Variant> getVariant(int variantId);

    List<Variant> getVariantsOfProduct(int productId);
}
