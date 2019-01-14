package net.fklj.richanemic.adm.repository;

import net.fklj.richanemic.data.Payment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Optional;

@Mapper
public interface PaymentRepository {

    @Insert("INSERT INTO payment (id, userId, orderId, cashFee, couponId) " +
            "VALUES (#{id}, #{userId}, #{orderId}, #{cashFee}, #{couponId}) ")
    void savePayment(Payment payment);

    @Select("SELECT * FROM payment WHERE orderId = #{orderId}")
    Payment getPaymentOfOrder(int orderId);

}
