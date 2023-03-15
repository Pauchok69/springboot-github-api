package com.demoapp.springbootgithubapi.service.github;

import com.demoapp.springbootgithubapi.service.GithubNextLinkCheckerService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GithubNextLinkCheckerServiceImpl implements GithubNextLinkCheckerService {
    @Override
    public boolean doesNextLinkExistInHeaders(HttpHeaders httpHeaders) {
        List<String> linkHeader = httpHeaders.get(HttpHeaders.LINK);

        return linkHeader != null
                && !linkHeader.isEmpty()
                && linkHeader.get(0).contains("rel=\"next\"");
    }
}
