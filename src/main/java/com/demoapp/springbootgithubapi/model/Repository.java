package com.demoapp.springbootgithubapi.model;

import lombok.Data;

@Data
public class Repository {
    private String name;
    private Boolean fork;
    private Owner owner;
}
