package skibinski.michal.revolut.dao;

import skibinski.michal.revolut.account.Account;
import skibinski.michal.revolut.account.Iban;
import skibinski.michal.revolut.account.Transfer;

public interface TransferDao {

  Account getAccount(Iban iban) throws AccountNotFoundException;

  void sendTransfer(Transfer transfer) throws AccountNotFoundException, InsufficientFundsException;
}
