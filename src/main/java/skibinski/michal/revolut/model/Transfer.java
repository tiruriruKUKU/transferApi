package skibinski.michal.revolut.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Objects;

public final class Transfer {

  private final Iban source;
  private final Iban destination;
  private final BigDecimal amount;

  @JsonCreator
  public Transfer(
      @JsonProperty("source") Iban source,
      @JsonProperty("destination") Iban destination,
      @JsonProperty("amount") BigDecimal amount) {
    BigDecimal _amount = amount.setScale(2, BigDecimal.ROUND_DOWN);
    if(_amount.compareTo(new BigDecimal(0)) < 0) {
      throw new IllegalArgumentException("Amount cannot be negative. Amount: " + _amount);
    }
    this.source = source;
    this.destination = destination;
    this.amount = _amount;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Transfer transfer = (Transfer) o;
    return Objects.equals(source, transfer.source) &&
        Objects.equals(destination, transfer.destination) &&
        Objects.equals(amount, transfer.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(source, destination, amount);
  }
}
