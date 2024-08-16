package com.zwierzchowski.marcin.github_repo_app.domain.dto;

import java.util.Set;

public record ResponseDTO(String repositoryName, String ownerLogin, Set<BranchDTO> branches) {}
