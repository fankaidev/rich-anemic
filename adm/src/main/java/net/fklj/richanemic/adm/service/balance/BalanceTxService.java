package net.fklj.richanemic.adm.service.balance;

import net.fklj.richanemic.data.CommerceException.BalanceException;
import org.springframework.transaction.annotation.Transactional;

public interface BalanceTxService extends BalanceService {

    @Transactional(rollbackFor = Exception.class)
    void deposit(int userId, int amount);

    @Transactional(rollbackFor = Exception.class)
    void use(int userId, int amount) throws BalanceException;
}
