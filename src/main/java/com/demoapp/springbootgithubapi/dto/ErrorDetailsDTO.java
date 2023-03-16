package com.demoapp.springbootgithubapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorDetailsDTO {
    private Integer status;
    private String message;
}
