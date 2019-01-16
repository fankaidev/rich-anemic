package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.rdm.entity.coupon.CouponEntity;

import java.util.List;

public interface CouponRepository extends BaseRepository<CouponEntity, Integer> {

    List<CouponEntity> findByUserId(int userId);

}
