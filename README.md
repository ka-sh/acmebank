## Acmebank demo project

### Summery

Simple web demo project implemented with the following technologies:

- Kotlin
- Spring Boot V3.1
- Spring Data JPA
- Spring web

### How to run

### Endpoints

| Method | Endpoint                                 | Description                                 |
|--------|------------------------------------------|---------------------------------------------|
| GET    | /api/v1/accounts/{accountNumber}/balance | Get account Balance                         |
| POST   | /api/v1/accounts/transfer                | Transfer From source to Destination account |

### Get Balance Operation

```http request
GET /api/v1/accounts/{accountNumber}/balance
```

##### Request Parameters

| Parameter     | Type   | Description                       | Restrictions                             |
|---------------|--------|-----------------------------------|------------------------------------------|
| accountNumber | String | Account number to get balance for | Must be valid account number of 8 digits |

##### Response

```json
{
  "accountNumber": "12345678",
  "balance": 999998.01,
  "currency": "HKD"
}
```

### Transfer Operation

```http request
POST /api/v1/accounts/transfer
```

##### Request Body

| Parameter | Type   | Description           | Restrictions                                         |
|-----------|--------|-----------------------|------------------------------------------------------|
| from      | String | Source account number | Must be valid account number of 8 digits             |
| to        | String | Destination account   | Must be valid account number of 8 digits             |
| amount    | Double | Amount to transfer    | Must be positive number with max of 2 decimal points |
| currency  | String | Currency              | Must be valid currency code (Only HKD is supported)  |

##### Example Request Body
```json
{
  "sourceAccountNumber": "12345678",
  "destinationAccountNumber": "87654321",
  "amount": 1.01,
  "currency": "HKD"
}
```

#### Response
| Parameter | Type   | Description                       |
|-----------|--------|-----------------------------------|
| status    | String | Operation status                  |
| message   | String | status message or failure message |

```json
{
    "status": "Success",
    "message": "Transfer completed"
}
```

