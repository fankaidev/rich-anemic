package net.fklj.richanemic.adm.repository;

import com.google.common.collect.ImmutableMap;
import net.fklj.richanemic.data.Order;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.data.OrderItemStatus;
import net.fklj.richanemic.data.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;

@Service
public class OrderRepository {

    @Autowired
    private NamedParameterJdbcOperations db;

    private static final RowMapper<OrderItem> ORDER_ITEM_MAPPER =
            new BeanPropertyRowMapper<>(OrderItem.class);

    private static final RowMapper<Order> ORDER_MAPPER =
            new BeanPropertyRowMapper<>(Order.class);

    public void saveOrder(Order order) {
        BeanPropertySqlParameterSource orderSource = new BeanPropertySqlParameterSource(order);
        orderSource.registerSqlType("status", Types.VARCHAR);
        db.update("INSERT INTO `order` (id, userId, status) " +
                        "VALUES (:id, :userId, :status)",
                orderSource);
        for (OrderItem item : order.getItems()) {
            BeanPropertySqlParameterSource itemSource = new BeanPropertySqlParameterSource(item);
            itemSource.registerSqlType("status", Types.VARCHAR);
            // id is generated by db
            db.update("INSERT INTO order_item (orderId, productId, variantId, quantity, status) " +
                            "VALUES (:orderId, :productId, :variantId, :quantity, :status)",
                    itemSource);
        }
    }

    public Optional<Order> getOrder(int orderId) {
        try {
            Order order = db.queryForObject("SELECT * FROM `order` WHERE id = :id",
                    singletonMap("id", orderId),
                    ORDER_MAPPER);
            List<OrderItem> items = getItemsOfOrder(orderId);
            order.setItems(items);
            return Optional.of(order);
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Order> lockOrder(int orderId) {
        try {
            Order order = db.queryForObject("SELECT * FROM `order` WHERE id = :id FOR UPDATE",
                    singletonMap("id", orderId),
                    ORDER_MAPPER);
            List<OrderItem> items = getItemsOfOrder(orderId);
            order.setItems(items);
            return Optional.of(order);
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    private List<OrderItem> getItemsOfOrder(int orderId) {
        return db.query("SELECT * FROM order_item WHERE orderId = :orderId",
                singletonMap("orderId", orderId),
                ORDER_ITEM_MAPPER);
    }

    public void updateOrderStatus(int orderId, OrderStatus status) {
        db.update("UPDATE `order` SET status = :status WHERE id = :id",
                ImmutableMap.of("status", status.toString(), "id", orderId));
    }

    public void updateOrderItemStatus(int orderItemId, OrderItemStatus status) {
        db.update("UPDATE `order_item` SET status = :status WHERE id = :id",
                ImmutableMap.of("status", status.toString(), "id", orderItemId));
    }

    public Optional<OrderItem> getOrderItem(int orderItemId) {
        try {
            OrderItem item = db.queryForObject("SELECT * FROM order_item WHERE id = :id",
                    singletonMap("id", orderItemId), ORDER_ITEM_MAPPER);
            return Optional.of(item);
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    public Map<Integer, OrderItem> getOrderItemsByOrderItemIds(Collection<Integer> orderItemIds) {
        List<OrderItem> items = db.query("SELECT * FROM order_item WHERE id IN (:ids)",
                singletonMap("ids", orderItemIds),
                ORDER_ITEM_MAPPER);
        return items.stream().collect(Collectors.toMap(OrderItem::getId, Function.identity()));
    }

    public List<OrderItem> getOrderItemsByProductId(int productId) {
        return db.query("SELECT * FROM order_item WHERE productId = :productId",
                singletonMap("productId", productId),
                ORDER_ITEM_MAPPER);
    }

    public List<OrderItem> getOrderItemsByVariantId(int variantId) {
        return db.query("SELECT * FROM order_item WHERE variantId = :variantId",
                singletonMap("variantId", variantId),
                ORDER_ITEM_MAPPER);
    }
}
