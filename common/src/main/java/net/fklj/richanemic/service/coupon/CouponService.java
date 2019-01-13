package net.fklj.richanemic.service.coupon;

import net.fklj.richanemic.data.Coupon;

import java.util.List;
import java.util.Optional;

public interface CouponService {

    List<Coupon> getCouponsOfUser(int userId);

    Optional<Coupon> getCoupon(int userId, int couponId);
}
