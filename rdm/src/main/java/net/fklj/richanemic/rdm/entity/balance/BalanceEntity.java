package net.fklj.richanemic.rdm.entity.balance;

import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.Balance;
import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InsufficientBalanceException;
import net.fklj.richanemic.data.CommerceException.InvalidBalanceAmountException;
import net.fklj.richanemic.rdm.entity.AggregateRoot;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.Id;

@Slf4j
@Entity
public class BalanceEntity extends Balance implements AggregateRoot {

    @Override
    @Id
    public int getUserId() {
        return userId;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    // in lock mode
    public void deposit(int depositAmount) throws InvalidBalanceAmountException {
        if (depositAmount <= 0) {
            throw new InvalidBalanceAmountException();
        }
        this.amount += depositAmount;
    }

    @Transactional(rollbackFor = Exception.class)
    public void use(int useAmount) throws CommerceException {
        if (useAmount <= 0) {
            throw new InvalidBalanceAmountException();
        }
        log.info("use balance, userId={}, amount={}, balance={}", userId, useAmount, toString());
        if (this.amount < useAmount) {
            throw new InsufficientBalanceException();
        }
        this.amount -= useAmount;
    }

}
