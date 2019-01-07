package net.fklj.richanemic.adm.repository;

import net.fklj.richanemic.adm.data.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;

@Service
public class PaymentRepository {

    @Autowired
    private NamedParameterJdbcOperations db;

    private static final RowMapper<OrderItem> ACCOUNT_MAPPER =
            new BeanPropertyRowMapper<>(OrderItem.class);

}
