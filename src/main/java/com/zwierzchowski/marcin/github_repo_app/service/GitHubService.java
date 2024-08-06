package com.zwierzchowski.marcin.github_repo_app.service;

import com.zwierzchowski.marcin.github_repo_app.domain.dto.BranchDTO;
import com.zwierzchowski.marcin.github_repo_app.domain.dto.ResponseDTO;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.Set;

public interface GitHubService {

    Set<ResponseDTO> getRepositoriesDetails(String username);

    Set<BranchDTO> getRepositoryBranchesDetails(String username, String repoName);


    <T> Flux<T> getResponse(String username, Class<T> responseType, String url);

    <T> Flux<T> fetchResponse(URI uri, Class<T> responseType);

}
