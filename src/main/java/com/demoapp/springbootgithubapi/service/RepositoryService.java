package com.demoapp.springbootgithubapi.service;

import com.demoapp.springbootgithubapi.payload.RepositoryDTO;

import java.util.List;

public interface RepositoryService {
    /**
     * Collects all GitHub repositories with branches of user with provided username.
     *
     * @param username      GitHub username;
     * @param includeForked should include forked branches;
     * @return List of user repositories with branches.
     */
    List<RepositoryDTO> getAllRepositoriesByUsername(String username, boolean includeForked);
}
