package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.Balance;
import net.fklj.richanemic.rdm.entity.BalanceEntity;
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

    private static final RowMapper<BalanceEntity> ACCOUNT_MAPPER =
            new BeanPropertyRowMapper<>(BalanceEntity.class);

    public void save(int userId, int amount) {
        db.update("UPDATE balance SET amount = :amount WHERE userId = :userId",
                new MapSqlParameterSource("userId", userId).addValue("amount", amount));
    }

    public Balance get(int userId) {
        return db.queryForObject("SELECT * FROM balance WHERE userId = :userId",
                Collections.singletonMap("userId", userId), ACCOUNT_MAPPER);
    }

    public BalanceEntity lock(int userId) {
        BalanceEntity result = db.queryForObject("SELECT * FROM balance WHERE userId = :userId FOR UPDATE",
                Collections.singletonMap("userId", userId), ACCOUNT_MAPPER);
        result.setBalanceRepository(this);
        return result;
    }
}
