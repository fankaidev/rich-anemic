package net.fklj.richanemic.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fklj.richanemic.data.OrderItemStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    protected int id;

    protected int orderId;

    protected int productId;

    protected int variantId;

    protected int quantity;

    protected OrderItemStatus status;

}
