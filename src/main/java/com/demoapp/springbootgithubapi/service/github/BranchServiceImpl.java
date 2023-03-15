package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.client.GithubClient;
import com.demoapp.springbootgithubapi.mapper.BranchMapper;
import com.demoapp.springbootgithubapi.model.Branch;
import com.demoapp.springbootgithubapi.payload.BranchDTO;
import com.demoapp.springbootgithubapi.service.BranchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {
    private final GithubClient restTemplate;
    private final BranchMapper branchMapper;

    public BranchServiceImpl(GithubClient restTemplate, BranchMapper branchMapper) {
        this.restTemplate = restTemplate;
        this.branchMapper = branchMapper;
    }

    @Override
    public List<BranchDTO> getBranches(String username, String repositoryName) {
        List<Branch> repositoryBranches = restTemplate.getRepositoryBranches(
                username,
                repositoryName
        );

        return repositoryBranches
                .stream()
                .map(branchMapper::branchToBranchDto)
                .toList();
    }
}
