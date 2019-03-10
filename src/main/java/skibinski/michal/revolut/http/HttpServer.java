package skibinski.michal.revolut.http;

import org.takes.http.Exit;
import org.takes.http.FtCli;

import java.io.IOException;
import skibinski.michal.revolut.dao.TransferDaoImpl;

public class HttpServer {

  private HttpApplication app = new HttpApplication(new TransferDaoImpl());

  public void start(String[] args) throws IOException {
    new FtCli(app.getTake(), args
    ).start(Exit.NEVER);
  }


}
