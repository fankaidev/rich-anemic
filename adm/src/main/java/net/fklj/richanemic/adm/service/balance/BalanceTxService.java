package net.fklj.richanemic.adm.service.balance;

public interface BalanceTxService extends BalanceService {

    void deposit(int userId, int amount);

    void use(int userId, int amount);
}
