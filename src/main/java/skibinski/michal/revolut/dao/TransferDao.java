package skibinski.michal.revolut.dao;

import java.util.Optional;
import skibinski.michal.revolut.account.Account;
import skibinski.michal.revolut.account.Iban;

public interface TransferDao {

  Optional<Account> findAccount(Iban iban);
}
