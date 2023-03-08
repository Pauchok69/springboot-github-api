package com.demoapp.springbootgithubapi.service;

import com.demoapp.springbootgithubapi.payload.BranchDTO;

import java.util.List;

public interface BranchService {
    public List<BranchDTO> getBranches(String username, String repositoryName);
}
