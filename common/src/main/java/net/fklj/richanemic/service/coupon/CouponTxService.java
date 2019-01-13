package net.fklj.richanemic.service.coupon;

import net.fklj.richanemic.data.CommerceException.CouponNotFoundException;
import net.fklj.richanemic.data.CommerceException.CouponUsedException;
import org.springframework.transaction.annotation.Transactional;

public interface CouponTxService extends CouponService {

    /**
     * @return couponValue
     */
    @Transactional(rollbackFor = Exception.class)
    int useCoupon(int couponId) throws CouponNotFoundException, CouponUsedException;

    @Transactional(rollbackFor = Exception.class)
    int grantCoupon(int userId, int value);

}
