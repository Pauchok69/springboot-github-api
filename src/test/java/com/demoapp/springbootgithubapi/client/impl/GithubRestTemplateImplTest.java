package com.demoapp.springbootgithubapi.client.impl;

import com.demoapp.springbootgithubapi.exception.UserDoesNotExistException;
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
                "2022-11-28",
                "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"
        );
    }

    @BeforeEach
    void setUp() {
        when(restTemplateMock.getForEntity(anyString(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(responseEntityMock);
        when(responseEntityMock.getStatusCode()).thenReturn(HttpStatus.OK);
    }

    @Test
    void getUserRepositoriesByUsernameShouldReturnEmptyListWhenResponseBodyIsNull() {
        when(responseEntityMock.getBody()).thenReturn(null);

        Assertions.assertEquals(Collections.emptyList(), githubRestTemplate.getUserRepositoriesByUsername("test"));
    }

    @Test
    void getUserRepositoriesByUsernameShouldThrowsUserNotFoundExceptionWhenResponseHttpStatusIs404() {
        when(responseEntityMock.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

        Assertions.assertThrows(UserDoesNotExistException.class, () -> githubRestTemplate.getUserRepositoriesByUsername("Not existable user"));
    }

    @Test
    void getUserRepositoriesByUsernameShouldWorksCorrectForLessThan100Repositories() {
        HttpHeaders httpHeadersMock = mock(HttpHeaders.class);
        when(responseEntityMock.getBody()).thenReturn(new Repository[99]);
        when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
        when(httpHeadersMock.get(GithubRestTemplateImpl.HTTP_HEADER_GITHUB_API_VERSION)).thenReturn(null);

        Assertions.assertEquals(99, githubRestTemplate.getUserRepositoriesByUsername("User with less than 100 repositories").size());
        githubRestTemplate.getUserRepositoriesByUsername("User with less than 100 repositories");
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
        githubRestTemplate.getUserRepositoriesByUsername("User with less than 100 repositories");
    }
}