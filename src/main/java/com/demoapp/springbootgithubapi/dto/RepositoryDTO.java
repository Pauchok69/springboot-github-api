package com.demoapp.springbootgithubapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class RepositoryDTO {
    private String name;
    private String ownerLogin;
    private List<BranchDTO> branches;
}
