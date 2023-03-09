package com.demoapp.springbootgithubapi.client;

import com.demoapp.springbootgithubapi.model.Branch;
import com.demoapp.springbootgithubapi.model.Repository;

import java.util.List;

public interface GithubRestTemplate {
    List<Repository> getUserRepositoriesByUsername(String username);

    List<Branch> getBranches(String username, Repository repository);
}
