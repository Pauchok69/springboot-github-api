package com.demoapp.springbootgithubapi.controller;

import com.demoapp.springbootgithubapi.client.impl.GithubClientImpl;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.eclipse.jetty.http.HttpHeader;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {
    public static final int WIREMOCK_PORT = 7070;
    public static final String USERS_REPOSITORIES_PATH_TEMPLATE = "/users/{username}/repositories";
    public static final String GITHUB_USERS_REPOS_PATH = "/users/?.*/repos";
    public static final String GITHUB_BRANCHES_PATH = "/repos/?.*/?.*/branches";
    public static final String TEST_USER = "testUser";
    public static final String USER_WITHOUT_REPOSITORIES = "UserWithoutRepositories";
    public static final String USER_WITH_REPOSITORIES = "UserWithRepositories";
    private static WireMockServer wireMockServer;

    @Value("${app.github.api_version}")
    private String githubApiVersion;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(
                new WireMockConfiguration().port(WIREMOCK_PORT)
        );

        wireMockServer.start();
        WireMock.configureFor("localhost", WIREMOCK_PORT);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void getRepositoriesByUsername_shouldReturn404WhenUserNotFound() throws Exception {
        stubFor(WireMock.get(urlPathMatching(GITHUB_USERS_REPOS_PATH))
                .willReturn(
                        aResponse().withStatus(HttpStatus.NOT_FOUND.value())
                )
        );

        mockMvc.perform(MockMvcRequestBuilders.get(USERS_REPOSITORIES_PATH_TEMPLATE, TEST_USER).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("{\"status\":404,\"message\":\"User with username 'testUser' not found\"}"));

        verify(1, getRequestedFor(urlPathMatching(GITHUB_USERS_REPOS_PATH))
                .withHeader(HttpHeader.AUTHORIZATION.asString(), containing("Bearer"))
                .withHeader(GithubClientImpl.HTTP_HEADER_GITHUB_API_VERSION, equalTo(githubApiVersion)));
    }

    @Test
    void getRepositoriesByUsername_shouldReturn406ForAllNonJsonRequests() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(USERS_REPOSITORIES_PATH_TEMPLATE, TEST_USER).contentType(MediaType.APPLICATION_XML))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("{\"status\":406,\"message\":\"Content-Type 'application/xml' is not supported\"}")));

        verify(0, getRequestedFor(urlPathMatching(GITHUB_USERS_REPOS_PATH))
                .withHeader(HttpHeader.AUTHORIZATION.asString(), containing("Bearer"))
                .withHeader(GithubClientImpl.HTTP_HEADER_GITHUB_API_VERSION, equalTo(githubApiVersion)));
        verify(0, getRequestedFor(urlPathMatching(GITHUB_BRANCHES_PATH))
                .withHeader(HttpHeader.AUTHORIZATION.asString(), containing("Bearer"))
                .withHeader(GithubClientImpl.HTTP_HEADER_GITHUB_API_VERSION, equalTo(githubApiVersion)));
    }

    @Test
    void getRepositoriesByUsername_shouldReturnEmptyJsonArrayForUserWithoutRepositories() throws Exception {
        stubFor(WireMock.get(urlPathMatching(GITHUB_USERS_REPOS_PATH))
                .willReturn(okJson("[]"))
        );

        mockMvc.perform(MockMvcRequestBuilders.get(USERS_REPOSITORIES_PATH_TEMPLATE, USER_WITHOUT_REPOSITORIES)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("[]")));

        verify(1, getRequestedFor(urlPathMatching(GITHUB_USERS_REPOS_PATH))
                .withHeader(HttpHeader.AUTHORIZATION.asString(), containing("Bearer"))
                .withHeader(GithubClientImpl.HTTP_HEADER_GITHUB_API_VERSION, equalTo(githubApiVersion)));
        verify(0, getRequestedFor(urlPathMatching(GITHUB_BRANCHES_PATH))
                .withHeader(HttpHeader.AUTHORIZATION.asString(), containing("Bearer"))
                .withHeader(GithubClientImpl.HTTP_HEADER_GITHUB_API_VERSION, equalTo(githubApiVersion)));
    }

    @Test
    void getRepositoriesByUsername_shouldReturnCorrectResponseForUserWithRepositories() throws Exception {
        stubFor(WireMock.get(urlPathMatching(GITHUB_USERS_REPOS_PATH))
                .willReturn(okJson("[{\"name\": \"TestRepository\", \"fork\": false, \"owner\": {\"login\": \"TestUser\"}}]"))
        );

        stubFor(WireMock.get(urlPathMatching(GITHUB_BRANCHES_PATH))
                .willReturn(okJson("[{\"name\": \"main\", \"commit\": {\"sha\": \"6ea49f98c2460cefae79aff38ddb9062945e4ebc\"}}]"))
        );

        mockMvc.perform(MockMvcRequestBuilders.get(USERS_REPOSITORIES_PATH_TEMPLATE, USER_WITH_REPOSITORIES)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("[{\"name\":\"TestRepository\",\"ownerLogin\":\"TestUser\",\"branches\":[{\"name\":\"main\",\"lastCommitSha\":\"6ea49f98c2460cefae79aff38ddb9062945e4ebc\"}]}]")));

        verify(1, getRequestedFor(urlPathMatching(GITHUB_USERS_REPOS_PATH))
                .withHeader(HttpHeader.AUTHORIZATION.asString(), containing("Bearer"))
                .withHeader(GithubClientImpl.HTTP_HEADER_GITHUB_API_VERSION, equalTo(githubApiVersion))
                .withQueryParam("page", equalTo("1")));
        verify(1, getRequestedFor(urlPathMatching(GITHUB_BRANCHES_PATH))
                .withHeader(HttpHeader.AUTHORIZATION.asString(), containing("Bearer"))
                .withHeader(GithubClientImpl.HTTP_HEADER_GITHUB_API_VERSION, equalTo(githubApiVersion))
                .withQueryParam("page", equalTo("1")));
    }
}


