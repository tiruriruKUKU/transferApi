package skibinski.michal.revolut.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkMethods;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.RqRegex;
import org.takes.facets.fork.TkFork;
import org.takes.rs.RsJson;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;
import skibinski.michal.revolut.account.Account;
import skibinski.michal.revolut.account.Iban;
import skibinski.michal.revolut.account.IbanFormatException;
import skibinski.michal.revolut.dao.TransferDao;

class HttpApplication {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final TransferDao dao;

  private final Take take = new TkFork(
      new FkMethods("GET",
          new TkFork(
              new FkRegex(
                  "/api/accounts/(?<iban>[A-Z0-9]+)",
                  this::processGetAccountRequest
              )
          )
      )
  );

  HttpApplication(TransferDao dao) {
    this.dao = dao;
  }

  Take getTake() {
    return take;
  }

  private Response processGetAccountRequest(RqRegex request) {
    try {
      Iban iban = new Iban(request.matcher().group("iban"));
      Optional<Account> account = dao.findAccount(iban);
      if(account.isPresent()) {
        String json = this.objectMapper.writeValueAsString(account.get());
        return new RsJson(new RsWithBody(json));
      } else {
        return new RsWithStatus(404);
      }
    } catch (IbanFormatException e) {
      return new RsWithStatus(400);
    } catch (JsonProcessingException e) {
      return new RsWithStatus(500);
    }
  }

}
