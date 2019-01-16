package net.fklj.richanemic.rdm.service.balance;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.Balance;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InvalidBalanceAmountException;
import net.fklj.richanemic.rdm.entity.balance.BalanceEntity;
import net.fklj.richanemic.rdm.repository.BalanceRepository;
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
        BalanceEntity balance = balanceRepository.lock(userId).get();
        balance.deposit(amount);
    }

    @Override
    public Balance getBalance(int userId) {
        return balanceRepository.findById(userId).get();
    }

    @Override
    public void use(int userId, int amount) throws CommerceException {
        BalanceEntity balance = balanceRepository.lock(userId).get();
        balance.use(amount);
    }
}
