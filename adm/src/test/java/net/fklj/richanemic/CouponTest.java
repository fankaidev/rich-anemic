package net.fklj.richanemic;

import net.fklj.richanemic.data.Coupon;
import net.fklj.richanemic.service.coupon.CouponTxService;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.CouponNotFoundException;
import net.fklj.richanemic.data.CommerceException.CouponUsedException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static net.fklj.richanemic.data.Constants.VOID_COUPON_ID;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CouponTest extends BaseTest {

    @Autowired
    private CouponTxService couponService;

    @Test
    public void test() throws CommerceException {
        Optional<Coupon> coupon = couponService.getCoupon(USER1_ID, USER1_COUPON_10_ID);
        assertTrue(coupon.isPresent());
        assertThat(coupon.get().getValue(), is(10));
        assertThat(coupon.get().isUsed(), is(false));

        int couponFee = couponService.useCoupon(coupon.get().getId());
        Optional<Coupon> afterUse = couponService.getCoupon(USER1_ID, USER1_COUPON_10_ID);
        assertThat(afterUse.get().isUsed(), is(true));
        assertThat(couponFee, is(10));

        assertThat(couponService.getCouponsOfUser(USER1_ID), hasSize(2));
    }

    @Test
    public void testUseVoidCoupon() throws CommerceException {
        assertThat(couponService.useCoupon(VOID_COUPON_ID), is(0));
        assertThat(couponService.useCoupon(VOID_COUPON_ID), is(0));
    }

    @Test(expected = CouponNotFoundException.class)
    public void testUseInvalid() throws CommerceException {
        couponService.useCoupon(-1);
    }

    @Test(expected = CouponUsedException.class)
    public void testDoubleUse() throws CommerceException {
        couponService.useCoupon(USER1_COUPON_10_ID);
        couponService.useCoupon(USER1_COUPON_10_ID);
    }

}
