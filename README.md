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

## Requirements
* Docker
* Git
* Any IDE (IntelliJ recommended) or terminal (Linux/Mac, [Git Bash](https://git-scm.com/downloads) for Windows)

## Getting Started
via terminal

`git clone https://github.com/Rufus100Procent/bolagsverket-pokemon-backend.git`

`cd bolagsverket-pokemon-backend`

`./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`

### Build Deployable Image
```
docker build -t bolagsverket-pokemon-backend:0.1.0 .
```

