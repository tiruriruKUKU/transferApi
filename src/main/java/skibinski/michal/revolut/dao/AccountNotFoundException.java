package skibinski.michal.revolut.dao;

import skibinski.michal.revolut.account.Iban;

public class AccountNotFoundException extends Exception {
  public AccountNotFoundException(Iban iban) {
    super("Account not found: " + iban.toString());
  }
}
