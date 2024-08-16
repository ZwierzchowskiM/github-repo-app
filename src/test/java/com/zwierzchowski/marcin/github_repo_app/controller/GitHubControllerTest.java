package com.zwierzchowski.marcin.github_repo_app.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.zwierzchowski.marcin.github_repo_app.domain.dto.ResponseDTO;
import com.zwierzchowski.marcin.github_repo_app.service.GitHubService;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GitHubControllerTest {

  private final String username = "testUser";

  @Mock private GitHubService gitHubService;

  @InjectMocks private GitHubController gitHubController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetUserRepositories_ReturnsRepositories() {

    Set<ResponseDTO> mockResponse = Set.of(new ResponseDTO("repo1", "owner1", Set.of()));
    when(gitHubService.getRepositoriesDetails(username)).thenReturn(Optional.of(mockResponse));

    ResponseEntity<Set<ResponseDTO>> response = gitHubController.getUserRepositories(username);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockResponse, response.getBody());
    verify(gitHubService, times(1)).getRepositoriesDetails(username);
  }

  @Test
  void testGetUserRepositories_ReturnsNoContent() {

    when(gitHubService.getRepositoriesDetails(username)).thenReturn(Optional.empty());

    ResponseEntity<Set<ResponseDTO>> response = gitHubController.getUserRepositories(username);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(gitHubService, times(1)).getRepositoriesDetails(username);
  }
}
