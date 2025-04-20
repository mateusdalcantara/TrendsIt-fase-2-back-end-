package com.trendsit.trendsit_fase2.config;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.repository.group.GroupRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GroupSecurity {
    private final GroupRepository repo;

    public GroupSecurity(GroupRepository repo) {
        this.repo = repo;
    }

    // retorna true se o usuário for o criador daquele grupo
    public boolean isGroupCreator(UUID groupId, Profile user) {
        return repo.findById(groupId)
                .map(g -> g.getCriador().getId().equals(user.getId()))
                .orElse(false);
    }
}