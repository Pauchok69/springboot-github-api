package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.service.GithubNextLinkCheckerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GithubNextLinkCheckerServiceImpl<T> implements GithubNextLinkCheckerService<T> {
    @Override
    public boolean doesNextLinkExistInHeader(ResponseEntity<T> responseEntity) {
        List<String> linkHeader = responseEntity.getHeaders().get(HttpHeaders.LINK);

        return linkHeader != null
                && !linkHeader.isEmpty()
                && linkHeader.get(0).contains("rel=\"next\"");
    }
}
