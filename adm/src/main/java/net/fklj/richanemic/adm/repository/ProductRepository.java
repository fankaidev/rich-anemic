package net.fklj.richanemic.adm.repository;

import net.fklj.richanemic.data.Product;
import net.fklj.richanemic.data.ProductStatus;
import net.fklj.richanemic.data.Variant;
import net.fklj.richanemic.data.VariantStatus;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.jdbc.SQL;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Mapper
public interface ProductRepository {

    @Insert("INSERT INTO product (id, quota, price, soldCount, status) " +
            "VALUES (#{id}, #{quota}, #{price}, #{soldCount}, #{status})")
    void saveProduct(Product product);

    @Insert("INSERT INTO variant (id, productId, quota, soldCount, status) " +
            "VALUES (#{id}, #{productId}, #{quota}, #{soldCount}, #{status})")
    void saveVariant(Variant variant);

    @Select("SELECT * FROM product WHERE id = #{productId} FOR UPDATE")
    Product lockProduct(int productId);

    @Select("SELECT * FROM product WHERE id = #{productId}")
    Product getProduct(int productId);

    @Select("SELECT * FROM variant WHERE id = #{variantId}")
    Variant getVariant(int variantId);

    @Select("SELECT * FROM variant WHERE productId = #{productId}")
    List<Variant> getVariantByProductId(int productId);

    @Update("UPDATE product SET status = #{status} WHERE id = #{productId}")
    void updateProductStatus(int productId, ProductStatus status);

    @Update("UPDATE variant SET status = #{status} WHERE id = #{variantId}")
    void updateVariantStatus(int variantId, VariantStatus status);

    @Update("UPDATE variant SET soldCount = soldCount + #{quantity} " +
            "WHERE id = #{variantId} AND (quota = 0 OR quota >= soldCount + #{quantity})")
    boolean increaseVariantSoldCount(int variantId, int quantity);

    @Update("UPDATE product SET soldCount = soldCount + #{quantity} " +
            "WHERE id = #{productId} AND (quota = 0 OR quota >= soldCount + #{quantity})")
    boolean increaseProductSoldCount(int productId, int quantity);

    @SelectProvider(type=SqlProvider.class, method="getProducts")
//    @Select("SELECT * FROM product WHERE id in (#{productIds})")
    List<Product> getProducts(@Param("productIds") Collection<Integer> productIds);

    class SqlProvider {

        public static String getProducts(Map<String, Object> params){
            Collection<Integer> productIds = (Collection<Integer>) params.get("productIds");
            String str = productIds.stream().map(i -> i.toString()).collect(joining(","));
            return new SQL(){{
                SELECT("*");
                FROM("product");
                WHERE("id in ("+ str + ")");
            }}.toString();
        }

    }

}
