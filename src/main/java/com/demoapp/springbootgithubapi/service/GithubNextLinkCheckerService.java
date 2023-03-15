package com.demoapp.springbootgithubapi.service;

import org.springframework.http.HttpHeaders;

public interface GithubNextLinkCheckerService {
    boolean doesNextLinkExistInHeaders(HttpHeaders httpHeaders);
}
