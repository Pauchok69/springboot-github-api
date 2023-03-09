package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.client.GithubRestTemplate;
import com.demoapp.springbootgithubapi.mapper.RepositoryMapper;
import com.demoapp.springbootgithubapi.model.Owner;
import com.demoapp.springbootgithubapi.model.Repository;
import com.demoapp.springbootgithubapi.payload.BranchDTO;
import com.demoapp.springbootgithubapi.payload.RepositoryDTO;
import com.demoapp.springbootgithubapi.service.BranchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RepositoryServiceImplTest {
    private static GithubRestTemplate githubRestTemplateMock;
    private static BranchService branchServiceMock;
    private static RepositoryServiceImpl repositoryService;


    @BeforeAll
    static void beforeAll() {
        githubRestTemplateMock = mock(GithubRestTemplate.class);
        branchServiceMock = mock(BranchServiceImpl.class);
        repositoryService = new RepositoryServiceImpl(githubRestTemplateMock, Mappers.getMapper(RepositoryMapper.class), branchServiceMock);
    }

    @Test
    void getAllNonForkedRepositoriesByUsernameShouldWorkCorrectWithZeroRepositories() {
        when(githubRestTemplateMock.getUserRepositoriesByUsername(anyString()))
                .thenReturn(Collections.emptyList());

        List<RepositoryDTO> repositories = repositoryService.getAllNonForkedRepositoriesByUsername(anyString());
        Assertions.assertEquals(0, repositories.size());
    }

    @Test
    void getAllNonForkedRepositoriesByUsernameMappedCorrectly() {
        Repository repository = createTestRepository();

        when(githubRestTemplateMock.getUserRepositoriesByUsername(anyString()))
                .thenReturn(List.of(repository));

        List<RepositoryDTO> repositoriesDTOs = repositoryService.getAllNonForkedRepositoriesByUsername(anyString());

        Assertions.assertEquals(repositoriesDTOs.get(0).getName(), repository.getName());
        Assertions.assertEquals(repositoriesDTOs.get(0).getOwnerLogin(), repository.getOwner().getLogin());
    }

    @Test
    void getAllNonForkedRepositoriesByUsernameShouldNotContainForkedRepositories() {
        Repository repository = createTestRepository();

        Repository forkedRepository = createTestRepository();
        forkedRepository.setFork(Boolean.TRUE);

        when(githubRestTemplateMock.getUserRepositoriesByUsername(anyString()))
                .thenReturn(List.of(repository, forkedRepository));

        List<RepositoryDTO> repositoriesDTOs = repositoryService.getAllNonForkedRepositoriesByUsername(anyString());
        System.out.println("repositoriesDTOs = " + repositoriesDTOs);

        Assertions.assertEquals(1, repositoriesDTOs.size());
    }

    @Test
    void getAllNonForkedRepositoriesByUsernameShouldWorkCorrectlyWithBranches() {
        Repository repository = createTestRepository();

        when(githubRestTemplateMock.getUserRepositoriesByUsername(anyString()))
                .thenReturn(List.of(repository));

        when(branchServiceMock.getBranches(anyString(), anyString()))
                .thenReturn(List.of(new BranchDTO(), new BranchDTO()));

        List<RepositoryDTO> repositoriesDTOs = repositoryService.getAllNonForkedRepositoriesByUsername(anyString());

        Assertions.assertEquals(2, repositoriesDTOs.get(0).getBranches().size());
    }

    private static Repository createTestRepository() {
        Owner owner = new Owner();
        owner.setLogin("test owner");

        Repository repository = new Repository();
        repository.setName("test repository");
        repository.setFork(Boolean.FALSE);
        repository.setOwner(owner);
        return repository;
    }
}