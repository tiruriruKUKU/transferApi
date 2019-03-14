# transferApi

This is demo implementation of a transfer api. You can use it to transfer money between accounts. 
You can not transfer money from or to an external account. Both source and destination accounts must
be within the system.

## Quick Start

To start server on port 8080 use `gradlew run` command.

You can play with the api using web UI that can be found on `http://localhost:8080/index.html`

Data store is populated with four test accounts:
  * AL35202111090000000001234567
  * AD1400080001001234567890
  * BY86AKBB10100000002966000000
  * HR1723600001101234565
  
## API
To avoid problems with floating point arithmetic, amount of money and account balance is always
passed as a string. Precision is up to two numbers after a decimal point. All numbers with a higher
precision will be rounded down.

### `/api/accounts/{iban}`
Method: `GET`

Returns account details with history of all transfers. Transfer history can be arbitrarily long.
To present history of transfers real application should use a paging mechanism.

In real application a every entry in transactions history should contain timestamps.
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
    },
    {
      "source": "AL35202111090000000001234567",
      "destination": "HR1723600001101234565",
      "amount": "35"
    }
  ]
}
```
     
    
## Http server

## Datastore