package skibinski.michal.revolut.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;
import skibinski.michal.revolut.account.Account;
import skibinski.michal.revolut.account.Iban;
import skibinski.michal.revolut.account.Transfer;
import skibinski.michal.revolut.dao.TransferDao;

public class HttpApplicationTest {

  private final Iban accountIban = new Iban("BY86AKBB10100000002966000000");
  private final TransferDao mockDao = new TransferDao() {
    @Override
    public Optional<Account> findAccount(Iban iban) {
      if (!accountIban.equals(iban)) {
        return Optional.empty();
      }
      Iban secondIban = new Iban("AL35202111090000000001234567");
      return Optional.of(new Account(
          iban,
          new BigDecimal("2.11"),
          Arrays.asList(
              new Transfer(iban, secondIban, new BigDecimal("2.2")),
              new Transfer(secondIban, iban, new BigDecimal("22.2"))
          )
      ));
    }
  };
  private final Take take = new HttpApplication(mockDao).getTake();
  private final ObjectMapper objectMapper = new ObjectMapper();


  @Test
  public void shouldRetrieveAccount() throws IOException {
    Account expected = mockDao.findAccount(accountIban).get();
    String response = new RsPrint(
        take.act(new RqFake("GET", "/api/accounts/" + accountIban.toString()))
    ).printBody();
    Assertions.assertThat(objectMapper.readValue(response, Account.class)).isEqualTo(expected);
  }

  @Test
  public void shouldReturn400ForInvalidIban() throws IOException {
    String head = new RsPrint(
        take.act(new RqFake("GET", "/api/accounts/4234fd"))
    ).printHead();
    Assertions.assertThat(head).contains("400");
  }

  @Test
  public void shouldReturn404ForNotFoundAccount() throws IOException {
    String head = new RsPrint(
        take.act(new RqFake("GET", "/api/accounts/HR1723600001101234565")) //valid iban
    ).printHead();
    Assertions.assertThat(head).contains("404");
  }
}