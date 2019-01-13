package net.fklj.richanemic.rdm.entity;

import lombok.Setter;
import net.fklj.richanemic.data.OrderItem;
import net.fklj.richanemic.rdm.repository.OrderRepository;

@Setter
public class OrderEntity extends OrderItem {

    private OrderRepository orderRepository;



}
