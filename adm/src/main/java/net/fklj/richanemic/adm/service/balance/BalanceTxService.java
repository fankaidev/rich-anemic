package net.fklj.richanemic.adm.service.balance;

import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InvalidBalanceAmountException;
import org.springframework.transaction.annotation.Transactional;

public interface BalanceTxService extends BalanceService {

    @Transactional(rollbackFor = Exception.class)
    void deposit(int userId, int amount) throws InvalidBalanceAmountException;

    @Transactional(rollbackFor = Exception.class)
    void use(int userId, int amount) throws CommerceException;
}
