package com.demoapp.springbootgithubapi.model;

import lombok.Data;

import java.util.Set;

@Data
public class Repository {
    private String name;
    private Owner owner;
    private Set<Branch> branches;
}
