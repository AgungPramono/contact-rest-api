# Contact API Spec

## Create Contact

Endpoint: POST /api/contacts

Request Header:
- X-API-TOKEN : Token (Mandatory)

Request Body:

```json
{
  "firstName": "Eko",
  "lastName": "wardoyo",
  "email": "eko@mail.com",
  "phone": "0813696192"
}
```
Response Body (success):

```json
{
  "data": {
    "id": "random-string",
    "firstName": "Eko",
    "lastName": "wardoyo",
    "email": "eko@mail.com",
    "phone": "0813696192"
  }
}
```

Response Body (failed):

```json
{
  "errors": "email format invalid,phone format invalid,....."
}
```
## Update Contact
Endpoint: PUT /api/contacts/{idContact}

Request Header:
- X-API-TOKEN : Token (Mandatory)

Request Body:

```json
{
  "firstName": "Eko",
  "lastName": "wardoyo",
  "email": "eko@mail.com",
  "phone": "0813696192"
}
```
Response Body (success):

```json
{
  "data": {
    "id": "random-string",
    "firstName": "Eko",
    "lastName": "wardoyo",
    "email": "eko@mail.com",
    "phone": "0813696192"
  }
}
```

Response Body (failed):

```json
{
  "errors": "email format invalid,phone format invalid,....."
}
```
## Get Contact
Endpoint: GET /api/contacts/{idContact}

Request Header:
- X-API-TOKEN : Token (Mandatory)

Response Body (success):

```json
{
  "data": {
    "id": "random-string",
    "firstName": "Eko",
    "lastName": "wardoyo",
    "email": "eko@mail.com",
    "phone": "0813696192"
  }
}
```

Response Body (failed,404):

```json
{
  "errors": "contact is not found"
}
```
## Search Contact
Endpoint: GET /api/contacts

Query Param:

- name : String, contact first name or last name, using like query, optional
- phone : String, contact phone, using like query,optional
- email : String, contact email, using like query,optional
- page : Integer, start from 0,default 0
- size : Integer, default 10

Request Header:
- X-API-TOKEN : Token (Mandatory)

Response Body (success):

```json
{
  "data": [
    {
      "id": "random-string",
      "firstName": "Eko",
      "lastName": "wardoyo",
      "email": "eko@mail.com",
      "phone": "0813696192"
    }
  ],
  "paging" : {
      "currentPage": 0,
      "totalPage": 10,
      "size":10
  }
}
```

Response Body (failed):

```json
{
  "errors": "Unautorized"
}
```
## Remove Contact
Endpoint: DELETE /api/contacts/{idContact}

Request Header:
- X-API-TOKEN : Token (Mandatory)

Response Body (success):

```json
{
  "data": "ok"
}
```

Response Body (failed,404):

```json
{
  "errors": "contact is not found"
}
```