package com.demoapp.springbootgithubapi.model;

import lombok.Data;

import java.util.Set;

@Data
public class Branch {
    private String name;
    private Set<Commit> commits;
}
