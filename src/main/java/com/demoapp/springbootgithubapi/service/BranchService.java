package com.demoapp.springbootgithubapi.service;

import com.demoapp.springbootgithubapi.payload.BranchDTO;
import com.demoapp.springbootgithubapi.payload.RepositoryDTO;

import java.util.List;

public interface BranchService {
    List<BranchDTO> getAllBranches(String username, String repositoryName);
    List<BranchDTO> getAllBranches(RepositoryDTO repositoryDTO);
}
