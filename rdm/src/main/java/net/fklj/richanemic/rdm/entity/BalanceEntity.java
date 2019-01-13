package net.fklj.richanemic.rdm.entity;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.Balance;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InsufficientBalanceException;
import net.fklj.richanemic.data.CommerceException.InvalidBalanceAmountException;
import net.fklj.richanemic.rdm.repository.BalanceRepository;

@Slf4j
@Setter
public class BalanceEntity extends Balance {

    private BalanceRepository balanceRepository;

    // in lock mode
    public void deposit(int depositAmount) throws InvalidBalanceAmountException {
        if (depositAmount <= 0) {
            throw new InvalidBalanceAmountException();
        }
        this.amount += depositAmount;
        save();
    }

    public void use(int useAmount) throws CommerceException {
        if (useAmount <= 0) {
            throw new InvalidBalanceAmountException();
        }
        log.info("use balance, userId={}, amount={}, balance={}", userId, useAmount, toString());
        if (this.amount < useAmount) {
            throw new InsufficientBalanceException();
        }
        this.amount -= useAmount;
        save();
    }

    private void save() {
        balanceRepository.save(userId, amount);
    }

}
