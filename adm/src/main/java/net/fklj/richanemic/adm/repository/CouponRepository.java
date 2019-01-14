package net.fklj.richanemic.adm.repository;

import net.fklj.richanemic.data.Coupon;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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

@Mapper
public interface CouponRepository {

    @Select("SELECT * FROM coupon WHERE userId = #{userId}")
    List<Coupon> getCouponsOfUser(int userId);

    @Insert("INSERT INTO coupon (id, userId, value, used) " +
            "VALUES (#{id}, #{userId}, #{value}, #{used})")
    void saveCoupon(Coupon coupon);

    @Update("UPDATE coupon SET used = 1 WHERE id = #{couponId}")
    void updateCouponUsed(int couponId);

    @Select("SELECT * FROM coupon WHERE id = #{couponId} FOR UPDATE")
    Coupon lockCoupon(int couponId);
}
