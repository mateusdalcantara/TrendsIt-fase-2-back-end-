package com.trendsit.trendsit_fase2.Utils;

import com.trendsit.trendsit_fase2.Model.Usuario;
import com.trendsit.trendsit_fase2.dto.UsuarioDTO;
import org.springframework.stereotype.Service;

@Service
public class UsuarioUtils {

    public UsuarioDTO toDTO (Usuario usuario){
        return new UsuarioDTO(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCurso());}}
