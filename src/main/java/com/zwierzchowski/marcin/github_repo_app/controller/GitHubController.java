package com.zwierzchowski.marcin.github_repo_app.controller;

import com.zwierzchowski.marcin.github_repo_app.domain.dto.ResponseDTO;
import com.zwierzchowski.marcin.github_repo_app.service.GitHubService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("${api.path}/github")
@Log4j2
public class GitHubController {


    private GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping(value = "/repositories", headers = "Accept=application/json")
    public ResponseEntity<Set<ResponseDTO>> getUserRepositories(@RequestParam String username) {

        log.info("Fetching repositories for user: {}", username);
        Set<ResponseDTO> allRepositories = gitHubService.getRepositoriesDetails(username);

        return ResponseEntity.ok(allRepositories);
    }


}
