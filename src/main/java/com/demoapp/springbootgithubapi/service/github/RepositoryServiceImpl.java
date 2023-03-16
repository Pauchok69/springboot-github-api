package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.client.GithubClient;
import com.demoapp.springbootgithubapi.mapper.RepositoryMapper;
import com.demoapp.springbootgithubapi.model.Repository;
import com.demoapp.springbootgithubapi.payload.RepositoryDTO;
import com.demoapp.springbootgithubapi.service.BranchService;
import com.demoapp.springbootgithubapi.service.GithubNextLinkCheckerService;
import com.demoapp.springbootgithubapi.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RepositoryServiceImpl implements RepositoryService {
    private final GithubClient githubClient;
    private final RepositoryMapper repositoryMapper;
    private final BranchService branchService;
    private final GithubNextLinkCheckerService githubNextLinkCheckerService;

    @Override
    public List<RepositoryDTO> getAllRepositoriesByUsername(String username, boolean includeForked) {
        Stream<Repository> repositoryStream = doGetAllRepositoriesByUsername(username, 1)
                .stream();

        if (!includeForked) {
            repositoryStream = repositoryStream.filter(repository -> !repository.getFork());
        }
        List<RepositoryDTO> repositoryDTOs = repositoryStream
                .map(repositoryMapper::repositoryToRepositoryDto)
                .toList();

        repositoryDTOs
                .stream()
                .parallel()
                .forEach(r -> r.setBranches(branchService.getAllBranches(r)));

        return repositoryDTOs;
    }

    private List<Repository> doGetAllRepositoriesByUsername(String username, int page) {
        ResponseEntity<Repository[]> responseEntity = githubClient.getUserRepositoriesByUsername(username, page);
        Repository[] body = responseEntity.getBody();

        if (body == null) {
            return Collections.emptyList();
        }
        List<Repository> repositories = new ArrayList<>(Arrays.asList(body));

        if (githubNextLinkCheckerService.doesNextLinkExistInHeaders(responseEntity.getHeaders())) {
            repositories.addAll(doGetAllRepositoriesByUsername(username, ++page));
        }

        return repositories;
    }
}
