package com.demoapp.springbootgithubapi.mapper;

import com.demoapp.springbootgithubapi.client.model.Branch;
import com.demoapp.springbootgithubapi.payload.BranchDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    @Mapping(target = "lastCommitSha", source = "entity.commit.sha")
    BranchDTO branchToBranchDto(Branch entity);
}
