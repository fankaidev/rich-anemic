package net.fklj.richanemic.adm;

import net.fklj.richanemic.adm.service.balance.BalanceServiceImpl;
import net.fklj.richanemic.adm.service.balance.BalanceTxService;
import net.fklj.richanemic.data.CommerceException;
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

}
