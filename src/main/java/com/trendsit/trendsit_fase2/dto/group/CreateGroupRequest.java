package com.trendsit.trendsit_fase2.dto.group;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class CreateGroupRequest {

    @NotNull("Precisa por um nome no grupo")
    private String nome;

}
