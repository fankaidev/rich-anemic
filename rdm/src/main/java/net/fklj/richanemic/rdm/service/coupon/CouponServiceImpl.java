package net.fklj.richanemic.rdm.service.coupon;

import net.fklj.richanemic.rdm.repository.CouponRepository;
import net.fklj.richanemic.data.CommerceException.CouponNotFoundException;
import net.fklj.richanemic.data.CommerceException.CouponUsedException;
import net.fklj.richanemic.data.Coupon;
import net.fklj.richanemic.service.coupon.CouponTxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static net.fklj.richanemic.data.Constants.VOID_COUPON_ID;

@Service
public class CouponServiceImpl implements CouponTxService {

    @Autowired
    private CouponRepository couponRepository;

    @Override
    public int useCoupon(int couponId) throws CouponNotFoundException, CouponUsedException {
        if (couponId == VOID_COUPON_ID) {
            // don't use coupon, simply return couponValue of 0
            return 0;
        }

        Coupon coupon = couponRepository.lockCoupon(couponId)
                .orElseThrow(CouponNotFoundException::new);
        if (coupon.isUsed()) {
            throw new CouponUsedException();
        }
        couponRepository.updateCouponUsed(couponId);
        return coupon.getValue();
    }

    @Override
    public List<Coupon> getCouponsOfUser(int userId) {
        return couponRepository.getCouponsOfUser(userId);
    }

    @Override
    public int grantCoupon(int userId, int value) {
        int couponId = new Random().nextInt();
        Coupon coupon = Coupon.builder().id(couponId)
                .userId(userId).value(value).build();
        couponRepository.saveCoupon(coupon);
        return couponId;
    }

    @Override
    public Optional<Coupon> getCoupon(int userId, int couponId) {
        return getCouponsOfUser(userId).stream()
                .filter(coupon -> coupon.getId() == couponId)
                .findAny();
    }

}
