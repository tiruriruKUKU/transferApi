package skibinski.michal.revolut.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class Account {

  private final BigDecimal balance;
  private final Iban iban;
  private final List<Transfer> history;

  public Account(Iban iban, BigDecimal balance, List<Transfer> history) {
    this.balance = balance.setScale(2, BigDecimal.ROUND_DOWN);
    this.iban = iban;
    this.history = new ArrayList<>(history);
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public Iban getIban() {
    return iban;
  }

  public List<Transfer> getHistory() {
    return history;
  }

}
