package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.Payment;

import java.util.Optional;

public interface PaymentRepository extends BaseRepository<Payment, Integer> {

    Optional<Payment> findByOrderId(int orderId);
}
