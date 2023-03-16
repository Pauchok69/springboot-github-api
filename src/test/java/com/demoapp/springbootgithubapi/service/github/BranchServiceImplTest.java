package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.client.GithubClient;
import com.demoapp.springbootgithubapi.mapper.BranchMapper;
import com.demoapp.springbootgithubapi.model.Branch;
import com.demoapp.springbootgithubapi.model.Commit;
import com.demoapp.springbootgithubapi.payload.BranchDTO;
import com.demoapp.springbootgithubapi.payload.RepositoryDTO;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchServiceImplTest {
    public static final String TEST_USERNAME = "Test Username";
    public static final String TEST_REPOSITORY_NAME = "Test Repository Name";
    public static final String TEST_BRANCH_NAME = "Test Branch Name";
    public static final String TEST_COMMIT_SHA = "Test Commit SHA";

    @Mock
    private ResponseEntity<Branch[]> responseEntityMock;
    @Mock
    private HttpHeaders httpHeadersMock;
    @Mock
    private GithubClient githubClientMock;
    @Mock
    private BranchMapper branchMapperMock;
    @Mock
    private GithubNextLinkCheckerService githubNextLinkCheckerServiceMock;
    @InjectMocks
    private BranchServiceImpl branchService;

    @BeforeEach
    void setUp() {
        when(githubClientMock.getRepositoryBranches(anyString(), anyString(), anyInt()))
                .thenReturn(responseEntityMock);
        lenient().when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
    }

    @Test
    void getAllBranchesShouldReturnEmptyWhenResponseEntityBodyIsNull() {
        when(responseEntityMock.getBody()).thenReturn(null);

        List<BranchDTO> branches = branchService.getAllBranches(TEST_USERNAME, TEST_REPOSITORY_NAME);

        assertEquals(0, branches.size());

        verify(responseEntityMock, times(1)).getBody();
        verify(branchMapperMock, never()).branchToBranchDto(any(Branch.class));
        verify(githubNextLinkCheckerServiceMock, never()).doesNextLinkExistInHeaders(any(HttpHeaders.class));
    }

    @Test
    void getAllBranchesShouldReturnEmptyListForRepositoryWithoutBranches() {
        when(responseEntityMock.getBody()).thenReturn(new Branch[]{});
        when(githubNextLinkCheckerServiceMock.doesNextLinkExistInHeaders(any(HttpHeaders.class)))
                .thenReturn(false);

        List<BranchDTO> branches = branchService.getAllBranches(TEST_USERNAME, TEST_REPOSITORY_NAME);

        assertEquals(0, branches.size());

        verify(githubClientMock, times(1))
                .getRepositoryBranches(anyString(), anyString(), anyInt());
        verify(responseEntityMock, times(1)).getBody();
        verify(branchMapperMock, never()).branchToBranchDto(any(Branch.class));
        verify(githubNextLinkCheckerServiceMock, times(1))
                .doesNextLinkExistInHeaders(any(HttpHeaders.class));
    }

    @Test
    void getAllBranchesShouldWorksCorrectlyForLessThan100BranchesInTheRepository() {
        when(responseEntityMock.getBody()).thenReturn(createTestBranches(99));
        when(githubNextLinkCheckerServiceMock.doesNextLinkExistInHeaders(any(HttpHeaders.class)))
                .thenReturn(false);
        when(branchMapperMock.branchToBranchDto(any(Branch.class)))
                .thenReturn(new BranchDTO());

        List<BranchDTO> branches = branchService.getAllBranches(TEST_USERNAME, TEST_REPOSITORY_NAME);

        assertEquals(99, branches.size());

        verify(githubClientMock, times(1))
                .getRepositoryBranches(anyString(), anyString(), anyInt());
        verify(responseEntityMock, times(1)).getBody();
        verify(branchMapperMock, times(99)).branchToBranchDto(any(Branch.class));
        verify(githubNextLinkCheckerServiceMock, times(1))
                .doesNextLinkExistInHeaders(any(HttpHeaders.class));
    }

    @Test
    void getAllBranchesShouldWorksCorrectlyForMoreThan100BranchesInTheRepository() {
        when(responseEntityMock.getBody())
                .thenReturn(createTestBranches(100))
                .thenReturn(createTestBranches(100))
                .thenReturn(createTestBranches(34));
        when(githubNextLinkCheckerServiceMock.doesNextLinkExistInHeaders(any()))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(branchMapperMock.branchToBranchDto(any(Branch.class)))
                .thenReturn(new BranchDTO());

        List<BranchDTO> branches = branchService.getAllBranches(TEST_USERNAME, TEST_REPOSITORY_NAME);

        assertEquals(234, branches.size());

        verify(githubClientMock, times(3))
                .getRepositoryBranches(anyString(), anyString(), anyInt());
        verify(responseEntityMock, times(3)).getBody();
        verify(branchMapperMock, times(234))
                .branchToBranchDto(any(Branch.class));
        verify(githubNextLinkCheckerServiceMock, times(3))
                .doesNextLinkExistInHeaders(any(HttpHeaders.class));
    }

    @Test
    void getAllBranchesShouldWorksCorrectlyWithRepositoryDTOasArgument() {
        RepositoryDTO repositoryDTO = new RepositoryDTO();
        repositoryDTO.setName("Test repository name");
        repositoryDTO.setOwnerLogin("Test username");

        when(responseEntityMock.getBody()).thenReturn(createTestBranches(3));
        when(githubNextLinkCheckerServiceMock.doesNextLinkExistInHeaders(any(HttpHeaders.class)))
                .thenReturn(false);
        when(branchMapperMock.branchToBranchDto(any(Branch.class))).thenReturn(new BranchDTO());

        List<BranchDTO> branches = branchService.getAllBranches(repositoryDTO);

        assertEquals(3, branches.size());

        verify(githubClientMock, times(1))
                .getRepositoryBranches(anyString(), anyString(), anyInt());
        verify(responseEntityMock, times(1)).getBody();
        verify(branchMapperMock, times(3)).branchToBranchDto(any(Branch.class));
        verify(githubNextLinkCheckerServiceMock, times(1))
                .doesNextLinkExistInHeaders(any(HttpHeaders.class));
    }

    private Branch[] createTestBranches(int length) {
        Branch[] branches = new Branch[length];

        for (int i = 0; i < length; i++) {
            Commit commit = new Commit();
            commit.setSha(TEST_COMMIT_SHA);

            Branch branch = new Branch();
            branch.setName(TEST_BRANCH_NAME);
            branch.setCommit(commit);

            branches[i] = branch;
        }

        return branches;
    }
}
