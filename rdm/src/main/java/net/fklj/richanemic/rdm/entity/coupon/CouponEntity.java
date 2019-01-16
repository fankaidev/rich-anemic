package net.fklj.richanemic.rdm.entity.coupon;

import lombok.NoArgsConstructor;
import lombok.Setter;
import net.fklj.richanemic.data.CommerceException.CouponUsedException;
import net.fklj.richanemic.data.CommerceException.InvalidCouponException;
import net.fklj.richanemic.data.Coupon;
import net.fklj.richanemic.rdm.entity.AggregateRoot;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Random;

import static net.fklj.richanemic.data.Constants.VOID_COUPON_ID;

@Setter
@NoArgsConstructor
@Entity
public class CouponEntity extends Coupon implements AggregateRoot {

    public static final CouponEntity VOID_COUPON = new CouponEntity(VOID_COUPON_ID, 0, 0);

    @Id
    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public int getUserId() {
        return super.getUserId();
    }

    @Override
    public int getValue() {
        return super.getValue();
    }

    @Override
    public boolean isUsed() {
        return super.isUsed();
    }

    protected CouponEntity(int id, int userId, int value) {
        super(id, userId, value, false);

    }

    public CouponEntity(int userId, int value) throws InvalidCouponException {
        this(new Random().nextInt(), userId, value);
        if (value <= 0) {
            throw new InvalidCouponException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int use() throws CouponUsedException {
        if (id == VOID_COUPON_ID) {
            // don't use coupon, simply return couponValue of 0
            return 0;
        }

        if (used) {
            throw new CouponUsedException();
        }

        this.used = true;
        return value;
    }


}
