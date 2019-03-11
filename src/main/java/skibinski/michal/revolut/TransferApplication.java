package skibinski.michal.revolut;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkMethods;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.RqRegex;
import org.takes.facets.fork.TkFork;
import org.takes.rs.RsJson;
import org.takes.rs.RsText;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;
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
    this.take = new TkFork(
        new FkMethods("GET",
            new TkFork(
                new FkRegex(
                    "/api/accounts/(?<iban>[A-Z0-9]+)",
                    this::processGetAccountRequest
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
      return new RsWithStatus(500);
    }
  }

  private Response processPostTransferRequest(RqRegex request) {
    try {
      Transfer transfer = objectMapper
          .readValue(
              new InputStreamReader(
                  request.body(),
                  StandardCharsets.UTF_8
              ),
              Transfer.class
          );
      dao.sendTransfer(transfer);
      return new RsWithStatus(200);
    } catch (InsufficientFundsException e) {
      return Error.INSUFFICIENT_FUNDS.getResponse();
    } catch (AccountNotFoundException e) {
      return Error.ACCOUNT_NOT_FOUND.getResponse();
    } catch (JsonParseException | JsonMappingException e) {
      return new RsWithStatus(400);
    } catch (IOException e) {
      return new RsWithStatus(500);
    }
  }

  private enum Error {
    INVALID_IBAN("invalid_iban", 400),
    ACCOUNT_NOT_FOUND("account_not_found", 404),
    INSUFFICIENT_FUNDS("insufficient_founds", 403);

    private final Response response;

    Error(String text, int code) {
      this.response = new RsWithStatus(new RsText(text), code);
    }

    public Response getResponse() {
      return response;
    }
  }
}
