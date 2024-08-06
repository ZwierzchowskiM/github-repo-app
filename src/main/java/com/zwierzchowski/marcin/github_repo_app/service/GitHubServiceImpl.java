package com.zwierzchowski.marcin.github_repo_app.service;

import com.zwierzchowski.marcin.github_repo_app.domain.Branch;
import com.zwierzchowski.marcin.github_repo_app.domain.dto.BranchDTO;
import com.zwierzchowski.marcin.github_repo_app.domain.Repository;
import com.zwierzchowski.marcin.github_repo_app.domain.dto.ResponseDTO;
import com.zwierzchowski.marcin.github_repo_app.exception.UserNotFoundException;
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

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
public class GitHubServiceImpl implements GitHubService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubServiceImpl.class);

    @Value("${github.repos.api.url:https://api.github.com/users/{username}/repos}")
    private String gitHubReposApiUrl;
    @Value("${github.branches.api.url:https://api.github.com/repos/{username}/{reponame}/branches}")
    private String gitHubBranchesApiUrl;
    @Value("${github.commits.api.url:https://api.github.com/repos/{username}/{reponame}/commits}")
    private String gitHubCommitsApiUrl;



    WebClient webClient;

    public GitHubServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    public Set<ResponseDTO> getRepositoriesDetails(String username) {

        logger.info("Fetching repositories details for user: {}", username);
        Flux<Repository> repositories = getResponse(username, Repository.class, gitHubReposApiUrl);

        Set<ResponseDTO> responseDTOS =
                repositories
                        .map(repository -> new ResponseDTO(repository.name()))
                        .collect(Collectors.toSet()).block();

        logger.info("Successfully fetched repositories details for user: {}", username);
        return responseDTOS;
    }

    public Set<BranchDTO> getRepositoryBranchesDetails(String username, String repositoryName) {

        logger.info("Fetching branches for repository: {}/{}", username, repositoryName);
        Flux<Branch> branches = getResponse(username, repositoryName, Branch.class, gitHubBranchesApiUrl);

        Set<BranchDTO> branchDTOS =
                branches
                        .map(branch -> new BranchDTO(branch.getName()))
                        .collect(Collectors.toSet()).block();

        return branchDTOS;
    }

    public <T> Flux<T> getResponse(String username, Class<T> responseType, String url) {

        logger.info("Building URI for username: {} ", username);
        URI uri = UriComponentsBuilder.fromUriString(url)
                .buildAndExpand(username)
                .toUri();

        return fetchResponse(uri, responseType);
    }

    public <T> Flux<T> getResponse(String username, String repositoryName, Class<T> responseType, String url) {

        logger.info("Building URI for username: {} and repository: {}", username, repositoryName);
        URI uri = UriComponentsBuilder.fromUriString(url)
                .buildAndExpand(username, repositoryName)
                .toUri();

        return fetchResponse(uri, responseType);
    }

    public <T> Flux<T> fetchResponse(URI uri, Class<T> responseType) {

        logger.info("Executing GET request to URI: {}", uri);
        Flux<T> response = webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(httpStatus -> !httpStatus.is2xxSuccessful(),
                        clientResponse -> handleResponse(clientResponse.statusCode()))
                .bodyToFlux(responseType)
                .log()
                .onErrorResume(Exception.class, ex -> {
                            logger.error(ex.getMessage());
                            return Mono.empty();
                        }
                );

        return response;
    }

    private Mono<? extends Throwable> handleResponse(HttpStatusCode statusCode) {

        if (statusCode.is2xxSuccessful()) {
            logger.info("Request successful ");
            return null;
        } else if (statusCode.is4xxClientError()) {
            logger.error("Request failed. Received status code: {}", statusCode);
            return Mono.error(new UserNotFoundException("User not found on GitHub"));
        } else if (statusCode.is5xxServerError()) {
            logger.error("Request failed. Received status code: {}", statusCode);
            return Mono.error(new RuntimeException("Server error"));
        } else {
            logger.error("Unexpected status code. Received status code: {}", statusCode);
            return Mono.error(new RuntimeException("Unexpected error"));
        }
    }

}


