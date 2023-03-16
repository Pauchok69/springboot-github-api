package com.demoapp.springbootgithubapi.controller;

import com.demoapp.springbootgithubapi.dto.RepositoryDTO;
import com.demoapp.springbootgithubapi.service.RepositoryService;
import com.demoapp.springbootgithubapi.service.github.RepositoryServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final RepositoryService repositoryService;

    public UserController(RepositoryServiceImpl repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping(
            value = "/{username}/repositories",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<RepositoryDTO> getRepositoriesByUsername(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") boolean includeForked
    ) {
        return repositoryService.getAllRepositoriesByUsername(username, includeForked);
    }
}
