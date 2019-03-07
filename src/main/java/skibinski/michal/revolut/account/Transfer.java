package skibinski.michal.revolut.account;

import java.math.BigDecimal;

public final class Transfer {

  private final Iban source;
  private final Iban destination;
  private final BigDecimal amount;

  public Transfer(Iban source, Iban destination, BigDecimal amount) {
    this.source = source;
    this.destination = destination;
    this.amount = amount;
  }

  public Iban getSource() {
    return source;
  }

  public Iban getDestination() {
    return destination;
  }

  public BigDecimal getAmount() {
    return amount;
  }
}
