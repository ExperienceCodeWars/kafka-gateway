# KAFKA GATEWAY

## Example DTO
* Add operation
``` 
{
  "Request": {
    "messageId": "1",
    "operationType": "ADD",
    "senderSystemCode": "SBER",
    "client": {
      "clientType": {
        "codeName": "INDIVIDUAL",
        "name": "Физическое лицо"
      },
      "clientFullName": "Petrov Petr Petrovich",
      "accountNumber": "4276 1231 1241 1241",
      "inn": "12345678910",
	    "activeStatus" : "active" 
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
    "client": {
      "clientType": {
        "codeName": "INDIVIDUAL",
        "name": "Физическое лицо"
      },
      "clientFullName": "Petrov Petr Petrovich",
      "accountNumber": "4276 1231 1241 1241",
      "inn": "12345678910",
	    "activeStatus" : "active"
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
    "client": {
      "clientType": {
        "codeName": "INDIVIDUAL",
        "name": "Физическое лицо"
      },
      "clientFullName": "Petrov Petr Petrovich",
      "accountNumber": "4276 1231 1241 1241",
      "inn": "12345678910",
	    "activeStatus" : "active"
    }
  }
}
```

## DB H2
- connection address can be obtained at startup in the console