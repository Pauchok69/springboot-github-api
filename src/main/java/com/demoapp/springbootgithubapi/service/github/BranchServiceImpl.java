package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.client.GithubClient;
import com.demoapp.springbootgithubapi.mapper.BranchMapper;
import com.demoapp.springbootgithubapi.client.model.Branch;
import com.demoapp.springbootgithubapi.dto.BranchDTO;
import com.demoapp.springbootgithubapi.dto.RepositoryDTO;
import com.demoapp.springbootgithubapi.service.BranchService;
import com.demoapp.springbootgithubapi.service.GithubNextLinkCheckerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final GithubClient githubClient;
    private final BranchMapper branchMapper;
    private final GithubNextLinkCheckerService githubNextLinkCheckerService;

    @Override
    public List<BranchDTO> getAllBranches(RepositoryDTO repositoryDTO) {
        return getAllBranches(repositoryDTO.getOwnerLogin(), repositoryDTO.getName());
    }

    @Override
    public List<BranchDTO> getAllBranches(String username, String repositoryName) {
        List<Branch> repositoryBranches = doGetAllBranches(username, repositoryName, 1);

        return repositoryBranches
                .stream()
                .map(branchMapper::branchToBranchDto)
                .toList();
    }

    private List<Branch> doGetAllBranches(String username, String repositoryName, int page) {
        ResponseEntity<Branch[]> responseEntity = githubClient.getRepositoryBranches(username, repositoryName, page);
        Branch[] body = responseEntity.getBody();

        if (body == null) {
            return Collections.emptyList();
        }
        List<Branch> branches = new ArrayList<>(Arrays.asList(body));

        if (githubNextLinkCheckerService.doesNextLinkExistInHeaders(responseEntity.getHeaders())) {
            branches.addAll(doGetAllBranches(username, repositoryName, ++page));
        }

        return branches;
    }
}
