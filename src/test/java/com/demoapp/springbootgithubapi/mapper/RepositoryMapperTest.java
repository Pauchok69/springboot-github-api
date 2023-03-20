package com.demoapp.springbootgithubapi.mapper;

import com.demoapp.springbootgithubapi.client.model.Owner;
import com.demoapp.springbootgithubapi.client.model.Repository;
import com.demoapp.springbootgithubapi.dto.RepositoryDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RepositoryMapperTest {
    @Spy
    private RepositoryMapper repositoryMapper = Mappers.getMapper(RepositoryMapper.class);

    @Test
    void repositoryToRepositoryDtoShouldWorkAsExpected() {
        Owner owner = new Owner();
        owner.setLogin("Test username");

        Repository repository = new Repository();
        repository.setForked(false);
        repository.setName("Test Name");
        repository.setOwner(owner);

        RepositoryDTO repositoryDTO = repositoryMapper.repositoryToRepositoryDto(repository);

        assertEquals(repositoryDTO.getName(), repository.getName());
        assertEquals(repositoryDTO.getOwnerLogin(), repository.getOwner().getLogin());
        assertNull(repositoryDTO.getBranches());

        verify(repositoryMapper, times(1)).repositoryToRepositoryDto(any(Repository.class));
    }
}