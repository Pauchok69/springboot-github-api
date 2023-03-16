package com.demoapp.springbootgithubapi.service;

import com.demoapp.springbootgithubapi.dto.BranchDTO;
import com.demoapp.springbootgithubapi.dto.RepositoryDTO;

import java.util.List;

public interface BranchService {
    List<BranchDTO> getAllBranches(String username, String repositoryName);
    List<BranchDTO> getAllBranches(RepositoryDTO repositoryDTO);
}
