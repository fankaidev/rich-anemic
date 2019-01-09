package net.fklj.richanemic.adm.service.coupon;

public interface CouponTxService extends CouponService {

    void useCoupon(int couponId);

    int grantCoupon(int userId, int value);

}
