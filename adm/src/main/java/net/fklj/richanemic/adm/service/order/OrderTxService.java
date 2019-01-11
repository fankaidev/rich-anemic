package net.fklj.richanemic.adm.service.order;

import net.fklj.richanemic.adm.data.Order;
import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderTxService extends OrderService {

    @Transactional(rollbackFor = Exception.class)
    int create(int userId, List<OrderItem> items) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void cancel(int orderId) throws OrderNotFoundException;

    @Transactional(rollbackFor = Exception.class)
    void pay(int orderId, int couponId, int cashFee) throws OrderNotFoundException;

    @Transactional(rollbackFor = Exception.class)
    void refundItem(int orderId, OrderItem item) throws OrderNotFoundException;

}
