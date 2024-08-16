package com.zwierzchowski.marcin.github_repo_app.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Repository(
    String name, @JsonProperty("branches_url") String branchesUrl, Owner owner, boolean fork) {}
