package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.payload.BranchDTO;
import com.demoapp.springbootgithubapi.service.BranchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {
    @Override
    public List<BranchDTO> getBranches(String username, String repositoryName) {
        return null;
    }
}
