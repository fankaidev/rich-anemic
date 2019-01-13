package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.Coupon;
import net.fklj.richanemic.rdm.entity.CouponEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.fklj.richanemic.data.Constants.VOID_COUPON_ID;

@Repository
public class CouponRepository {

    @Autowired
    private NamedParameterJdbcOperations db;

    private static final RowMapper<Coupon> COUPON_MAPPER =
            new BeanPropertyRowMapper<>(Coupon.class);

    private static final RowMapper<CouponEntity> COUPON_ENTITY_MAPPER =
            new BeanPropertyRowMapper<>(CouponEntity.class);

    public List<Coupon> getCouponsOfUser(int userId) {
        return db.query("SELECT * FROM coupon WHERE userId = :userId",
                Collections.singletonMap("userId", userId), COUPON_MAPPER);
    }

    public void save(Coupon coupon) {
        db.update("INSERT INTO coupon (id, userId, value, used) " +
                        "VALUES (:id, :userId, :value, :used) " +
                        "ON DUPLICATE KEY UPDATE used = :used",
                new BeanPropertySqlParameterSource(coupon));
    }

    public Optional<CouponEntity> lockCoupon(int couponId) {
         try {
             CouponEntity coupon = couponId == VOID_COUPON_ID ? CouponEntity.VOID_COUPON
                     : db.queryForObject("SELECT * FROM coupon WHERE id = :id FOR UPDATE",
                     Collections.singletonMap("id", couponId), COUPON_ENTITY_MAPPER);
             coupon.setCouponRepository(this);
            return Optional.ofNullable(coupon);
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }
}
