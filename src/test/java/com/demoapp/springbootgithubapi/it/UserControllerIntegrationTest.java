package com.demoapp.springbootgithubapi.it;

import com.demoapp.springbootgithubapi.controller.UserController;
import com.demoapp.springbootgithubapi.exception.UserDoesNotExistException;
import com.demoapp.springbootgithubapi.payload.BranchDTO;
import com.demoapp.springbootgithubapi.payload.RepositoryDTO;
import com.demoapp.springbootgithubapi.service.github.RepositoryServiceImpl;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@WebMvcTest(UserController.class)
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepositoryServiceImpl repositoryServiceMock;

    private static URI buildUri(String username) {
        return URI.create("/users/" + username + "/repositories");
    }

    @Test
    void getRepositoriesByUsernameShouldReturnCorrectMessageForNotExistingUser() throws Exception {
        when(repositoryServiceMock.getAllNonForkedRepositoriesByUsername(anyString()))
                .thenThrow(new UserDoesNotExistException("not-existing-user"));

        mockMvc.perform(MockMvcRequestBuilders.get(buildUri("not-existing-user")))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("{\"status\":404,\"message\":\"User with username 'not-existing-user' not found\"}")));
    }

    @Test
    void getRepositoriesByUsernameShouldReturnCorrectMessageForContentTypeApplicationXml() throws Exception {
        verify(repositoryServiceMock, never()).getAllNonForkedRepositoriesByUsername(anyString());

        mockMvc.perform(MockMvcRequestBuilders.get(buildUri("anyUser")).contentType(MediaType.APPLICATION_XML))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("{\"status\":406,\"message\":\"Content-Type 'application/xml' is not supported\"}")));
    }

    @Test
    void getRepositoriesByUsernameShouldReturnEmptyListForUserWithoutRepositories() throws Exception {
        when(repositoryServiceMock.getAllNonForkedRepositoriesByUsername(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(buildUri("UserWithoutRepositories")))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("[]")));
    }

    @Test
    void getRepositoriesByUsernameShouldReturnCorrectJsonForUserWithRepositories() throws Exception {
        String username = "User with repositories";

        BranchDTO branchDTO1 = new BranchDTO();
        branchDTO1.setName("main");
        branchDTO1.setLastCommitSha("Test sha");

        BranchDTO branchDTO2 = new BranchDTO();
        branchDTO2.setName("dev");
        branchDTO2.setLastCommitSha("Test sha");

        RepositoryDTO repositoryDTO = new RepositoryDTO();
        repositoryDTO.setName("Repository 1");
        repositoryDTO.setOwnerLogin(username);
        repositoryDTO.setBranches(List.of(branchDTO1, branchDTO2));

        when(repositoryServiceMock.getAllNonForkedRepositoriesByUsername(anyString()))
                .thenReturn(List.of(repositoryDTO));

        mockMvc.perform(MockMvcRequestBuilders.get(buildUri("UserWithoutRepositories")))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("[{\"name\":\"Repository 1\",\"ownerLogin\":\"User with repositories\",\"branches\":[{\"name\":\"main\",\"lastCommitSha\":\"Test sha\"},{\"name\":\"dev\",\"lastCommitSha\":\"Test sha\"}]}]")));
    }
}
