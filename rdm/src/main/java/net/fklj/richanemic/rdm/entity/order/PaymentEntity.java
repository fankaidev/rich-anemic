package net.fklj.richanemic.rdm.entity.order;

import net.fklj.richanemic.data.Payment;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PaymentEntity extends Payment {

    @Id
    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public int getOrderId() {
        return super.getOrderId();
    }

    @Override
    public int getUserId() {
        return super.getUserId();
    }

    @Override
    public int getCashFee() {
        return super.getCashFee();
    }

    @Override
    public int getCouponId() {
        return super.getCouponId();
    }
}
