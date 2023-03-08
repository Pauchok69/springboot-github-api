package com.demoapp.springbootgithubapi.service;

import com.demoapp.springbootgithubapi.payload.RepositoryDTO;

import java.util.List;

public interface RepositoryService {
    public List<RepositoryDTO> getAllNonForkedRepositoriesByUsername(String username);
}
