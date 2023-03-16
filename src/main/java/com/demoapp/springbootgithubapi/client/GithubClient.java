package com.demoapp.springbootgithubapi.client;

import com.demoapp.springbootgithubapi.client.model.Branch;
import com.demoapp.springbootgithubapi.client.model.Repository;
import org.springframework.http.ResponseEntity;

public interface GithubClient {
    ResponseEntity<Repository[]> getUserRepositoriesByUsername(String username, int page);

    ResponseEntity<Branch[]> getRepositoryBranches(String username, String repositoryName, int page);
}
