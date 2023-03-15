package com.demoapp.springbootgithubapi.service;

import org.springframework.http.ResponseEntity;

public interface GithubNextLinkCheckerService<T> {
    boolean doesNextLinkExistInHeader(ResponseEntity<T> responseEntity);
}
