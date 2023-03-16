package com.demoapp.springbootgithubapi.mapper;

import com.demoapp.springbootgithubapi.client.model.Repository;
import com.demoapp.springbootgithubapi.payload.RepositoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RepositoryMapper {
    @Mapping(target = "ownerLogin", source = "entity.owner.login")
    RepositoryDTO repositoryToRepositoryDto(Repository entity);
}
