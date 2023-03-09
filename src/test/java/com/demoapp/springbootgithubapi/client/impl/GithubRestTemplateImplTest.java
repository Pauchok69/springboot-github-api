package com.demoapp.springbootgithubapi.client.impl;

import com.demoapp.springbootgithubapi.exception.RepositoryDoesNotExistException;
import com.demoapp.springbootgithubapi.exception.UserDoesNotExistException;
import com.demoapp.springbootgithubapi.model.Branch;
import com.demoapp.springbootgithubapi.model.Repository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GithubRestTemplateImplTest {
    public static final String GITHUB_RESPONSE_HEADER_LINK_WITH_NEXT_PAGE = "<https://api.github.com/user/50894/repos?per_page=100&page=2>; rel=\"next\", <https://api.github.com/user/50894/repos?per_page=100&page=5>; rel=\"last\"";
    public static final String GITHUB_RESPONSE_HEADER_LINK_WITHOUT_NEXT_PAGE = "<https://api.github.com/user/50894/repos?per_page=100&page=4>; rel=\"prev\", <https://api.github.com/user/50894/repos?per_page=100&page=1>; rel=\"first\"";
    public static final String TEST_USERNAME = "Test username";
    public static final String TEST_REPOSITORY = "Test Repository";

    private static final RestTemplate restTemplateMock = mock(RestTemplate.class);
    private static final ResponseEntity<Object> responseEntityMock = mock(ResponseEntity.class);
    private static GithubRestTemplateImpl githubRestTemplate;

    @BeforeAll
    static void beforeAll() {
        RestTemplateBuilder restTemplateBuilderMock = mock(RestTemplateBuilder.class);

        when(restTemplateBuilderMock.rootUri(anyString()))
                .thenReturn(restTemplateBuilderMock);
        when(restTemplateBuilderMock.defaultHeader(anyString(), anyString()))
                .thenReturn(restTemplateBuilderMock);
        when(restTemplateBuilderMock.build())
                .thenReturn(restTemplateMock);

        githubRestTemplate = new GithubRestTemplateImpl(
                restTemplateBuilderMock,
                "Test API Version",
                "Test API Token"
        );
    }

    @BeforeEach
    void setUp() {
        when(restTemplateMock.getForEntity(anyString(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(responseEntityMock);
        when(restTemplateMock.getForEntity(anyString(), any(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(responseEntityMock);
        when(responseEntityMock.getStatusCode()).thenReturn(HttpStatus.OK);
    }

    //getUserRepositoriesByUsername
    @Test
    void getUserRepositoriesByUsernameShouldReturnEmptyListWhenResponseBodyIsNull() {
        when(responseEntityMock.getBody()).thenReturn(null);

        Assertions.assertEquals(Collections.emptyList(), githubRestTemplate.getUserRepositoriesByUsername("test"));
    }

    @Test
    void getUserRepositoriesByUsernameShouldThrowsUserNotFoundExceptionWhenResponseHttpStatusIs404() {
        when(responseEntityMock.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

        Assertions.assertThrows(UserDoesNotExistException.class, () -> githubRestTemplate.getUserRepositoriesByUsername("Not existing user"));
    }

    @Test
    void getUserRepositoriesByUsernameShouldWorksCorrectForLessThan100Repositories() {
        HttpHeaders httpHeadersMock = mock(HttpHeaders.class);
        when(responseEntityMock.getBody()).thenReturn(new Repository[99]);
        when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
        when(httpHeadersMock.get(GithubRestTemplateImpl.HTTP_HEADER_GITHUB_API_VERSION)).thenReturn(null);

        Assertions.assertEquals(99, githubRestTemplate.getUserRepositoriesByUsername("User with less than 100 repositories").size());
    }

    @Test
    void getUserRepositoriesByUsernameShouldWorksCorrectForMoreThan100Repositories() {
        HttpHeaders httpHeadersMock = mock(HttpHeaders.class);
        when(responseEntityMock.getBody())
                .thenReturn(new Repository[100])
                .thenReturn(new Repository[50]);
        when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
        when(httpHeadersMock.get(GithubRestTemplateImpl.HTTP_HEADER_GITHUB_LINK))
                .thenReturn(List.of(GITHUB_RESPONSE_HEADER_LINK_WITH_NEXT_PAGE))
                .thenReturn(List.of(GITHUB_RESPONSE_HEADER_LINK_WITHOUT_NEXT_PAGE));

        Assertions.assertEquals(150, githubRestTemplate.getUserRepositoriesByUsername("User with 150 repositories").size());
    }

    //getRepositoryBranches()
    @Test
    void getRepositoryBranchesShouldReturnEmptyListWhenResponseBodyIsNull() {
        when(responseEntityMock.getBody()).thenReturn(null);

        Assertions.assertEquals(Collections.emptyList(), githubRestTemplate.getRepositoryBranches(TEST_USERNAME, "Not Existing Repository"));
    }

    @Test
    void getRepositoryBranchesShouldThrowsProperExceptionWhenResponseHttpStatusIs404() {
        when(responseEntityMock.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

        Assertions.assertThrows(
                RepositoryDoesNotExistException.class,
                () -> githubRestTemplate.getRepositoryBranches(TEST_USERNAME, TEST_REPOSITORY)
        );
    }

    @Test
    void getRepositoryBranchesShouldWorkCorrectForLessThan100Branches() {
        HttpHeaders httpHeadersMock = mock(HttpHeaders.class);
        when(responseEntityMock.getBody()).thenReturn(new Branch[99]);
        when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
        when(httpHeadersMock.get(GithubRestTemplateImpl.HTTP_HEADER_GITHUB_API_VERSION)).thenReturn(null);

        Assertions.assertEquals(99, githubRestTemplate.getRepositoryBranches(TEST_USERNAME, TEST_REPOSITORY).size());
    }

    @Test
    void getRepositoryBranchesShouldWorksCorrectForMoreThan100Repositories() {
        HttpHeaders httpHeadersMock = mock(HttpHeaders.class);
        when(responseEntityMock.getBody())
                .thenReturn(new Branch[100])
                .thenReturn(new Branch[50]);
        when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
        when(httpHeadersMock.get(GithubRestTemplateImpl.HTTP_HEADER_GITHUB_LINK))
                .thenReturn(List.of(GITHUB_RESPONSE_HEADER_LINK_WITH_NEXT_PAGE))
                .thenReturn(List.of(GITHUB_RESPONSE_HEADER_LINK_WITHOUT_NEXT_PAGE));

        Assertions.assertEquals(150, githubRestTemplate.getRepositoryBranches(TEST_USERNAME, TEST_REPOSITORY).size());
    }
}
