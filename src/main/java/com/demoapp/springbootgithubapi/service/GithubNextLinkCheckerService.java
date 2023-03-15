package com.demoapp.springbootgithubapi.service;

import org.springframework.http.ResponseEntity;

public interface GithubNextLinkCheckerService {
    boolean doesNextLinkExistInHeader(ResponseEntity<Object> responseEntity);
}
