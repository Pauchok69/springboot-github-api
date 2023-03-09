package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.client.GithubRestTemplate;
import com.demoapp.springbootgithubapi.mapper.BranchMapper;
import com.demoapp.springbootgithubapi.model.Branch;
import com.demoapp.springbootgithubapi.model.Commit;
import com.demoapp.springbootgithubapi.payload.BranchDTO;
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

class BranchServiceImplTest {
    public static final String TEST_USERNAME = "Test Username";
    public static final String TEST_REPOSITORY_NAME = "Test Repository Name";
    private static GithubRestTemplate githubRestTemplateMock;
    private static BranchService branchService;

    @BeforeAll
    static void beforeAll() {
        githubRestTemplateMock = mock(GithubRestTemplate.class);
        branchService = new BranchServiceImpl(githubRestTemplateMock, Mappers.getMapper(BranchMapper.class));
    }

    @Test
    void getBranchesShouldWorkCorrectWithZeroBranches() {
        when(githubRestTemplateMock.getRepositoryBranches(anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        List<BranchDTO> branches = branchService.getBranches(TEST_USERNAME, TEST_REPOSITORY_NAME);
        Assertions.assertEquals(0, branches.size());
    }

    @Test
    void getBranchesMappedCorrectly() {
        Commit commit = new Commit();
        commit.setSha("Test Sha");

        Branch branch = new Branch();
        branch.setName("Test Branch");
        branch.setCommit(commit);

        when(githubRestTemplateMock.getRepositoryBranches(anyString(), anyString()))
                .thenReturn(List.of(branch));

        List<BranchDTO> branchDTOs = branchService.getBranches(TEST_USERNAME, TEST_REPOSITORY_NAME);

        Assertions.assertEquals(branchDTOs.get(0).getName(), branch.getName());
        Assertions.assertEquals(branchDTOs.get(0).getLastCommitSha(), branch.getCommit().getSha());
    }
}