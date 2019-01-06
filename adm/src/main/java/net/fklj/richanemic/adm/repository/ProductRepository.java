package net.fklj.richanemic.adm.repository;

import com.google.common.collect.ImmutableMap;
import net.fklj.richanemic.adm.data.Product;
import net.fklj.richanemic.adm.data.ProductStatus;
import net.fklj.richanemic.adm.data.Variant;
import net.fklj.richanemic.adm.data.VariantStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonMap;

@Repository
public class ProductRepository {

    @Autowired
    private NamedParameterJdbcOperations db;

    private static final RowMapper<Product> PRODUCT_MAPPER =
            new BeanPropertyRowMapper<>(Product.class);

    private static final RowMapper<Variant> VARIANT_MAPPER =
            new BeanPropertyRowMapper<>(Variant.class);


    public void saveProduct(Product product) {
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(product);
        source.registerSqlType("status", Types.VARCHAR);
        db.update("INSERT INTO product (id, quota, price, soldCount, status) " +
                        "VALUES (:id, :quota, :price, :soldCount, :status)",
                source);
    }

    public void saveVariant(Variant variant) {
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(variant);
        source.registerSqlType("status", Types.VARCHAR);
        db.update("INSERT INTO variant (id, productId, quota, soldCount, status) " +
                        "VALUES (:id, :productId, :quota, :soldCount, :status)",
                source);
    }

    public Optional<Product> getProduct(int productId) {
        Product result = db.queryForObject("SELECT * FROM product WHERE id = :productId",
                singletonMap("productId", productId), PRODUCT_MAPPER);
        return Optional.ofNullable(result);
    }

    public Optional<Variant> getVariant(int variantId) {
        Variant result = db.queryForObject("SELECT * FROM variant WHERE id = :variantId",
                singletonMap("variantId", variantId), VARIANT_MAPPER);
        return Optional.ofNullable(result);
    }

    public List<Variant> getVariantByProductId(int productId) {
        return db.query("SELECT * FROM variant WHERE productId = :productId",
                singletonMap("productId", productId), VARIANT_MAPPER);
    }

    public void updateProductStatus(int productId, ProductStatus status) {
        db.update("UPDATE product SET status = :status WHERE id = :id",
                ImmutableMap.of("status", status.toString(), "id", productId));
    }

    public void updateVariantStatus(int variantId, VariantStatus status) {
        db.update("UPDATE variant SET status = :status WHERE id = :id",
                ImmutableMap.of("status", status.toString(), "id", variantId));
    }

    public boolean increaseVariantSoldCount(int variantId, int quantity) {
        return db.update("UPDATE variant SET soldCount = soldCount + :quantity " +
                        "WHERE id = :id AND (quota = 0 OR quota >= soldCount + :quantity)",
                ImmutableMap.of("id", variantId, "quantity", quantity)) > 0;
    }

    public boolean increaseProductSoldCount(int productId, int quantity) {
        return db.update("UPDATE product SET soldCount = soldCount + :quantity " +
                        "WHERE id = :id AND (quota = 0 OR quota >= soldCount + :quantity)",
                ImmutableMap.of("id", productId, "quantity", quantity)) > 0;
    }
}
