package net.fklj.richanemic.adm.service.coupon;

import org.springframework.transaction.annotation.Transactional;

public interface CouponTxService extends CouponService {

    @Transactional(rollbackFor = Exception.class)
    void useCoupon(int couponId);

    @Transactional(rollbackFor = Exception.class)
    int grantCoupon(int userId, int value);

}
