package com.demoapp.springbootgithubapi.client.impl;

import com.demoapp.springbootgithubapi.client.GithubClient;
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

@Service
public class GithubClientImpl implements GithubClient {
    public static final String HTTP_HEADER_GITHUB_API_VERSION = "X-GitHub-Api-Version";
    public static final int PER_PAGE_DEFAULT = 100;
    private final RestTemplate restTemplate;

    public GithubClientImpl(
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
    public ResponseEntity<Repository[]> getUserRepositoriesByUsername(String username, int page) {
        try {
            ResponseEntity<Repository[]> responseEntity = restTemplate.getForEntity(
                    "/users/{username}/repos?per_page={perPage}&page={page}",
                    Repository[].class,
                    username,
                    PER_PAGE_DEFAULT,
                    page
            );

            if (responseEntity.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                throw new UserDoesNotExistException(username);
            }

            return responseEntity;
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new UserDoesNotExistException(username);
            }

            throw exception;
        }
    }

    @Override
    public ResponseEntity<Branch[]> getRepositoryBranches(String username, String repositoryName, int page) {
        try {
            ResponseEntity<Branch[]> responseEntity = restTemplate.getForEntity(
                    "/repos/{username}/{repository_name}/branches",
                    Branch[].class,
                    username,
                    repositoryName,
                    PER_PAGE_DEFAULT,
                    page
            );

            if (responseEntity.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                throw new RepositoryDoesNotExistException(username, repositoryName);
            }

            return responseEntity;
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new RepositoryDoesNotExistException(username, repositoryName);
            }

            throw exception;
        }
    }
}
