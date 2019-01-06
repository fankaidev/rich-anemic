package net.fklj.richanemic.adm.service;

import net.fklj.richanemic.adm.data.OrderItem;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderAggregateService extends OrderService {

    @Transactional(rollbackFor = Exception.class)
    int createOrder(int userId, List<OrderItem> items) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void cancelOrder(int orderId) throws OrderNotFoundException;
}
