package com.demoapp.springbootgithubapi.client.impl;

import com.demoapp.springbootgithubapi.client.GithubRestTemplate;
import com.demoapp.springbootgithubapi.exception.RepositoryDoesNotExistException;
import com.demoapp.springbootgithubapi.exception.UserDoesNotExistException;
import com.demoapp.springbootgithubapi.model.Branch;
import com.demoapp.springbootgithubapi.model.Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GithubRestTemplateImpl implements GithubRestTemplate {
    public static final String HTTP_HEADER_GITHUB_API_VERSION = "X-GitHub-Api-Version";
    public static final String HTTP_HEADER_GITHUB_LINK = "Link";
    public static final int PER_PAGE_DEFAULT = 100;
    private final RestTemplate restTemplate;

    public GithubRestTemplateImpl(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${app.github.api_version}") String githubApiVersion,
            @Value("${app.github.api_token}") String githubApiToken
    ) {
        this.restTemplate = restTemplateBuilder
                .rootUri("https://api.github.com")
                .defaultHeader(HTTP_HEADER_GITHUB_API_VERSION, githubApiVersion)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubApiToken)
                .build();
    }

    @Override
    public List<Repository> getUserRepositoriesByUsername(String username) {
        int page = 1;
        List<Repository> repositories = new ArrayList<>();
        List<String> linkHeader;

        try {
            do {
                ResponseEntity<Repository[]> responseEntity = doGetUserRepositoriesByUsername(username, page);

                if (responseEntity.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                    throw new UserDoesNotExistException(username);
                }
                Repository[] body = responseEntity.getBody();

                if (body == null) {
                    break;
                }
                repositories.addAll(Arrays.asList(body));
                linkHeader = responseEntity.getHeaders().get(HTTP_HEADER_GITHUB_LINK);

                page++;
            } while (hasNextPage(linkHeader));
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new UserDoesNotExistException(username);
            }
            throw exception;
        }

        return repositories;
    }

    private ResponseEntity<Repository[]> doGetUserRepositoriesByUsername(String username, int page) {
        return restTemplate.getForEntity(
                "/users/{username}/repos?per_page={perPage}&page={page}",
                Repository[].class,
                username,
                PER_PAGE_DEFAULT,
                page
        );
    }

    @Override
    public List<Branch> getRepositoryBranches(String username, String repositoryName) {
        int page = 1;
        List<Branch> branches = new ArrayList<>();
        List<String> linkHeader;

        try {
            do {
                ResponseEntity<Branch[]> responseEntity = doGetRepositoryBranches(username, repositoryName, page);

                if (responseEntity.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                    throw new RepositoryDoesNotExistException(username, repositoryName);
                }
                Branch[] body = responseEntity.getBody();

                if (body == null) {
                    break;
                }
                branches.addAll(Arrays.asList(body));
                linkHeader = responseEntity.getHeaders().get(HTTP_HEADER_GITHUB_LINK);

                page++;
            } while (hasNextPage(linkHeader));
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new RepositoryDoesNotExistException(username, repositoryName);
            }

            throw exception;
        }

        return branches;
    }

    private ResponseEntity<Branch[]> doGetRepositoryBranches(String username, String repositoryName, int page) {
        return restTemplate.getForEntity(
                "/repos/{username}/{repository_name}/branches",
                Branch[].class,
                username,
                repositoryName,
                PER_PAGE_DEFAULT,
                page
        );
    }


    private static boolean hasNextPage(List<String> linkHeader) {
        return linkHeader != null
                && !linkHeader.isEmpty()
                && linkHeader.get(0).contains("rel=\"next\"");
    }

}
