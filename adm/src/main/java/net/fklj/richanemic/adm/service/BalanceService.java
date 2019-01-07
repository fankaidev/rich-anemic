package net.fklj.richanemic.adm.service;

import net.fklj.richanemic.adm.data.Balance;
import net.fklj.richanemic.adm.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {

    @Autowired
    private BalanceRepository balanceRepository;

    public void deposit(int userId, int amount) {
        balanceRepository.increaseAmount(userId, amount);
    }

    public Balance getBalance(int userId) {
        return balanceRepository.get(userId);
    }

    public void use(int userId, int amount) {
        balanceRepository.increaseAmount(userId, -amount);
    }
}
