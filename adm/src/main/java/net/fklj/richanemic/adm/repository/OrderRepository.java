package net.fklj.richanemic.adm.repository;

import net.fklj.richanemic.data.Order;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.data.OrderItemStatus;
import net.fklj.richanemic.data.OrderStatus;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.jdbc.SQL;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Mapper
public interface OrderRepository {

    @Insert("INSERT INTO `order` (id, userId, status) " +
            "VALUES (#{id}, #{userId}, #{status})")
    void saveOrder(Order order);

    @Insert("INSERT INTO order_item (orderId, productId, variantId, quantity, status) " +
            "VALUES (#{orderId}, #{productId}, #{variantId}, #{quantity}, #{status})")
    @Options(useGeneratedKeys=true)
    void saveItem(OrderItem item);

    @Select("SELECT * FROM `order` WHERE id = #{orderId}")
    Order getOrder(int orderId);

    @Select("SELECT * FROM `order` WHERE id = #{orderId} FOR UPDATE")
    Order lockOrder(int orderId);

    @Select("SELECT * FROM order_item WHERE orderId = #{orderId}")
    List<OrderItem> getItemsOfOrder(int orderId);

    @Update("UPDATE `order` SET status = #{status} WHERE id = #{orderId}")
    void updateOrderStatus(int orderId, OrderStatus status);

    @Update("UPDATE `order_item` SET status = #{status} WHERE id = #{orderItemId}")
    void updateOrderItemStatus(int orderItemId, OrderItemStatus status);

    @Select("SELECT * FROM order_item WHERE id = #{orderItemId}")
    OrderItem getOrderItem(int orderItemId);

    @SelectProvider(type= SqlProvider.class, method="getOrderItemsByOrderItemIds")
    List<OrderItem> getOrderItemsByOrderItemIds(
            @Param("orderItemIds") Collection<Integer> orderItemIds);

    @Select("SELECT * FROM order_item WHERE productId = #{productId}")
    List<OrderItem> getOrderItemsByProductId(int productId);

    @Select("SELECT * FROM order_item WHERE variantId = #{variantId}")
    List<OrderItem> getOrderItemsByVariantId(int variantId);


    class SqlProvider {

        public static String getOrderItemsByOrderItemIds(Map<String, Object> params){
            Collection<Integer> orderItemIds = (Collection<Integer>) params.get("orderItemIds");
            String str = orderItemIds.stream().map(i -> i.toString()).collect(joining(","));
            return new SQL(){{
                SELECT("*");
                FROM("order_item");
                WHERE("id in ("+ str + ")");
            }}.toString();
        }

    }
}
