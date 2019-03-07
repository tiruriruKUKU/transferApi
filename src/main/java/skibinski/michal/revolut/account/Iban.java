package skibinski.michal.revolut.account;

import org.apache.commons.validator.routines.IBANValidator;

public final class Iban {

  private final String iban;

  public Iban(String iban) throws IbanFormatException {
    if (!IBANValidator.getInstance().isValid(iban)) {
      throw new IbanFormatException(iban);
    }
    this.iban = iban;
  }

  public String getIban() {
    return iban;
  }
}
