package com.zwierzchowski.marcin.github_repo_app.service;

import com.zwierzchowski.marcin.github_repo_app.domain.Branch;
import com.zwierzchowski.marcin.github_repo_app.domain.Repository;
import com.zwierzchowski.marcin.github_repo_app.domain.dto.ResponseDTO;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GitHubService {

  Optional<Set<ResponseDTO>> getRepositoriesDetails(String username);

  Mono<ResponseDTO> buildRepositoryResponseDTO(String username, Repository repository);

  Flux<Repository> getRepositories(String username);

  Flux<Branch> getBranches(String username, String repoName);

  <T> Flux<T> fetchRepositoriesFromGitHub(String username, Class<T> responseType, String url);

  <T> Flux<T> fetchBranchesFromGitHub(
      String username, String repositoryName, Class<T> responseType, String url);

  <T> Flux<T> fetchResponse(URI uri, Class<T> responseType);
}
