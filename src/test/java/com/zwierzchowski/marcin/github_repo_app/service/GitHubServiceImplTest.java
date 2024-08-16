package com.zwierzchowski.marcin.github_repo_app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.zwierzchowski.marcin.github_repo_app.domain.Branch;
import com.zwierzchowski.marcin.github_repo_app.domain.Commit;
import com.zwierzchowski.marcin.github_repo_app.domain.Owner;
import com.zwierzchowski.marcin.github_repo_app.domain.Repository;
import com.zwierzchowski.marcin.github_repo_app.domain.dto.BranchDTO;
import com.zwierzchowski.marcin.github_repo_app.domain.dto.ResponseDTO;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class GitHubServiceImplTest {

  @Mock private WebClient webClient;

  @InjectMocks private GitHubServiceImpl gitHubService;

  String username;
  Repository repository;
  Branch branch;
  String repoName;
  GitHubServiceImpl spyGitHubService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    gitHubService = new GitHubServiceImpl(webClient);
    spyGitHubService = spy(gitHubService);

    gitHubService.gitHubReposApiUrl = "https://api.github.com/users/{username}/repos";
    gitHubService.gitHubBranchesApiUrl = "https://api.github.com/repos/{username}/{repo}/branches";

    username = "tester";
    repository = new Repository("repo1", "url", new Owner("owner"), false);
    branch = new Branch("main", new Commit("sha123"));
    repoName = "repo1";
  }

  @Test
  void testGetRepositories() {

    doReturn(Flux.just(repository))
        .when(spyGitHubService)
        .fetchRepositoriesFromGitHub(username, Repository.class, gitHubService.gitHubReposApiUrl);

    Flux<Repository> result = spyGitHubService.getRepositories(username);

    StepVerifier.create(result).expectNext(repository).verifyComplete();
  }

  @Test
  void testGetBranches() {

    doReturn(Flux.just(branch))
        .when(spyGitHubService)
        .fetchBranchesFromGitHub(
            username, repoName, Branch.class, gitHubService.gitHubBranchesApiUrl);

    Flux<Branch> result = spyGitHubService.getBranches(username, repoName);

    StepVerifier.create(result).expectNext(branch).verifyComplete();
  }

  @Test
  void getRepositoriesDetails() {

    ResponseDTO expectedResponseDTO =
        new ResponseDTO("repo1", "owner", new HashSet<>(Set.of(new BranchDTO("main", "sha123"))));

    doReturn(Flux.just(repository)).when(spyGitHubService).getRepositories(username);
    doReturn(Mono.just(expectedResponseDTO))
        .when(spyGitHubService)
        .buildRepositoryResponseDTO(username, repository);

    Optional<Set<ResponseDTO>> result = spyGitHubService.getRepositoriesDetails(username);

    assertTrue(result.isPresent());
    assertEquals(1, result.get().size());
    assertEquals(expectedResponseDTO, result.get().iterator().next());
  }

  @Test
  void buildRepositoryResponseDTO() {

    BranchDTO expectedBranchDTO = new BranchDTO("main", "sha123");
    ResponseDTO expectedResponseDTO =
        new ResponseDTO("repo1", "owner", new HashSet<>(List.of(expectedBranchDTO)));

    doReturn(Flux.just(branch)).when(spyGitHubService).getBranches(username, repository.name());

    Mono<ResponseDTO> result = spyGitHubService.buildRepositoryResponseDTO(username, repository);

    StepVerifier.create(result).expectNext(expectedResponseDTO).verifyComplete();
  }
}
