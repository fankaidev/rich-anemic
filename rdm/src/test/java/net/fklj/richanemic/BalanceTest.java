package net.fklj.richanemic;

import net.fklj.richanemic.data.CommerceException;
import net.fklj.richanemic.data.CommerceException.InvalidBalanceAmountException;
import net.fklj.richanemic.service.balance.BalanceTxService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BalanceTest extends BaseTest {

    @Autowired
    private BalanceTxService balanceService;

    @Test
    public void test() throws CommerceException {
        int initAmount = 10000;
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount));

        balanceService.use(USER1_ID, 100);
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount - 100));

        balanceService.deposit(USER1_ID, 200);
        assertThat(balanceService.getBalance(USER1_ID).getAmount(), is(initAmount + 100));
    }

    @Test(expected = InvalidBalanceAmountException.class)
    public void testDepositInvalid() throws InvalidBalanceAmountException {
        balanceService.deposit(USER1_ID, -200);
    }

    @Test(expected = InvalidBalanceAmountException.class)
    public void testUseInvalid() throws CommerceException {
        balanceService.use(USER1_ID, -200);
    }

}
