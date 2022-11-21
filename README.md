# KAFKA GATEWAY

## Example DTO
* Add operation
``` 
{
  "Request": {
    "messageId": "1",
    "operationType": "ADD",
    "senderSystemCode": "SBER",
    "Client": {
      "ClientType": {
        "codeName": "INDIVIDUAL",
        "name": "Физическое лицо"
      },
      "clientFullName": "Petrov Petr Petrovich",
      "account": "4276 1231 1241 1241",
      "inn": "12345678910"
    }
  }
}
```
* Delete operation
``` 
{
  "Request": {
    "messageId": "1",
    "operationType": "Delete",
    "senderSystemCode": "SBER",
    "Client": {
      "ClientType": {
        "codeName": "INDIVIDUAL",
        "name": "Физическое лицо"
      },
      "clientFullName": "Petrov Petr Petrovich",
      "account": "4276 1231 1241 1241",
      "inn": "12345678910"
    }
  }
}
```
* Verify operation
``` 
{
  "Request": {
    "messageId": "1",
    "operationType": "Verify",
    "senderSystemCode": "SBER",
    "Client": {
      "ClientType": {
        "codeName": "INDIVIDUAL",
        "name": "Физическое лицо"
      },
      "clientFullName": "Petrov Petr Petrovich",
      "account": "4276 1231 1241 1241",
      "inn": "12345678910"
    }
  }
}
```

## DB H2
- connection address can be obtained at startup in the console