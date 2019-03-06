package skibinski.michal.revolut;

import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.Exit;
import org.takes.http.FtCli;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException {
        new FtCli(
                new TkFork(new FkRegex("/", "hello, world!")), args
        ).start(Exit.NEVER);
    }
}
