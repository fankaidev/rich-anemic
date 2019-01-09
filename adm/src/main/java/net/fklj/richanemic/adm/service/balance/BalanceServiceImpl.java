package net.fklj.richanemic.adm.service.balance;

import net.fklj.richanemic.adm.data.Balance;
import net.fklj.richanemic.adm.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BalanceServiceImpl implements BalanceTxService {

    @Autowired
    private BalanceRepository balanceRepository;

    @Override
    public void deposit(int userId, int amount) {
        balanceRepository.increaseAmount(userId, amount);
    }

    @Override
    public Balance getBalance(int userId) {
        return balanceRepository.get(userId);
    }

    @Override
    public void use(int userId, int amount) {
        balanceRepository.increaseAmount(userId, -amount);
    }
}
