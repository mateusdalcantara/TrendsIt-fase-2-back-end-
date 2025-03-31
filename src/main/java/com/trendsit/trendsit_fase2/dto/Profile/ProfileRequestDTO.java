package com.trendsit.trendsit_fase2.dto.Profile;

import com.trendsit.trendsit_fase2.model.Profile;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para criação/atualização de perfis.
 */
@Getter
@Setter
public class ProfileRequestDTO {
    @NotBlank(message = "Username é obrigatório")
    private String username;
    private Integer idade;
    private String curso;

    public ProfileRequestDTO(Profile perfilAtualizado) {
        this.username = perfilAtualizado.getUsername(); // Get value from Profile
        this.idade = perfilAtualizado.getIdade();
        this.curso = perfilAtualizado.getCurso();
    }

    public ProfileRequestDTO() {

    }
}