package skibinski.michal.revolut.dao;

import skibinski.michal.revolut.model.Account;
import skibinski.michal.revolut.model.Iban;
import skibinski.michal.revolut.model.Transfer;

public interface TransferDao {

  Account getAccount(Iban iban) throws AccountNotFoundException;

  void sendTransfer(Transfer transfer) throws AccountNotFoundException, InsufficientFundsException;
}
