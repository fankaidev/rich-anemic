package net.fklj.richanemic.adm.service.order;

import net.fklj.richanemic.adm.data.Order;
import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.data.CommerceException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderTxService extends OrderService {

    @Transactional(rollbackFor = Exception.class)
    int create(int userId, List<OrderItem> items) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void cancel(Order order);

    @Transactional(rollbackFor = Exception.class)
    void pay(Order order, int couponId, int cashFee);

    @Transactional(rollbackFor = Exception.class)
    void refundItem(OrderItem item);

}
