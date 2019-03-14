package skibinski.michal.revolut;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkMethods;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.RqRegex;
import org.takes.facets.fork.TkFork;
import org.takes.rq.RqPrint;
import org.takes.rs.RsJson;
import org.takes.rs.RsText;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;
import org.takes.tk.TkClasspath;
import org.takes.tk.TkSlf4j;
import org.takes.tk.TkWithType;
import skibinski.michal.revolut.model.Account;
import skibinski.michal.revolut.model.Iban;
import skibinski.michal.revolut.model.IbanFormatException;
import skibinski.michal.revolut.model.Transfer;
import skibinski.michal.revolut.dao.AccountNotFoundException;
import skibinski.michal.revolut.dao.InsufficientFundsException;
import skibinski.michal.revolut.dao.TransferDao;

class TransferApplication {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final TransferDao dao;
  private final Take take;

  TransferApplication(TransferDao dao) {
    this.dao = dao;
    this.take = new TkSlf4j(
        new TkFork(
            new FkMethods("GET",
                new TkFork(
                    new FkRegex(
                        "/api/accounts/(?<iban>[A-Z0-9]+)",
                        this::processGetAccountRequest
                    ),
                    new FkRegex(
                        "/.+html",
                        new TkWithType(new TkClasspath("/static"), "text/html")
                    )
                )
            ),
            new FkMethods("POST",
                new TkFork(
                    new FkRegex(
                        "/api/transfer",
                        this::processPostTransferRequest
                    )
                )
            )
        )
    );
  }

  Take getTake() {
    return take;
  }

  private Response processGetAccountRequest(RqRegex request) {
    try {
      Iban iban = new Iban(request.matcher().group("iban"));
      Account account = dao.getAccount(iban);
      String json = this.objectMapper.writeValueAsString(account);
      return new RsJson(new RsWithBody(json));
    } catch (IbanFormatException e) {
      return Error.INVALID_IBAN.getResponse();
    } catch (AccountNotFoundException e) {
      return Error.ACCOUNT_NOT_FOUND.getResponse();
    } catch (JsonProcessingException e) {
      return Error.INTERNAL_ERROR.getResponse();
    }
  }

  private Response processPostTransferRequest(RqRegex request) {
    try {
      String body = new RqPrint(request).printBody();
      Transfer transfer = objectMapper.readValue(body, Transfer.class);
      dao.sendTransfer(transfer);
      return new RsWithStatus(200);
    } catch (InsufficientFundsException e) {
      return Error.INSUFFICIENT_FUNDS.getResponse();
    } catch (AccountNotFoundException e) {
      return Error.ACCOUNT_NOT_FOUND.getResponse();
    } catch (JsonParseException | JsonMappingException e) {
      return Error.INCORRECT_JSON.getResponse();
    } catch (IOException e) {
      return Error.INTERNAL_ERROR.getResponse();
    }
  }

  private enum Error {
    INVALID_IBAN("invalid_iban", 400),
    ACCOUNT_NOT_FOUND("account_not_found", 404),
    INSUFFICIENT_FUNDS("insufficient_founds", 403),
    INCORRECT_JSON("incorrect_json", 400),
    INTERNAL_ERROR("internal_server_error", 500);

    private final Response response;

    Error(String text, int code) {
      this.response = new RsWithStatus(new RsText(text), code);
    }

    public Response getResponse() {
      return response;
    }
  }
}
