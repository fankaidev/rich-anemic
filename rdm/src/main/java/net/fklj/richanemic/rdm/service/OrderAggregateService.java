package net.fklj.richanemic.rdm.service;

import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.CreateOrderException;
import net.fklj.richanemic.data.CommerceException.OrderNotFoundException;
import net.fklj.richanemic.rdm.entity.OrderItem;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderAggregateService extends OrderService {

    @Transactional(rollbackFor = Exception.class)
    int createOrder(int userId, List<OrderItem> items) throws CommerceException;

    @Transactional(rollbackFor = Exception.class)
    void cancelOrder(int orderId) throws CommerceException;
}
