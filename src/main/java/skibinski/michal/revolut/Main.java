package skibinski.michal.revolut;

import java.math.BigDecimal;
import skibinski.michal.revolut.dao.TransferDaoInMemory;

import java.io.IOException;
import skibinski.michal.revolut.model.Iban;

public class Main {

  public static void main(String[] args) throws IOException {
    TransferDaoInMemory dao = new TransferDaoInMemory();

    dao.putIfAbsent(new Iban("AL35202111090000000001234567"), new BigDecimal("242.32"));
    dao.putIfAbsent(new Iban("AD1400080001001234567890"), new BigDecimal("24322.36"));
    dao.putIfAbsent(new Iban("BY86AKBB10100000002966000000"), new BigDecimal("2424243322.36"));
    dao.putIfAbsent(new Iban("HR1723600001101234565"), new BigDecimal("3322.36"));

    TransferApplication app = new TransferApplication(dao);
    new HttpServer(app).start(args);
  }
}
