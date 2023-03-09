package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.client.GithubRestTemplate;
import com.demoapp.springbootgithubapi.mapper.RepositoryMapper;
import com.demoapp.springbootgithubapi.model.Owner;
import com.demoapp.springbootgithubapi.model.Repository;
import com.demoapp.springbootgithubapi.payload.RepositoryDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RepositoryServiceImplTest {
    private static GithubRestTemplate githubRestTemplateMock;
    private static RepositoryServiceImpl repositoryService;

    @BeforeAll
    static void beforeAll() {
        githubRestTemplateMock = mock(GithubRestTemplate.class);
        repositoryService = new RepositoryServiceImpl(githubRestTemplateMock, Mappers.getMapper(RepositoryMapper.class));
    }

    @Test
    void getAllNonForkedRepositoriesByUsernameShouldWorkCorrectWithZeroRepositories() {
        when(githubRestTemplateMock.getUserRepositoriesByUsername(anyString()))
                .thenReturn(Collections.emptyList());

        List<RepositoryDTO> repositories = repositoryService.getAllNonForkedRepositoriesByUsername(anyString());
        Assertions.assertEquals(0, repositories.size());
    }

    @Test
    void getAllNonForkedRepositoriesByUsernameShouldWorkCorrectWithNoneZeroRepositories() {
        when(githubRestTemplateMock.getUserRepositoriesByUsername(anyString()))
                .thenReturn(Arrays.asList(new Repository(), new Repository(), new Repository()));

        List<RepositoryDTO> repositories = repositoryService.getAllNonForkedRepositoriesByUsername(anyString());
        Assertions.assertEquals(3, repositories.size());
    }

    @Test
    void getAllNonForkedRepositoriesByUsernameMappedCorrectly() {
        Owner owner = new Owner();
        owner.setLogin("test owner");

        Repository repository = new Repository();
        repository.setName("test repository");
        repository.setOwner(owner);

        when(githubRestTemplateMock.getUserRepositoriesByUsername(anyString()))
                .thenReturn(List.of(repository));

        List<RepositoryDTO> repositoriesDTOs = repositoryService.getAllNonForkedRepositoriesByUsername(anyString());

        Assertions.assertEquals(repositoriesDTOs.get(0).getName(), repository.getName());
        Assertions.assertEquals(repositoriesDTOs.get(0).getOwnerLogin(), repository.getOwner().getLogin());
    }
}