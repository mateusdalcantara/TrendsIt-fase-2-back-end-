package com.trendsit.trendsit_fase2.dto.group;

import com.trendsit.trendsit_fase2.dto.profile.SimpleProfileDTO;
import com.trendsit.trendsit_fase2.model.group.Group;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class GroupDTO {
    private final java.util.UUID id;
    private final String nome;
    private final LocalDateTime createdAt;
    private final SimpleProfileDTO criador;
    private final List<SimpleProfileDTO> membros;

    public GroupDTO(Group group) {
        this.id = group.getId();
        this.nome = group.getNome();
        this.createdAt = group.getCreatedAt();
        this.criador = new SimpleProfileDTO(group.getCriador());
        this.membros = group.getMembros().stream()
                .map(SimpleProfileDTO::new)
                .collect(Collectors.toList());
    }
}
