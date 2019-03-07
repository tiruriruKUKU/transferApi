package skibinski.michal.revolut.account;

public class IbanFormatException extends IllegalArgumentException {

  IbanFormatException(String iban) {
    super(iban + " is not a valid iban.");
  }
}
