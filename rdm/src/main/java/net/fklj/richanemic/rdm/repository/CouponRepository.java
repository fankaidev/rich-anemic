package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.Coupon;

import java.util.List;

public interface CouponRepository extends BaseRepository<Coupon, Integer> {

    List<Coupon> findByUserId(int userId);

}
