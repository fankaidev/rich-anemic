package net.fklj.richanemic.adm.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Payment {

    private int orderId;

    private int userId;

    private List<OrderItem> items;

    private OrderStatus status;

}
