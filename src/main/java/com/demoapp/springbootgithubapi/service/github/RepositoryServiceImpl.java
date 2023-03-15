package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.client.GithubClient;
import com.demoapp.springbootgithubapi.mapper.RepositoryMapper;
import com.demoapp.springbootgithubapi.model.Repository;
import com.demoapp.springbootgithubapi.payload.RepositoryDTO;
import com.demoapp.springbootgithubapi.service.BranchService;
import com.demoapp.springbootgithubapi.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepositoryServiceImpl implements RepositoryService {
    private final GithubClient githubRestTemplate;
    private final RepositoryMapper repositoryMapper;
    private final BranchService branchService;

    @Autowired
    public RepositoryServiceImpl(
            GithubClient githubRestTemplate,
            RepositoryMapper repositoryMapper,
            BranchService branchService
    ) {
        this.githubRestTemplate = githubRestTemplate;
        this.repositoryMapper = repositoryMapper;
        this.branchService = branchService;
    }

    @Override
    public List<RepositoryDTO> getAllNonForkedRepositoriesByUsername(String username) {
        List<Repository> repositories = githubRestTemplate.getUserRepositoriesByUsername(username);

        List<RepositoryDTO> repositoryDTOs = repositories
                .stream()
                .filter(repository -> !repository.getFork())
                .map(repositoryMapper::repositoryToRepositoryDto)
                .toList();
        repositoryDTOs
                .stream()
                .parallel()
                .forEach(r -> r.setBranches(branchService.getBranches(r.getOwnerLogin(), r.getName())));

        return repositoryDTOs;
    }
}
