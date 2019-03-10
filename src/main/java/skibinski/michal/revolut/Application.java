package skibinski.michal.revolut;

import skibinski.michal.revolut.http.HttpServer;

import java.io.IOException;

public class Application {

  public static void main(String[] args) throws IOException {
    new HttpServer().start(args);
  }
}
