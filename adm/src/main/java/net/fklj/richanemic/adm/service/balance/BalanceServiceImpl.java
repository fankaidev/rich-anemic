package net.fklj.richanemic.adm.service.balance;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.Balance;
import net.fklj.richanemic.adm.repository.BalanceRepository;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InsufficientBalanceException;
import net.fklj.richanemic.data.CommerceException.InvalidBalanceAmountException;
import net.fklj.richanemic.service.balance.BalanceTxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BalanceServiceImpl implements BalanceTxService {

    @Autowired
    private BalanceRepository balanceRepository;

    @Override
    public void deposit(int userId, int amount) throws InvalidBalanceAmountException {
        if (amount <= 0) {
            throw new InvalidBalanceAmountException();
        }
        balanceRepository.lock(userId);
        balanceRepository.changeAmount(userId, amount);
    }

    @Override
    public Balance getBalance(int userId) {
        return balanceRepository.get(userId);
    }

    @Override
    public void use(int userId, int amount) throws CommerceException {
        if (amount <= 0) {
            throw new InvalidBalanceAmountException();
        }
        Balance balance = balanceRepository.lock(userId);
        log.info("use balance, userId={}, amount={}, balance={}", userId, amount, balance);
        if (balance.getAmount() < amount) {
            throw new InsufficientBalanceException();
        }
        balanceRepository.changeAmount(userId, -amount);
        log.info("use balance done");
    }
}
