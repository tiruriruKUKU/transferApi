package skibinski.michal.revolut.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;
import org.apache.commons.validator.routines.IBANValidator;

public final class Iban {

  @JsonValue
  private final String iban;

  @JsonCreator
  public Iban(String iban) throws IbanFormatException {
    if (!IBANValidator.getInstance().isValid(iban)) {
      throw new IbanFormatException(iban);
    }
    this.iban = iban;
  }

  @Override
  public String toString() {
    return iban;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Iban iban1 = (Iban) o;
    return Objects.equals(iban, iban1.iban);
  }

  @Override
  public int hashCode() {
    return Objects.hash(iban);
  }
}
