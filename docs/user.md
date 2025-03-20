# User API Spec

## Register User

Endpoint : POST /api/users

Request Body:

```json
{
  "username": "user",
  "password": "userpswd",
  "name": "user contact"
}
```
Response Body (success):

```json
{
  "data": "ok"
}
```

Response Body (failed):

```json
{
  "errors": "username must not blank, ??"
}
```
## Login User

Endpoint : POST /api/auth/login

Request Body:

```json
{
  "username": "user",
  "password": "userpswd"
}
```
Response Body (success):

```json
{
  "data": {
    "token": "TOKEN",
    "expiredAt": 7543571 //milisecond
  }
}
```

Response Body (failed, 401):

```json
{
  "errors": "username or password wrong"
}
```

## Get User

Endpoint : GET /api/users/current

Request Header:
- X-API-TOKEN : Token (Mandatory)

Response Body (success):

```json
{
  "data": {
    "username": "ahmad",
    "name": "ahmad muntaha"
  }
}
```

Response Body (failed, 401):

```json
{
  "errors": "Unauthorized"
}
```
## Update User
Endpoint : PATCH /api/users/current

Request Header:
- X-API-TOKEN : Token (Mandatory)

Request Body:

```json
{
  "name": "ahmad",
  "password": "newPassword"
}
```

Response Body (success):

```json
{
  "data": {
    "username": "ahmad",
    "name": "ahmad muntaha"
  }
}
```
Response Body (failed, 401):

```json
{
  "errors": "Unauthorized"
}
```


## Logout User
Endpoint : DELETE /api/auth/logout

Request Header:
- X-API-TOKEN : Token (Mandatory)

Response Body (success):
```json
{
  "data":"success"
}
```