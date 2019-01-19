package net.fklj.richanemic.rdm.service.coupon;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.CommerceException.CouponNotFoundException;
import net.fklj.richanemic.data.CommerceException.CouponUsedException;
import net.fklj.richanemic.data.CommerceException.InvalidCouponException;
import net.fklj.richanemic.data.Coupon;
import net.fklj.richanemic.rdm.repository.CouponRepository;
import net.fklj.richanemic.service.coupon.CouponTxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Id;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.fklj.richanemic.data.Constants.VOID_COUPON_ID;

@Slf4j
@Service
public class CouponServiceImpl implements CouponTxService {

    @Autowired
    private CouponRepository couponRepository;

    @Override
    public int useCoupon(int couponId) throws CouponNotFoundException, CouponUsedException {
        Coupon coupon = couponId == VOID_COUPON_ID ? Coupon.VOID_COUPON :
                couponRepository.lock(couponId).orElseThrow(CouponNotFoundException::new);
        return coupon.use();
    }

    @Override
    public List<Coupon> getCouponsOfUser(int userId) {
        List<Coupon> byUserId = couponRepository.findByUserId(userId);
        return byUserId
                .stream()
                .map(ce -> (Coupon)ce)
                .collect(Collectors.toList());
    }

    @Override
    public int grantCoupon(int userId, int value) throws InvalidCouponException {
        int userId1 = userId;
        int value1 = value;
        Coupon coupon = new Coupon(userId1, value1);
        coupon = couponRepository.save(coupon);
        return coupon.getId();
    }

    @Override
    public Optional<Coupon> getCoupon(int userId, int couponId) {
        return getCouponsOfUser(userId).stream()
                .filter(coupon -> coupon.getId() == couponId)
                .findAny();
    }

}
