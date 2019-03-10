package skibinski.michal.revolut.dao;

import java.util.Optional;
import skibinski.michal.revolut.account.Account;
import skibinski.michal.revolut.account.Iban;

public class TransferDaoImpl implements TransferDao {

  @Override
  public Optional<Account> findAccount(Iban iban) {
    throw new RuntimeException("Not implemented yet");
  }
}
