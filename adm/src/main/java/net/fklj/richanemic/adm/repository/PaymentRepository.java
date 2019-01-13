package net.fklj.richanemic.adm.repository;

import net.fklj.richanemic.data.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Optional;

@Repository
public class PaymentRepository {

    @Autowired
    private NamedParameterJdbcOperations db;

    private static final RowMapper<Payment> PAYMENT_MAPPER =
            new BeanPropertyRowMapper<>(Payment.class);

    public void savePayment(Payment payment) {
        db.update("INSERT INTO payment (id, userId, orderId, cashFee, couponId) " +
                        "VALUES (:id, :userId, :orderId, :cashFee, :couponId) ",
                new BeanPropertySqlParameterSource(payment));
    }

    public Optional<Payment> getPayment(int paymentId) {
        try {
            Payment payment = db.queryForObject("SELECT * FROM payment WHERE id = :id",
                    Collections.singletonMap("id", paymentId), PAYMENT_MAPPER);
            return Optional.of(payment);

        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }

    }

    public Optional<Payment> getPaymentOfOrder(int orderId) {
        try {
            Payment payment = db.queryForObject("SELECT * FROM payment WHERE orderId = :orderId",
                    Collections.singletonMap("orderId", orderId), PAYMENT_MAPPER);
            return Optional.of(payment);

        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }

    }

}
