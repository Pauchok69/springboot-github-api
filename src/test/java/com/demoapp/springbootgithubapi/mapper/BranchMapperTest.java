package com.demoapp.springbootgithubapi.mapper;

import com.demoapp.springbootgithubapi.client.model.Branch;
import com.demoapp.springbootgithubapi.client.model.Commit;
import com.demoapp.springbootgithubapi.payload.BranchDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BranchMapperTest {
    @Spy
    private BranchMapper branchMapper = Mappers.getMapper(BranchMapper.class);

    @Test
    void branchToBranchDtoShouldWorkAsExpected() {
        Commit commit = new Commit();
        commit.setSha("Test Sha");

        Branch branch = new Branch();
        branch.setName("Test name");
        branch.setCommit(commit);

        BranchDTO branchDTO = branchMapper.branchToBranchDto(branch);

        assertEquals(branchDTO.getName(), branch.getName());
        assertEquals(branchDTO.getLastCommitSha(), branch.getCommit().getSha());
        verify(branchMapper, times(1)).branchToBranchDto(any(Branch.class));
    }
}