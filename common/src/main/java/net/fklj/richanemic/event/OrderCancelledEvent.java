package net.fklj.richanemic.event;

import lombok.Getter;
import lombok.Setter;
import net.fklj.richanemic.data.Order;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OrderCancelledEvent extends ApplicationEvent {

    public OrderCancelledEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

    private Order order;

}
