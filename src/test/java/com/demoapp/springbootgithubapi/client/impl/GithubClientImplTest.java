package com.demoapp.springbootgithubapi.client.impl;

import com.demoapp.springbootgithubapi.client.exception.RepositoryDoesNotExistException;
import com.demoapp.springbootgithubapi.client.exception.UserDoesNotExistException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubClientImplTest {
    public static final String TEST_USERNAME = "Test username";
    public static final String TEST_REPOSITORY = "Test Repository";
    public static final String NOT_EXISTING_USER = "Not Existing User";
    public static final String NOT_EXISTING_REPOSITORY = "Not Existing Repository";

    @Mock
    private ResponseEntity<Object> responseEntityMock;
    private RestTemplate restTemplateMock;
    private static GithubClientImpl githubClient;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder restTemplateBuilderMock = mock(RestTemplateBuilder.class);
        restTemplateMock = mock(RestTemplate.class);

        when(restTemplateBuilderMock.rootUri(anyString()))
                .thenReturn(restTemplateBuilderMock);
        when(restTemplateBuilderMock.defaultHeader(anyString(), any()))
                .thenReturn(restTemplateBuilderMock);
        when(restTemplateBuilderMock.build())
                .thenReturn(restTemplateMock);

        githubClient = new GithubClientImpl(
                restTemplateBuilderMock,
                "Test API Version",
                "Test API Token"
        );

        lenient().when(restTemplateMock.getForEntity(anyString(), any(), any(Object.class)))
                .thenReturn(responseEntityMock);
    }

    //getUserRepositoriesByUsername
    @Test
    void getUserRepositoriesByUsernameShouldReturnResponseEntity() {
        when(responseEntityMock.getStatusCode()).thenReturn(HttpStatus.OK);
        Assertions.assertInstanceOf(ResponseEntity.class, githubClient.getUserRepositoriesByUsername(TEST_USERNAME, 1));
        verify(restTemplateMock, times(1)).getForEntity(anyString(), any(), any(Object.class));
        verify(responseEntityMock, times(1)).getStatusCode();
    }

    @Test
    void getUserRepositoriesByUsernameShouldThrowUserNotFoundExceptionWhenResponseHttpStatusIs404() {
        when(responseEntityMock.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

        Assertions.assertThrows(UserDoesNotExistException.class, () -> githubClient.getUserRepositoriesByUsername(NOT_EXISTING_USER, 1));
        verify(restTemplateMock, times(1)).getForEntity(anyString(), any(), any(Object.class));
        verify(responseEntityMock, times(1)).getStatusCode();
    }

    @Test
    void getUserRepositoriesByUsernameShouldThrowUserNotFoundExceptionWhenHttpClientErrorExceptionOccurs() {
        when(restTemplateMock.getForEntity(anyString(), any(), any(Object.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Assertions.assertThrows(UserDoesNotExistException.class, () -> githubClient.getUserRepositoriesByUsername(NOT_EXISTING_USER, 1));
        verify(restTemplateMock, times(1)).getForEntity(anyString(), any(), any(Object.class));
    }

    //getRepositoryBranches()
    @Test
    void getRepositoryBranchesShouldReturnResponseEntity() {
        when(responseEntityMock.getStatusCode()).thenReturn(HttpStatus.OK);
        Assertions.assertInstanceOf(ResponseEntity.class, githubClient.getRepositoryBranches("test", "testRepository", 1));
        verify(restTemplateMock, times(1)).getForEntity(anyString(), any(), any(Object.class));
        verify(responseEntityMock, times(1)).getStatusCode();
    }

    @Test
    void getRepositoryBranchesShouldThrowsProperExceptionWhenResponseHttpStatusIs404() {
        when(responseEntityMock.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

        Assertions.assertThrows(
                RepositoryDoesNotExistException.class,
                () -> githubClient.getRepositoryBranches(TEST_USERNAME, TEST_REPOSITORY, 1)
        );
        verify(restTemplateMock, times(1)).getForEntity(anyString(), any(), any(Object.class));
        verify(responseEntityMock, times(1)).getStatusCode();
    }

    @Test
    void getRepositoryBranchesShouldThrowRepositoryNotFoundExceptionWhenHttpClientErrorExceptionOccurs() {
        when(restTemplateMock.getForEntity(anyString(), any(), any(Object.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Assertions.assertThrows(
                RepositoryDoesNotExistException.class,
                () -> githubClient.getRepositoryBranches(TEST_USERNAME, NOT_EXISTING_REPOSITORY, 1)
        );
        verify(restTemplateMock, times(1)).getForEntity(anyString(), any(), any(Object.class));
    }
//
//    @Test
//    void getUserRepositoriesByUsernameShouldWorkCorrectForLessThan100Repositories() {
//        HttpHeaders httpHeadersMock = mock(HttpHeaders.class);
//        when(responseEntityMock.getBody()).thenReturn(new Repository[99]);
//        when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
//        when(httpHeadersMock.get(GithubClientImpl.HTTP_HEADER_GITHUB_API_VERSION)).thenReturn(null);
//
//        Assertions.assertEquals(99, githubRestTemplate.getUserRepositoriesByUsername("User with less than 100 repositories").size());
//    }
//
//    @Test
//    void getUserRepositoriesByUsernameShouldWorkCorrectForMoreThan100Repositories() {
//        HttpHeaders httpHeadersMock = mock(HttpHeaders.class);
//        when(responseEntityMock.getBody())
//                .thenReturn(new Repository[100])
//                .thenReturn(new Repository[50]);
//        when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
//        when(httpHeadersMock.get(GithubClientImpl.HTTP_HEADER_GITHUB_LINK))
//                .thenReturn(List.of(GITHUB_RESPONSE_HEADER_LINK_WITH_NEXT_PAGE))
//                .thenReturn(List.of(GITHUB_RESPONSE_HEADER_LINK_WITHOUT_NEXT_PAGE));
//
//        Assertions.assertEquals(150, githubRestTemplate.getUserRepositoriesByUsername("User with 150 repositories").size());
//    }
//

//
//    @Test
//    void getRepositoryBranchesShouldReturnEmptyListWhenResponseBodyIsNull() {
//        when(responseEntityMock.getBody()).thenReturn(null);
//
//        Assertions.assertEquals(Collections.emptyList(), githubRestTemplate.getRepositoryBranches(TEST_USERNAME, NOT_EXISTING_REPOSITORY));
//    }
//

//
//    @Test
//    void getRepositoryBranchesShouldWorkCorrectForLessThan100Branches() {
//        HttpHeaders httpHeadersMock = mock(HttpHeaders.class);
//        when(responseEntityMock.getBody()).thenReturn(new Branch[99]);
//        when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
//        when(httpHeadersMock.get(GithubClientImpl.HTTP_HEADER_GITHUB_API_VERSION)).thenReturn(null);
//
//        Assertions.assertEquals(99, githubRestTemplate.getRepositoryBranches(TEST_USERNAME, TEST_REPOSITORY).size());
//    }
//
//    @Test
//    void getRepositoryBranchesShouldWorkCorrectForMoreThan100Repositories() {
//        HttpHeaders httpHeadersMock = mock(HttpHeaders.class);
//        when(responseEntityMock.getBody())
//                .thenReturn(new Branch[100])
//                .thenReturn(new Branch[50]);
//        when(responseEntityMock.getHeaders()).thenReturn(httpHeadersMock);
//        when(httpHeadersMock.get(GithubClientImpl.HTTP_HEADER_GITHUB_LINK))
//                .thenReturn(List.of(GITHUB_RESPONSE_HEADER_LINK_WITH_NEXT_PAGE))
//                .thenReturn(List.of(GITHUB_RESPONSE_HEADER_LINK_WITHOUT_NEXT_PAGE));
//
//        Assertions.assertEquals(150, githubRestTemplate.getRepositoryBranches(TEST_USERNAME, TEST_REPOSITORY).size());
//    }
//
}
