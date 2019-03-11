package skibinski.michal.revolut;

import org.takes.http.Exit;
import org.takes.http.FtCli;

import java.io.IOException;

class HttpServer {

  private final TransferApplication app;

  HttpServer(TransferApplication app) {
    this.app = app;
  }

  void start(String[] args) throws IOException {
    new FtCli(app.getTake(), args
    ).start(Exit.NEVER);
  }

}
