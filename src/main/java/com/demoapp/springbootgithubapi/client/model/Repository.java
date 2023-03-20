package com.demoapp.springbootgithubapi.client.model;

import lombok.Data;

@Data
public class Repository {
    private String name;
    private Boolean forked;
    private Owner owner;
}
