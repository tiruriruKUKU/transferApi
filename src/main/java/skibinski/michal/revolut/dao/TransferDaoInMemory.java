package skibinski.michal.revolut.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import skibinski.michal.revolut.model.Account;
import skibinski.michal.revolut.model.Iban;
import skibinski.michal.revolut.model.Transfer;

public class TransferDaoInMemory implements TransferDao {

  private final Map<Iban, StoredAccount> accounts = new ConcurrentHashMap<>();

  @Override
  public Account getAccount(Iban iban) throws AccountNotFoundException {
    StoredAccount account = accounts.get(iban);
    if (account == null) {
      throw new AccountNotFoundException(iban);
    }
    return account.toAccount();
  }

  @Override
  public void sendTransfer(Transfer transfer)
      throws AccountNotFoundException, InsufficientFundsException {
    StoredAccount src = accounts.get(transfer.getSource());
    StoredAccount dst = accounts.get(transfer.getDestination());
    if (src == null) {
      throw new AccountNotFoundException(transfer.getSource());
    }
    if (dst == null) {
      throw new AccountNotFoundException(transfer.getDestination());
    }
    if (!src.tryOutgoingTransfer(transfer)) {
      throw new InsufficientFundsException();
    }
    dst.incomingTransfer(transfer);
  }

  public void putIfAbsent(Iban iban, BigDecimal balance) {
    StoredAccount account = new StoredAccount(iban, balance);
    accounts.putIfAbsent(iban, account);
  }

  private final class StoredAccount {

    private final Iban iban;
    private BigDecimal amount;
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final List<Transfer> transfers = new ArrayList<>();

    private StoredAccount(Iban iban, BigDecimal amount) {
      this.iban = iban;
      this.amount = amount.setScale(2, BigDecimal.ROUND_DOWN);
    }

    boolean tryOutgoingTransfer(Transfer transfer) {
      Lock writeLock = this.lock.writeLock();
      writeLock.lock();
      try {
        if (this.amount.compareTo(transfer.getAmount()) < 0) {
          return false;
        } else {
          this.amount = this.amount.subtract(transfer.getAmount());
          transfers.add(transfer);
          return true;
        }
      } finally {
        writeLock.unlock();
      }
    }

    void incomingTransfer(Transfer transfer) {
      Lock writeLock = this.lock.writeLock();
      writeLock.lock();
      try {
        this.amount = this.amount.add(transfer.getAmount());
        transfers.add(transfer);
      } finally {
        writeLock.unlock();
      }
    }

    Account toAccount() {
      Lock readLock = this.lock.readLock();
      readLock.lock();
      try {
        return new Account(this.iban, this.amount, new ArrayList<>(transfers));
      } finally {
        readLock.unlock();
      }
    }
  }
}
