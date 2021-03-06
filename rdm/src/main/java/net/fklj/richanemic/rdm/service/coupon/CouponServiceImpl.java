package net.fklj.richanemic.rdm.service.coupon;

import net.fklj.richanemic.data.CommerceException.CouponNotFoundException;
import net.fklj.richanemic.data.CommerceException.CouponUsedException;
import net.fklj.richanemic.data.CommerceException.InvalidCouponException;
import net.fklj.richanemic.data.Coupon;
import net.fklj.richanemic.rdm.entity.coupon.CouponEntity;
import net.fklj.richanemic.rdm.repository.CouponRepository;
import net.fklj.richanemic.service.coupon.CouponTxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponServiceImpl implements CouponTxService {

    @Autowired
    private CouponRepository couponRepository;

    @Override
    public int useCoupon(int couponId) throws CouponNotFoundException, CouponUsedException {
        CouponEntity coupon = couponRepository.lockCoupon(couponId)
                .orElseThrow(CouponNotFoundException::new);
        return coupon.use();
    }

    @Override
    public List<Coupon> getCouponsOfUser(int userId) {
        return couponRepository.getCouponsOfUser(userId);
    }

    @Override
    public int grantCoupon(int userId, int value) throws InvalidCouponException {
        CouponEntity coupon = new CouponEntity(userId, value);
        couponRepository.save(coupon);
        return coupon.getId();
    }

    @Override
    public Optional<Coupon> getCoupon(int userId, int couponId) {
        return getCouponsOfUser(userId).stream()
                .filter(coupon -> coupon.getId() == couponId)
                .findAny();
    }

}
