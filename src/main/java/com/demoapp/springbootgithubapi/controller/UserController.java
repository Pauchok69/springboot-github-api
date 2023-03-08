package com.demoapp.springbootgithubapi.controller;

import com.demoapp.springbootgithubapi.payload.RepositoryDTO;
import com.demoapp.springbootgithubapi.service.RepositoryService;
import com.demoapp.springbootgithubapi.service.github.RepositoryServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final RepositoryService repositoryService;

    public UserController(RepositoryServiceImpl repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping("/{username}/repositories")
    public List<RepositoryDTO> getRepositoriesByUsername(@PathVariable String username) {
        return repositoryService.getAllNonForkedRepositoriesByUsername(username);
    }
}
