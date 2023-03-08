package com.demoapp.springbootgithubapi.payload;

import lombok.Data;

import java.util.Set;

@Data
public class RepositoryDTO {
    private String name;
    private String ownerLogin;
    private Set<BranchDTO> branches;

}
