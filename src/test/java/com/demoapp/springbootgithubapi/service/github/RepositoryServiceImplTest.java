package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.client.GithubClient;
import com.demoapp.springbootgithubapi.mapper.RepositoryMapper;
import com.demoapp.springbootgithubapi.model.Owner;
import com.demoapp.springbootgithubapi.model.Repository;
import com.demoapp.springbootgithubapi.payload.BranchDTO;
import com.demoapp.springbootgithubapi.payload.RepositoryDTO;
import com.demoapp.springbootgithubapi.service.BranchService;
import com.demoapp.springbootgithubapi.service.GithubNextLinkCheckerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepositoryServiceImplTest {
    public static final String TEST_USERNAME = "Test Username";
    public static final boolean INCLUDE_FORKED_FALSE = false;

    @Mock
    private ResponseEntity<Repository[]> responseEntityMock;
    @Mock
    private HttpHeaders httpHeadersMock;
    @Mock
    private GithubClient githubClientMock;
    @Mock
    private RepositoryMapper repositoryMapper;
    @Mock
    private BranchService branchServiceMock;
    @Mock
    private GithubNextLinkCheckerService githubNextLinkCheckerServiceMock;
    @InjectMocks
    private RepositoryServiceImpl repositoryService;

    @BeforeEach
    void setUp() {
        when(githubClientMock.getUserRepositoriesByUsername(anyString(), anyInt()))
                .thenReturn(responseEntityMock);
        lenient().when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
    }

    @Test
    void getAllRepositoriesByUsernameShouldReturnEmptyWhenResponseEntityBodyIsNull() {
        when(responseEntityMock.getBody()).thenReturn(null);

        List<RepositoryDTO> repositories = repositoryService.getAllRepositoriesByUsername(TEST_USERNAME, INCLUDE_FORKED_FALSE);

        assertEquals(0, repositories.size());

        verify(responseEntityMock, times(1)).getBody();
        verify(repositoryMapper, never()).repositoryToRepositoryDto(any(Repository.class));
        verify(githubNextLinkCheckerServiceMock, never()).doesNextLinkExistInHeaders(any(HttpHeaders.class));
        verify(branchServiceMock, never()).getAllBranches(anyString(), anyString());
    }

    @Test
    void getAllRepositoriesByUsernameShouldReturnEmptyListForUserWithoutRepositories() {
        when(responseEntityMock.getBody()).thenReturn(new Repository[0]);
        when(githubNextLinkCheckerServiceMock.doesNextLinkExistInHeaders(any(HttpHeaders.class)))
                .thenReturn(false);

        List<RepositoryDTO> repositories = repositoryService.getAllRepositoriesByUsername(TEST_USERNAME, INCLUDE_FORKED_FALSE);

        assertEquals(0, repositories.size());

        verify(githubClientMock, times(1))
                .getUserRepositoriesByUsername(anyString(), anyInt());
        verify(responseEntityMock, times(1)).getBody();
        verify(repositoryMapper, never()).repositoryToRepositoryDto(any(Repository.class));
        verify(githubNextLinkCheckerServiceMock, times(1))
                .doesNextLinkExistInHeaders(any(HttpHeaders.class));
    }

    @Test
    void getAllRepositoriesByUsernameShouldWorksCorrectlyForLessThan100Repositories() {
        Repository[] repositories = createTestNonForkedRepositories(99);

        when(responseEntityMock.getBody()).thenReturn(repositories);
        when(githubNextLinkCheckerServiceMock.doesNextLinkExistInHeaders(any(HttpHeaders.class)))
                .thenReturn(false);
        when(repositoryMapper.repositoryToRepositoryDto(any(Repository.class)))
                .thenReturn(new RepositoryDTO());
        when(branchServiceMock.getAllBranches(any(RepositoryDTO.class)))
                .thenReturn(List.of(new BranchDTO()));

        List<RepositoryDTO> repositoryDTOs = repositoryService.getAllRepositoriesByUsername(TEST_USERNAME, INCLUDE_FORKED_FALSE);

        assertEquals(99, repositoryDTOs.size());

        verify(githubClientMock, times(1))
                .getUserRepositoriesByUsername(anyString(), anyInt());
        verify(responseEntityMock, times(1)).getBody();
        verify(repositoryMapper, times(99)).repositoryToRepositoryDto(any(Repository.class));
        verify(githubNextLinkCheckerServiceMock, times(1))
                .doesNextLinkExistInHeaders(any(HttpHeaders.class));
    }

    @Test
    void getAllRepositoriesByUsernameShouldWorksCorrectlyForMoreThan100Repositories() {
        when(responseEntityMock.getBody())
                .thenReturn(createTestNonForkedRepositories(100))
                .thenReturn(createTestNonForkedRepositories(100))
                .thenReturn(createTestNonForkedRepositories(34));
        when(githubNextLinkCheckerServiceMock.doesNextLinkExistInHeaders(any(HttpHeaders.class)))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(repositoryMapper.repositoryToRepositoryDto(any(Repository.class)))
                .thenReturn(new RepositoryDTO());
        when(branchServiceMock.getAllBranches(any(RepositoryDTO.class)))
                .thenReturn(List.of(new BranchDTO()));

        List<RepositoryDTO> repositoryDTOs = repositoryService.getAllRepositoriesByUsername(TEST_USERNAME, INCLUDE_FORKED_FALSE);

        assertEquals(234, repositoryDTOs.size());

        verify(githubClientMock, times(3))
                .getUserRepositoriesByUsername(anyString(), anyInt());
        verify(responseEntityMock, times(3)).getBody();
        verify(repositoryMapper, times(234)).repositoryToRepositoryDto(any(Repository.class));
        verify(githubNextLinkCheckerServiceMock, times(3))
                .doesNextLinkExistInHeaders(any(HttpHeaders.class));
    }

    @Test
    void getAllRepositoriesByUsernameShouldWorksCorrectlyWhenGettingNonForkedRepositories() {
        Repository[] repositories = new Repository[]{
                createTestRepository(false),
                createTestRepository(false),
                createTestRepository(true)
        };
        when(responseEntityMock.getBody()).thenReturn(repositories);
        when(githubNextLinkCheckerServiceMock.doesNextLinkExistInHeaders(any(HttpHeaders.class)))
                .thenReturn(false);
        when(repositoryMapper.repositoryToRepositoryDto(any(Repository.class)))
                .thenReturn(new RepositoryDTO());
        when(branchServiceMock.getAllBranches(any(RepositoryDTO.class)))
                .thenReturn(List.of(new BranchDTO()));

        List<RepositoryDTO> repositoryDTOs = repositoryService.getAllRepositoriesByUsername(TEST_USERNAME, INCLUDE_FORKED_FALSE);

        assertEquals(2, repositoryDTOs.size());

        verify(githubClientMock, times(1))
                .getUserRepositoriesByUsername(anyString(), anyInt());
        verify(responseEntityMock, times(1)).getBody();
        verify(repositoryMapper, times(2)).repositoryToRepositoryDto(any(Repository.class));
        verify(githubNextLinkCheckerServiceMock, times(1))
                .doesNextLinkExistInHeaders(any(HttpHeaders.class));
    }

    private Repository[] createTestNonForkedRepositories(int length) {
        Repository[] repositories = new Repository[length];

        for (int i = 0; i < length; i++) {
            repositories[i] = createTestRepository(false);
        }
        return repositories;
    }

    private Repository createTestRepository(Boolean isForked) {
        Owner owner = new Owner();
        owner.setLogin("test owner");

        Repository repository = new Repository();
        repository.setName("test repository");
        repository.setFork(isForked);
        repository.setOwner(owner);
        return repository;
    }
}