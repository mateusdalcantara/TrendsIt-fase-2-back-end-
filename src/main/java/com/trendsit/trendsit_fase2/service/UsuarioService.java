package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.exception.UsuarioJaExisteException;
import com.trendsit.trendsit_fase2.exception.UsuarioNaoEncontradoException;
import com.trendsit.trendsit_fase2.model.Usuario;
import com.trendsit.trendsit_fase2.repository.UsuarioRepository;
import com.trendsit.trendsit_fase2.util.UsuarioUtils;
import com.trendsit.trendsit_fase2.dto.RegisterDTO;
import com.trendsit.trendsit_fase2.dto.UsuarioDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioUtils usuarioUtils;
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioUtils usuarioUtils, UsuarioRepository usuarioRepository) {
        this.usuarioUtils = usuarioUtils;
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioDTO> FindAll() {
        return usuarioRepository.findAll().stream().map(usuarioUtils::toDTO).collect(Collectors.toList());
    }

    public UsuarioDTO saveUser(RegisterDTO registerDTO) {
        if (usuarioRepository.existsByEmail(registerDTO.Email())){
            throw new UsuarioJaExisteException();//retorna um salso negativo
        }
        Usuario usuario = new Usuario();
        usuario.setNome(registerDTO.Nome());
        usuario.setEmail(registerDTO.Email());
        usuario.setCurso(registerDTO.Curso());
        usuario.setHash(registerDTO.Senha());
        usuario.setD_criacao(LocalDateTime.now());
        usuarioRepository.save(usuario);
        return usuarioUtils.toDTO(usuario);
    }

    public UsuarioDTO update(Long id, UsuarioDTO usuarioDTO) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            usuario.setNome(usuarioDTO.Nome());
            usuario.setEmail(usuarioDTO.Email());
            usuario.setCurso(usuarioDTO.Curso());
            usuario.setD_criacao(LocalDateTime.now());
            usuarioRepository.save(usuario);
            return usuarioUtils.toDTO(usuario);
        } else {
            throw new UsuarioNaoEncontradoException();
        }
    }

    public UsuarioDTO findById(Long id) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            return usuarioUtils.toDTO(optionalUsuario.get());
        } else {
            throw new UsuarioNaoEncontradoException();
        }
    }

    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }
}