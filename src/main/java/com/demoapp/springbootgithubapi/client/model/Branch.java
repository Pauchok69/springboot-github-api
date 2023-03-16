package com.demoapp.springbootgithubapi.client.model;

import lombok.Data;

@Data
public class Branch {
    private String name;
    private Commit commit;
}
