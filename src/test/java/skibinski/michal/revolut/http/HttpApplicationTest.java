package skibinski.michal.revolut.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;
import skibinski.michal.revolut.account.Account;
import skibinski.michal.revolut.account.Iban;
import skibinski.michal.revolut.account.Transfer;
import skibinski.michal.revolut.dao.AccountNotFoundException;
import skibinski.michal.revolut.dao.InsufficientFundsException;
import skibinski.michal.revolut.dao.TransferDao;

public class HttpApplicationTest {

  private final Iban accountIban = new Iban("BY86AKBB10100000002966000000");

  private final TransferDao mockDao = new TransferDao() {
    @Override
    public Account getAccount(Iban iban) throws AccountNotFoundException {
      if (!accountIban.equals(iban)) {
        throw new AccountNotFoundException(iban);
      }
      Iban secondIban = new Iban("AL35202111090000000001234567");
      return new Account(
          iban,
          new BigDecimal("2.11"),
          Arrays.asList(
              new Transfer(iban, secondIban, new BigDecimal("2.2")),
              new Transfer(secondIban, iban, new BigDecimal("22.2"))
          )
      );
    }

    @Override
    public void sendTransfer(Transfer transfer)
        throws AccountNotFoundException, InsufficientFundsException {
      if (!accountIban.equals(transfer.getSource())) {
        throw new AccountNotFoundException(transfer.getSource());
      }
      if (new BigDecimal(100).compareTo(transfer.getAmount()) < 0) {
        throw new InsufficientFundsException();
      }
    }
  };
  private final Take take = new HttpApplication(mockDao).getTake();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void shouldRetrieveAccount() throws IOException, AccountNotFoundException {
    Account expected = mockDao.getAccount(accountIban);
    String response = new RsPrint(
        take.act(new RqFake("GET", "/api/accounts/" + accountIban.toString()))
    ).printBody();
    Assertions.assertThat(objectMapper.readValue(response, Account.class)).isEqualTo(expected);
  }

  @Test
  public void getAccountShouldReturn400ForInvalidIban() throws IOException {
    String head = new RsPrint(
        take.act(new RqFake("GET", "/api/accounts/4234fd"))
    ).printHead();
    Assertions.assertThat(head).contains("400");
  }

  @Test
  public void getAccountShouldReturn404ForNotFoundAccount() throws IOException {
    String head = new RsPrint(
        take.act(new RqFake("GET", "/api/accounts/HR1723600001101234565")) //valid iban
    ).printHead();
    Assertions.assertThat(head).contains("404");
  }

  @Test
  public void postTransferShouldReturn200() throws IOException {
    Transfer t = new Transfer(accountIban, accountIban, new BigDecimal("2"));
    String head = new RsPrint(
        take.act(
            new RqFake(
                "POST",
                "/api/transfer",
                objectMapper.writeValueAsString(t)
            )
        )
    ).printHead();
    Assertions.assertThat(head).contains("200");
  }

  @Test
  public void postTransferWithTooHighAmountShouldReturn203() throws IOException {
    Transfer t = new Transfer(accountIban, accountIban, new BigDecimal("200"));
    String head = new RsPrint(
        take.act(
            new RqFake(
                "POST",
                "/api/transfer",
                objectMapper.writeValueAsString(t)
            )
        )
    ).printHead();
    Assertions.assertThat(head).contains("403");
  }

  @Test
  public void postTransferWithUnknownIbanShouldReturn404() throws IOException {
    Iban iban = new Iban("HR1723600001101234565");
    Transfer t = new Transfer(iban, iban, new BigDecimal("1"));
    String head = new RsPrint(
        take.act(
            new RqFake(
                "POST",
                "/api/transfer",
                objectMapper.writeValueAsString(t)
            )
        )
    ).printHead();
    Assertions.assertThat(head).contains("404");
  }
}