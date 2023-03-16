package com.demoapp.springbootgithubapi.dto;

import lombok.Data;

@Data
public class BranchDTO {
    private String name;
    private String lastCommitSha;
}
