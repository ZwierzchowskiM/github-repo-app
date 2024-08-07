package com.zwierzchowski.marcin.github_repo_app.service;

import com.zwierzchowski.marcin.github_repo_app.domain.Branch;
import com.zwierzchowski.marcin.github_repo_app.domain.Repository;
import com.zwierzchowski.marcin.github_repo_app.domain.dto.BranchDTO;
import com.zwierzchowski.marcin.github_repo_app.domain.dto.ResponseDTO;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

public interface GitHubService {

    Optional<Set<ResponseDTO>> getRepositoriesDetails(String username);

    Flux<Repository> getRepositories(String username);

    Flux<Branch> getBranches(String username, String repoName);

    <T> Flux<T> getRepositoryResponse(String username, Class<T> responseType, String url);

    <T> Flux<T> fetchResponse(URI uri, Class<T> responseType);

}
