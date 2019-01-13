package net.fklj.richanemic.service.order;

import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import net.fklj.richanemic.data.OrderItem;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderTxService extends OrderService {

    @Transactional(rollbackFor = Exception.class)
    int create(int userId, List<OrderItem> items) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    boolean cancel(int orderId) throws OrderNotFoundException;

    @Transactional(rollbackFor = Exception.class)
    void cancelWithEvent(int orderId) throws OrderNotFoundException;

    @Transactional(rollbackFor = Exception.class)
    void pay(int orderId, int couponId, int cashFee) throws OrderNotFoundException;

    @Transactional(rollbackFor = Exception.class)
    void refundItem(int orderId, int orderItemId) throws OrderNotFoundException;

}
