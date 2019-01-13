package net.fklj.richanemic.service.product;

import net.fklj.richanemic.data.Product;
import net.fklj.richanemic.data.Variant;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {

    Optional<Product> getProduct(int productId);

    Optional<Variant> getVariant(int variantId);

    List<Variant> getVariantsOfProduct(int productId);

    Map<Integer, Product> getProducts(Collection<Integer> productIds);

}
