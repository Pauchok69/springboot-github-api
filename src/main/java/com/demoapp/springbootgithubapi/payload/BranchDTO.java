package com.demoapp.springbootgithubapi.payload;

import lombok.Data;

@Data
public class BranchDTO {
    private String name;
    private String lastCommitSha;
}
