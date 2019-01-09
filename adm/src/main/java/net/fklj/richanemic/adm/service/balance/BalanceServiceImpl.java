package net.fklj.richanemic.adm.service.balance;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.adm.data.Balance;
import net.fklj.richanemic.adm.repository.BalanceRepository;
import net.fklj.richanemic.data.CommerceException.BalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BalanceServiceImpl implements BalanceTxService {

    @Autowired
    private BalanceRepository balanceRepository;

    @Override
    public void deposit(int userId, int amount) {
        balanceRepository.changeAmount(userId, amount);
    }

    @Override
    public Balance getBalance(int userId) {
        return balanceRepository.get(userId);
    }

    @Override
    public void use(int userId, int amount) throws BalanceException {
        Balance balance = balanceRepository.lock(userId);
        log.info("use balance, userId={}, amount={}, balance={}", userId, amount, balance);
        if (balance.getAmount() < amount) {
            throw new BalanceException();
        }
        balanceRepository.changeAmount(userId, -amount);
        log.info("use balance done");
    }
}
