package com.trendsit.trendsit_fase2.util;

import com.trendsit.trendsit_fase2.model.Usuario;
import com.trendsit.trendsit_fase2.dto.UsuarioDTO;
import com.trendsit.trendsit_fase2.service.Role;
import org.springframework.stereotype.Service;

@Service
public class UsuarioUtils {

    public UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCurso(),
                usuario.getRole(Role.USUARIO).name(),
                usuario.getD_criacao());
    }
}
