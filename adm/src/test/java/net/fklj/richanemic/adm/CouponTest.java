package net.fklj.richanemic.adm;

import net.fklj.richanemic.adm.data.Coupon;
import net.fklj.richanemic.adm.service.CouponService;
import net.fklj.richanemic.data.CommerceException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CouponTest extends BaseTest {

    @Autowired
    private CouponService couponService;

    @Test
    public void test() throws CommerceException {
        Optional<Coupon> coupon = couponService.getCoupon(USER1_ID, USER1_COUPON_10_ID);
        assertTrue(coupon.isPresent());
        assertThat(coupon.get().getValue(), is(10));
        assertThat(coupon.get().isUsed(), is(false));

        couponService.useCoupon(coupon.get().getId());
        Optional<Coupon> afterUse = couponService.getCoupon(USER1_ID, USER1_COUPON_10_ID);
        assertThat(afterUse.get().isUsed(), is(true));

        assertThat(couponService.getCouponsOfUser(USER1_ID), hasSize(2));

    }

}
