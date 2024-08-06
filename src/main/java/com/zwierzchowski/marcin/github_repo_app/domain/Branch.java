package com.zwierzchowski.marcin.github_repo_app.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Branch {

    private String name;
    private Commit commit;
    @JsonProperty("protected")
    private boolean branchProtected;
}