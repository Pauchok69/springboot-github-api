package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.client.GithubRestTemplate;
import com.demoapp.springbootgithubapi.mapper.RepositoryMapper;
import com.demoapp.springbootgithubapi.model.Repository;
import com.demoapp.springbootgithubapi.payload.RepositoryDTO;
import com.demoapp.springbootgithubapi.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepositoryServiceImpl implements RepositoryService {
    private final GithubRestTemplate githubRestTemplate;
    private final RepositoryMapper repositoryMapper;

    @Autowired
    public RepositoryServiceImpl(GithubRestTemplate githubRestTemplate, RepositoryMapper repositoryMapper) {
        this.githubRestTemplate = githubRestTemplate;
        this.repositoryMapper = repositoryMapper;
    }

    @Override
    public List<RepositoryDTO> getAllNonForkedRepositoriesByUsername(String username) {
        List<Repository> repositories = githubRestTemplate.getUserRepositoriesByUsername(username);

        return repositories.stream().map(repositoryMapper::repositoryToRepositoryDto).toList();
    }
}
