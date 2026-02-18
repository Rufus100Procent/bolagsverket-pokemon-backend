# Bologsverket Pokemon Backend

## About
This project is a coding challenge, in other words "ArbetsProv" for Bolagsverket.

Built in using Java, Maven, Spring framwork and local PostgreSQL

## Original Requirements

The following user stories were required for this assignment

**User Story 1** : On first startup, the application automatically imports all Pokémon from the
external PokeAPI and stores them in the internal database with relevant metadata.

**User Story 2** : A REST endpoint that lists all Pokémon from the database with support for:
- Pagination
- Filtering by type (e.g. `fire`, `water`)
- Sorting by name or height (`asc` / `desc`)

**User Story 3** : Full CRUD operations via separate REST endpoints. All write endpoints
(create, update, delete) are protected with authentication.

**User Story 4** : Personal favorite marking per user. A Pokémon can be marked or unmarked
as a favorite, and favorites are tied to the authenticated.

#### additional info
* PokeAPI: https://pokeapi.co/docs/v2
* In memoryDB (optional)

---

# Current state of the App
* Under development
---

## Rate Limiting

The application uses in-memory IP-based rate limiting. Each IP address is allowed a maximum of
25 requests per 60 seconds. If you exceed this limit you will receive a `429 Too Many Requests`
response. The counter resets back to zero every 60 seconds.

---
### Validation

Incoming JSON payloads are validated using Spring's  validation.
invalid requests are rejected early with a clear error message before touching the database

---
## Spring Oauth2 Resource Server

The API is secured using Spring Security's OAuth2 Resource Server. By adding a single
dependency and minimal configuration, Spring automatically protects every endpoint and future

Behind the scenes, on every incoming request Spring intercepts it before it reaches the
controller, extracts the `Authorization: Bearer <token>` header, decodes and validates the
JWT signature using the secret key, and rejects the request with [401 Unauthorized](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/401)
if the token is missing, expired, or tampered with.


---
## Authentication

The application uses simple username and password authentication and is completely stateless,
no session is stored on the server. Every request must carry its own JWT token to prove identity.

Passwords are never stored in plain text, Spring automatically hashes them using
[BCrypt](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html)
before saving to the database. BCrypt is a one-way hashing algorithm, meaning the original
password can never be recovered from the hash. When you log in, Spring hashes the incoming
password and compares it against the stored hash to verify your identity.

Once authenticated, the application returns a signed JWT token. This token must be included in the
`Authorization` header on all protected requests:
```
Authorization: Bearer <your-token>
```

## Requirements
* Docker
* Git
* Any IDE (IntelliJ recommended) or terminal (Linux/Mac, [Git Bash](https://git-scm.com/downloads) for Windows)

## Getting Started
via terminal

`git clone https://github.com/Rufus100Procent/bolagsverket-pokemon-backend.git`

`cd bolagsverket-pokemon-backend`

`docker compose up -d --build`

`./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`


### Getting started with tests
* `mvn test`

* `mvn integration-test` or `mvn verify` to run integration testing

#### Swagger UI

Swagger UI is enabled for the dev profile only.
```
http://localhost:8092/swagger-ui/index.html
```
To test protected endpoints, first call the register and login API to get a JWT token, then click the
**Authorize** button at the top right of the Swagger UI page and paste the token. All
subsequent requests will include it automatically.

APIs can also be tested in Postman or terminal using curl command.

## API Overview

The following endpoints are public and require no token:
* `GET /api/v0/pokemon`
* `/api/v0/auth/**`
* `GET /v3/api-docs/**`
* `GET /swagger-ui.html`
* `GET /actuator/**`

All other endpoints require a valid JWT token in the `Authorization` header.

#### Register
```
curl -X POST http://localhost:8092/api/v0/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password1"}'
```
Response:
```
"Successfully created user: <uuid>"
```

#### Login
```
curl -X POST http://localhost:8092/api/v0/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password1"}'
```
Response:
```
{ "accessToken": "eyJhbGciOi..." }
```

---
#### List all pokemon Names

Supports the following query parameters:
* `type`: filter by type, for example `fire`, `water`, `grass`
* `sort`: sort by `name` or `id`, defaults to `name`
* `order`: `asc` sorts A to Z, `desc` sorts Z to A, defaults to `asc`
* `page`: page number, defaults to `0`
* `size`: results per page, defaults to `20`
* 
```
curl -i -X GET "http://localhost:8092/api/v0/pokemon?type=water&page=0&size=10&sort=name&order=desc"
```
Response:
```
{
    "content": [
        { "id": 460, "name": "abomasnow" }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1352,
    "totalPages": 68,
    "last": false
}
```

#### Get Pokemon by ID
```
curl -i -X GET http://localhost:8092/api/v0/pokemon/1 -H "Authorization: Bearer YOUR_TOKEN_HERE"
```
Response:

```json
{
    "abilities": [
        "chlorophyll",
        "overgrow"
    ],
    "baseExperience": 64,
    "height": 7,
    "id": 1,
    "name": "bulbasaur",
    "types": [
        "grass",
        "poison"
    ],
    "weight": 69
}
```
#### Update Pokemon
```
curl -i -X PUT http://localhost:8092/api/v0/pokemon/460 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
  "name": "bulbasaur",
  "height": 10,
  "weight": 70,
  "baseExperience": 100,
  "typeIds": [1, 3],
  "abilityIds": [2],
  "favorite": true
}'
```
if you get error "Pokemon 'Name' is already in favorites" remove favorites from json payload

#### Create Pokemon
```
curl -i -X PUT http://localhost:8092/api/v0/pokemon/460 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
  "name": "newNmae",
  "height": 7,
  "weight": 69,
  "baseExperience": 64,
  "typeIds": [1, 2],
  "abilityIds": [1],
  "favorite": true
}'
```
#### Toggle Pokemon favorite
Favorites are tied to the authenticated user's JWT token. Once a pokemon is set as a favorite, 
only the user who set it can see it — every user has their own independent favorites

```
curl -i -X PUT http://localhost:8092/api/v0/pokemon/2/favorite?value=true \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

```

#### Delete Pokemon
```
curl -i -X DELETE http://localhost:8092/api/v0/pokemon/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### Build Deployable Image
```
docker build -t bolagsverket-pokemon-backend:0.1.0 .
```

