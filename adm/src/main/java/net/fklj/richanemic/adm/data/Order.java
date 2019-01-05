package net.fklj.richanemic.adm.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private int id;

    private int userId;

    private List<OrderItem> items;

    private OrderStatus status;

}
