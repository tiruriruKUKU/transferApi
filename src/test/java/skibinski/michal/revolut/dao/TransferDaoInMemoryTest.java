package skibinski.michal.revolut.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import skibinski.michal.revolut.model.Account;
import skibinski.michal.revolut.model.Iban;
import skibinski.michal.revolut.model.Transfer;

@RunWith(Enclosed.class)
public class TransferDaoInMemoryTest {

  private static final Iban srcIban = new Iban("AL35202111090000000001234567");
  private static final Iban dstIban = new Iban("AD1400080001001234567890");
  private static final Iban notPresentIban = new Iban("AT483200000012345864");
  private static final BigDecimal initialBalance = new BigDecimal("100.10");

  @RunWith(Parameterized.class)
  public static class ShouldTransferMoney {

    @Parameters
    public static Collection<Object[]> data() {
      return Arrays.asList(new Object[][]{
          {
              new BigDecimal("10.10"),
              new BigDecimal("90.00"),
              new BigDecimal("110.20")
          },
          {
              new BigDecimal("100.10"),
              new BigDecimal("0.00"),
              new BigDecimal("200.20")
          },
          {
              new BigDecimal("0.01"),
              new BigDecimal("100.09"),
              new BigDecimal("100.11")
          }
      });
    }

    private final BigDecimal transferAmount;
    private final BigDecimal expectedSrc;
    private final BigDecimal expectedDst;

    public ShouldTransferMoney(
        BigDecimal transferAmount,
        BigDecimal expectedSrc,
        BigDecimal expectedDst) {
      this.transferAmount = transferAmount;
      this.expectedSrc = expectedSrc;
      this.expectedDst = expectedDst;
    }

    @Test
    public void shouldTransferMoney()
        throws AccountNotFoundException, InsufficientFundsException, SameSourceAndDestinationException {
      TransferDao dao = createTestDao();
      dao.sendTransfer(new Transfer(srcIban, dstIban, transferAmount));
      assertThat(dao.getAccount(srcIban))
          .isNotNull()
          .hasFieldOrPropertyWithValue("balance", expectedSrc);
      assertThat(dao.getAccount(dstIban))
          .isNotNull()
          .hasFieldOrPropertyWithValue("balance", expectedDst);
    }
  }

  public static class ExceptionalCases {

    @Test
    public void shouldThrowInsufficientFundsExceptionForToHighTransfer()
        throws AccountNotFoundException {
      TransferDao dao = createTestDao();

      assertThatThrownBy(() -> dao.sendTransfer(
          new Transfer(srcIban, dstIban, new BigDecimal("100.11")))
      ).isInstanceOf(InsufficientFundsException.class);

      assertThat(dao.getAccount(srcIban))
          .isNotNull()
          .hasFieldOrPropertyWithValue("balance", initialBalance);
      assertThat(dao.getAccount(dstIban))
          .isNotNull()
          .hasFieldOrPropertyWithValue("balance", initialBalance);
    }

    @Test
    public void shouldThrowThrowAccountNotFoundForNotKnownSrc()
        throws AccountNotFoundException {
      TransferDao dao = createTestDao();

      assertThatThrownBy(() -> dao.sendTransfer(
          new Transfer(notPresentIban, dstIban, new BigDecimal("10.00")))
      ).isInstanceOf(AccountNotFoundException.class);

      Account dstAccount = dao.getAccount(dstIban);
      assertThat(dstAccount)
          .isNotNull()
          .hasFieldOrPropertyWithValue("balance", initialBalance);
    }

    @Test
    public void shouldThrowThrowAccountNotFoundForNotKnownDst()
        throws AccountNotFoundException {
      TransferDao dao = createTestDao();

      assertThatThrownBy(() -> dao.sendTransfer(
          new Transfer(srcIban, notPresentIban, new BigDecimal("10.00")))
      ).isInstanceOf(AccountNotFoundException.class);

      Account srcAccount = dao.getAccount(srcIban);
      assertThat(srcAccount)
          .isNotNull()
          .hasFieldOrPropertyWithValue("balance", initialBalance);
    }
  }

  private static TransferDaoInMemory createTestDao() {
    TransferDaoInMemory dao = new TransferDaoInMemory();
    dao.putIfAbsent(srcIban, initialBalance);
    dao.putIfAbsent(dstIban, initialBalance);
    return dao;
  }
}