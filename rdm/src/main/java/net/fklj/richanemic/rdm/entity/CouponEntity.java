package net.fklj.richanemic.rdm.entity;

import lombok.NoArgsConstructor;
import lombok.Setter;
import net.fklj.richanemic.data.CommerceException.CouponUsedException;
import net.fklj.richanemic.data.CommerceException.InvalidCouponException;
import net.fklj.richanemic.data.Coupon;
import net.fklj.richanemic.rdm.repository.CouponRepository;

import java.util.Random;

import static net.fklj.richanemic.data.Constants.VOID_COUPON_ID;

@Setter
@NoArgsConstructor
public class CouponEntity extends Coupon {

    public static final CouponEntity VOID_COUPON = new CouponEntity(VOID_COUPON_ID, 0, 0);

    private CouponRepository couponRepository;

    protected CouponEntity(int id, int userId, int value) {
        super(id, userId, value, false);

    }

    public CouponEntity(int userId, int value) throws InvalidCouponException {
        this(new Random().nextInt(), userId, value);
        if (value <= 0) {
            throw new InvalidCouponException();
        }
    }

    public int use() throws CouponUsedException {
        if (id == VOID_COUPON_ID) {
            // don't use coupon, simply return couponValue of 0
            return 0;
        }

        if (used) {
            throw new CouponUsedException();
        }

        this.used = true;
        save();
        return value;
    }

    private void save() {
        couponRepository.save(this);
    }


}
