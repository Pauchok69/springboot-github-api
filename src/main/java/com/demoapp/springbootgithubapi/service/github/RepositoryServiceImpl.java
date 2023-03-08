package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.payload.RepositoryDTO;
import com.demoapp.springbootgithubapi.service.RepositoryService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RepositoryServiceImpl implements RepositoryService {
    @Override
    public List<RepositoryDTO> getAllNonForkedRepositoriesByUsername(String username) {
        return Collections.emptyList();
    }
}
