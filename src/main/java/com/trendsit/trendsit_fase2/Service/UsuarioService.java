package com.trendsit.trendsit_fase2.Service;

import com.trendsit.trendsit_fase2.Model.Usuario;
import com.trendsit.trendsit_fase2.Repository.UsuarioRepository;
import com.trendsit.trendsit_fase2.Utils.UsuarioUtils;
import com.trendsit.trendsit_fase2.dto.RegisterDTO;
import com.trendsit.trendsit_fase2.dto.UsuarioDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioUtils usuarioUtils;
    private final UsuarioRepository usuarioRepository;
    public UsuarioService(UsuarioUtils usuarioUtils, UsuarioRepository usuarioRepository) {
        this.usuarioUtils = usuarioUtils;
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioDTO> FindAll(){
        return usuarioRepository.findAll().stream().map(usuarioUtils::toDTO).collect(Collectors.toList());
    }

    public UsuarioDTO saveUser(RegisterDTO registerDTO){
        Usuario usuario = new Usuario();
        usuario.setNome(registerDTO.Nome());
        usuario.setEmail(registerDTO.Email());
        usuario.setCurso(registerDTO.Curso());
        usuario.setHash(registerDTO.Senha());
        usuario.setD_criacao(LocalDateTime.now());
        usuario.setRole("Aluno");
        usuarioRepository.save(usuario);
        return new UsuarioDTO(registerDTO.Nome(), usuario.getEmail(), usuario.getCurso());
    }





}
