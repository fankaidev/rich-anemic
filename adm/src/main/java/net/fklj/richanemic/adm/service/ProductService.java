package net.fklj.richanemic.adm.service;

import net.fklj.richanemic.adm.data.Product;
import net.fklj.richanemic.adm.data.Variant;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Optional<Product> getProduct(int productId);

    Optional<Variant> getVariant(int variantId);

    List<Variant> getVariantsOfProduct(int productId);
}
