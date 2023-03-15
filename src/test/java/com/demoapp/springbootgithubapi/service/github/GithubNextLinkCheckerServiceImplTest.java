package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.service.GithubNextLinkCheckerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubNextLinkCheckerServiceImplTest {
    public static final String GITHUB_RESPONSE_HEADER_LINK_WITH_NEXT_PAGE = "<https://api.github.com/user/50894/repos?per_page=100&page=2>; rel=\"next\", <https://api.github.com/user/50894/repos?per_page=100&page=5>; rel=\"last\"";
    public static final String GITHUB_RESPONSE_HEADER_LINK_WITHOUT_NEXT_PAGE = "<https://api.github.com/user/50894/repos?per_page=100&page=4>; rel=\"prev\", <https://api.github.com/user/50894/repos?per_page=100&page=1>; rel=\"first\"";
    @Mock
    private HttpHeaders httpHeadersMock;
    private GithubNextLinkCheckerService nextLinkCheckerService;

    @BeforeEach
    void setUp() {
        nextLinkCheckerService = new GithubNextLinkCheckerServiceImpl();
    }

    @Test
    void doesNextLinkExistInHeaderShouldReturnFalseWhenLinkHeaderDoesNotExist() {
        when(httpHeadersMock.get(HttpHeaders.LINK)).thenReturn(null);
        assertFalse(nextLinkCheckerService.doesNextLinkExistInHeaders(httpHeadersMock));
        verify(httpHeadersMock, times(1)).get(anyString());
    }

    @Test
    void doesNextLinkExistInHeaderShouldReturnFalseWhenLinkHeaderExistsButNotContainNeededString() {
        when(httpHeadersMock.get(HttpHeaders.LINK)).thenReturn(List.of(GITHUB_RESPONSE_HEADER_LINK_WITHOUT_NEXT_PAGE));
        assertFalse(nextLinkCheckerService.doesNextLinkExistInHeaders(httpHeadersMock));
        verify(httpHeadersMock, times(1)).get(anyString());
    }

    @Test
    void doesNextLinkExistInHeaderShouldReturnTrueWhenLinkHeaderExistsAndContainsNeededString() {
        when(httpHeadersMock.get(HttpHeaders.LINK)).thenReturn(List.of(GITHUB_RESPONSE_HEADER_LINK_WITH_NEXT_PAGE));
        assertTrue(nextLinkCheckerService.doesNextLinkExistInHeaders(httpHeadersMock));
        verify(httpHeadersMock, times(1)).get(anyString());
    }
}