package skibinski.michal.revolut.model;

public class IbanFormatException extends IllegalArgumentException {

  IbanFormatException(String iban) {
    super(iban + " is not a valid iban.");
  }
}
