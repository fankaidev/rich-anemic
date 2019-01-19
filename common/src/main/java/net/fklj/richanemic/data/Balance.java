package net.fklj.richanemic.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fklj.richanemic.data.CommerceException.InsufficientBalanceException;
import net.fklj.richanemic.data.CommerceException.InvalidBalanceAmountException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.Id;

@Slf4j
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Balance {

    @Id
    protected int userId;

    protected int amount;

    @Transactional(rollbackFor = Exception.class)
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
