package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
public class BalanceRepository {

    @Autowired
    private NamedParameterJdbcOperations db;

    private static final RowMapper<Balance> ACCOUNT_MAPPER =
            new BeanPropertyRowMapper<>(Balance.class);

    public void changeAmount(int userId, int amount) {
        db.update("UPDATE balance SET amount = amount + :amount WHERE userId = :userId",
                new MapSqlParameterSource("userId", userId).addValue("amount", amount));
    }

    public Balance get(int userId) {
        return db.queryForObject("SELECT * FROM balance WHERE userId = :userId",
                Collections.singletonMap("userId", userId), ACCOUNT_MAPPER);
    }

    public Balance lock(int userId) {
        return db.queryForObject("SELECT * FROM balance WHERE userId = :userId FOR UPDATE",
                Collections.singletonMap("userId", userId), ACCOUNT_MAPPER);
    }
}
