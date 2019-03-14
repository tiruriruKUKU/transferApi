package skibinski.michal.revolut.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Account {

  private final BigDecimal balance;
  private final Iban iban;
  private final List<Transfer> history;

  @JsonCreator
  public Account(
      @JsonProperty("iban") Iban iban,
      @JsonProperty("balance") BigDecimal balance,
      @JsonProperty("history") List<Transfer> history
  ) {
    this.balance = balance.setScale(2, BigDecimal.ROUND_DOWN);
    this.iban = iban;
    this.history = new ArrayList<>(history);
  }

  @JsonIgnore
  public BigDecimal getBalance() {
    return balance;
  }

  @JsonProperty("balance")
  public String getBalanceAsString() {
    return balance.toString();
  }

  public Iban getIban() {
    return iban;
  }

  public List<Transfer> getHistory() {
    return history;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Account account = (Account) o;
    return Objects.equals(balance, account.balance) &&
        Objects.equals(iban, account.iban) &&
        Objects.equals(history, account.history);
  }

  @Override
  public int hashCode() {
    return Objects.hash(balance, iban, history);
  }
}
