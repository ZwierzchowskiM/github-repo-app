# RepoApp


The GitHubServiceImpl class provides functionality to interact with the GitHub API to fetch repository details for a given user.

- Spring Boot: This service is built using the Spring Boot framework.
- WebClient: Used for making HTTP requests to the GitHub API.

## Configuration 

Configuration
GitHub API URLs: The URLs for fetching repositories and branches are configurable through application.properties (github.repos.api.url and github.branches.api.url).
## Endpoints
```
GET /repositories?username={username}
```
Fetching Detailed Repository Information for a User
This endpoint allows fetching detailed information about a user's repositories on GitHub. Only non-fork repositories are returned. For each repository, information about the name, owner, and a list of branches is provided.

### Parameters:
- `username` (String, required) - The username on GitHub.

### Example Usage:
GET http://localhost:8080/api/v1/repositories/?username=zwierzchowskim

### Response:
```JSON
    {
  "repositoryName": "photo-album-app",
  "ownerLogin": "ZwierzchowskiM",
  "branches": [
    {
      "name": "feature-add-mysql-db",
      "lastCommitSHA": "e5d33b68080739af9d17f8579accba0fa03ad9da"
    },
    {
      "name": "main",
      "lastCommitSHA": "88de373afb6451095c828bd63bf4b7716e54660e"
    }
  ]
}
```


## Swagger

Swagger UI is available at `http://localhost:8080/swagger-ui/index.html#/`

## How to run

To get this project up and running, navigate to root directory of an application and execute following commands:

* Create a jar file.
```
$ mvn package
```

* Then build docker image using already built jar file.

```
$ docker build -t githubrepoapp .
```

* Run whole setup

```
$ docker run -p 8080:8080 githubrepoapp
```



