package net.fklj.richanemic.adm.service;

import net.fklj.richanemic.adm.data.Coupon;
import net.fklj.richanemic.adm.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public void useCoupon(int couponId) {
        couponRepository.updateCouponUsed(couponId);
    }

    public List<Coupon> getCouponsOfUser(int userId) {
        return couponRepository.getCouponsOfUser(userId);
    }

    public int grantCoupon(int userId, int value) {
        int couponId = new Random().nextInt();
        Coupon coupon = Coupon.builder().id(couponId)
                .userId(userId).value(value).build();
        couponRepository.saveCoupon(coupon);
        return couponId;
    }

    public Optional<Coupon> getCoupon(int userId, int couponId) {
        return getCouponsOfUser(userId).stream()
                .filter(coupon -> coupon.getId() == couponId)
                .findAny();
    }

}
