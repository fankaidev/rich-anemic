package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.rdm.entity.order.PaymentEntity;

import java.util.Optional;

public interface PaymentRepository extends BaseRepository<PaymentEntity, Integer> {

    Optional<PaymentEntity> findByOrderId(int orderId);
}
