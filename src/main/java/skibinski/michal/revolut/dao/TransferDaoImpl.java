package skibinski.michal.revolut.dao;

import skibinski.michal.revolut.account.Account;
import skibinski.michal.revolut.account.Iban;
import skibinski.michal.revolut.account.Transfer;

public class TransferDaoImpl implements TransferDao {

  @Override
  public Account getAccount(Iban iban) throws AccountNotFoundException {
    throw new RuntimeException("Not implemented yet");
  }

  @Override
  public void sendTransfer(Transfer trnasfer)
      throws AccountNotFoundException, InsufficientFundsException {
    throw new RuntimeException("Not implemented yet");
  }
}
