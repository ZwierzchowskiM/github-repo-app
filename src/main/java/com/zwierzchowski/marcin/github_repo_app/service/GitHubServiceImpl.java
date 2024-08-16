package com.zwierzchowski.marcin.github_repo_app.service;

import com.zwierzchowski.marcin.github_repo_app.domain.Branch;
import com.zwierzchowski.marcin.github_repo_app.domain.Repository;
import com.zwierzchowski.marcin.github_repo_app.domain.dto.BranchDTO;
import com.zwierzchowski.marcin.github_repo_app.domain.dto.ResponseDTO;
import com.zwierzchowski.marcin.github_repo_app.exception.UserNotFoundException;
import java.net.URI;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class GitHubServiceImpl implements GitHubService {

  private static final Logger logger = LoggerFactory.getLogger(GitHubServiceImpl.class);

  @Value("${github.repos.api.url}")
  protected String gitHubReposApiUrl;

  @Value("${github.branches.api.url}")
  protected String gitHubBranchesApiUrl;

  private WebClient webClient;

  public GitHubServiceImpl(WebClient webClient) {
    this.webClient = webClient;
  }

  public Optional<Set<ResponseDTO>> getRepositoriesDetails(String username) {
    Flux<Repository> repositories =
        getRepositories(username).filter(repository -> !repository.fork());
    Set<ResponseDTO> responseDTOs =
        repositories
            .flatMap(repository -> buildRepositoryResponseDTO(username, repository))
            .collect(Collectors.toSet())
            .block();
    return Optional.ofNullable(responseDTOs);
  }

  public Mono<ResponseDTO> buildRepositoryResponseDTO(String username, Repository repository) {
    return getBranches(username, repository.name())
        .flatMap(branch -> Mono.just(new BranchDTO(branch.name(), branch.commit().sha())))
        .collectList()
        .map(
            branches ->
                new ResponseDTO(
                    repository.name(), repository.owner().login(), new HashSet<>(branches)));
  }

  public Flux<Repository> getRepositories(String username) {

    logger.info("Fetching repositories details for user: {}", username);
    Flux<Repository> repositories =
        fetchRepositoriesFromGitHub(username, Repository.class, gitHubReposApiUrl);

    logger.info("Successfully fetched repositories details for user: {}", username);
    return repositories;
  }

  public Flux<Branch> getBranches(String username, String repositoryName) {

    logger.info("Fetching branches for repository: {}/{}", username, repositoryName);
    Flux<Branch> branches =
        fetchBranchesFromGitHub(username, repositoryName, Branch.class, gitHubBranchesApiUrl);

    return branches;
  }

  public <T> Flux<T> fetchRepositoriesFromGitHub(
      String username, Class<T> responseType, String url) {

    logger.info("Building URI for username: {} ", username);
    URI uri = UriComponentsBuilder.fromUriString(url).buildAndExpand(username).toUri();
    return fetchResponse(uri, responseType);
  }

  public <T> Flux<T> fetchBranchesFromGitHub(
      String username, String repositoryName, Class<T> responseType, String url) {

    logger.info("Building URI for username: {} and repository: {}", username, repositoryName);
    URI uri =
        UriComponentsBuilder.fromUriString(url).buildAndExpand(username, repositoryName).toUri();

    return fetchResponse(uri, responseType);
  }

  public <T> Flux<T> fetchResponse(URI uri, Class<T> responseType) {
    logger.info("Executing GET request to URI: {}", uri);
    return webClient
        .get()
        .uri(uri)
        .retrieve()
        .onStatus(
            httpStatus -> !httpStatus.is2xxSuccessful(),
            clientResponse -> handleResponse(clientResponse.statusCode()))
        .bodyToFlux(responseType)
        .onErrorMap(
            Exception.class,
            ex -> {
              logger.error(ex.getMessage());
              return new RuntimeException("Error fetching response", ex);
            });
  }

  private Mono<? extends Throwable> handleResponse(HttpStatusCode statusCode) {
    if (statusCode.is4xxClientError()) {
      logger.error("Client error occurred. Status code: {}", statusCode);
      return Mono.error(new UserNotFoundException("User not found on GitHub"));
    } else if (statusCode.is5xxServerError()) {
      logger.error("Server error occurred. Status code: {}", statusCode);
      return Mono.error(new RuntimeException("Server error occurred"));
    } else {
      logger.error("Unexpected error occurred. Status code: {}", statusCode);
      return Mono.error(new RuntimeException("Unexpected error occurred"));
    }
  }
}
