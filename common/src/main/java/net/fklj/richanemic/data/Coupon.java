package net.fklj.richanemic.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fklj.richanemic.data.CommerceException.CouponUsedException;
import net.fklj.richanemic.data.CommerceException.InvalidCouponException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Random;

import static net.fklj.richanemic.data.Constants.VOID_COUPON_ID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    public static final Coupon VOID_COUPON = new Coupon(VOID_COUPON_ID, 0, 0);

    @Id
    protected int id;

    protected int userId;

    protected int value;

    protected boolean used;

    protected Coupon(int id, int userId, int value) {
        this(id, userId, value, false);
    }

    public Coupon(int userId, int value) throws InvalidCouponException {
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
