package net.fklj.richanemic.adm.repository;

import net.fklj.richanemic.adm.data.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class CouponRepository {

    @Autowired
    private NamedParameterJdbcOperations db;

    private static final RowMapper<Coupon> COUPON_MAPPER =
            new BeanPropertyRowMapper<>(Coupon.class);

    public List<Coupon> getCouponsOfUser(int userId) {
        return db.query("SELECT * FROM coupon WHERE userId = :userId",
                Collections.singletonMap("userId", userId), COUPON_MAPPER);
    }

    public void saveCoupon(Coupon coupon) {
        db.update("INSERT INTO coupon (id, userId, value, used) " +
                "VALUES (:id, :userId, :value, :used) ",
                new BeanPropertySqlParameterSource(coupon));
    }

    public void updateCouponUsed(int couponId) {
        db.update("UPDATE coupon SET used = 1 WHERE id = :id",
                Collections.singletonMap("id", couponId));
    }
}
