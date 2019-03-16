# transferApi

This is a demo implementation of a transfer api. You can use it to transfer money between accounts. 
You can not transfer money from or to an external account. Both source and destination accounts must
be within the system.

## Quick Start

To start server on port 8080 use `gradlew run` command.

You can play with the api using web UI that can be found on `http://localhost:8080/index.html`

Datastore is populated with four test accounts:
  * AL35202111090000000001234567
  * AD1400080001001234567890
  * BY86AKBB10100000002966000000
  * HR1723600001101234565
  
## API
To avoid problems with floating point arithmetic, amount of money and account balance is always
passed as a string. Precision is up to two digits after a decimal point. Input with a higher
precision will be rounded down.

### `/api/accounts/{iban}`
Method: `GET`

Returns account details with history of all transfers. Transfer history can be arbitrarily long.
To present history of transfers a real application should use pagination.

In real application every entry in transactions history should contain a timestamp.
In this simplified implementation timestamps ar omitted.

Exemplary output:

```json
{
  "iban": "AL35202111090000000001234567",
  "balance": "239.32",
  "history": [
    {
      "source": "AL35202111090000000001234567",
      "destination": "AD1400080001001234567890",
      "amount": "3.1"
    },
    {
      "source": "AD1400080001001234567890",
      "destination": "AL35202111090000000001234567",
      "amount": "35"
    }
  ]
}
```
 
### `/api/transfer`
Method: `POST`

Submits transfer to the system.

Exemplary body of a request: 

```json
{
  "source": "AL35202111090000000001234567",
  "destination": "AD1400080001001234567890",
  "amount": "80"
}
```
    
## Http server
This application uses `takes` http server. Look at https://github.com/yegor256/takes for quick info.

When you run `skibinski.michal.revolut.Main` all command line arguments are passed to
`org.takes.http.FtCli`. Thanks to it you can configure behaviour of http server from command
line. Following options are available: 
```
--port=1234         Tells the server to listen to TCP port 1234
--lifetime=5000     The server will die in five seconds (useful for integration testing)
--hit-refresh       Run the server in hit-refresh mode
--daemon            Runs the server in Java daemon thread (for integration testing)
--threads=30        Processes incoming HTTP requests in 30 parallel threads
--max-latency=5000  Maximum latency in milliseconds per each request
                    (longer requests will be interrupted)
```


## Datastore
In memory datastore is build with JDK classes. It can be used concurrently by multiple threads.
Datastore with similar properties can be build with relational database and `select for update`
statement.
