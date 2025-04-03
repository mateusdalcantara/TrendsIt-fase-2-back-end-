package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.Comentario.ComentarioDTO;
import com.trendsit.trendsit_fase2.dto.Comentario.ComentarioResponseDTO;
import com.trendsit.trendsit_fase2.model.Comentario;
import com.trendsit.trendsit_fase2.model.Postagem;
import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.model.ProfileRole;
import com.trendsit.trendsit_fase2.repository.ComentarioRepository;
import com.trendsit.trendsit_fase2.repository.PostagemRepository;
import com.trendsit.trendsit_fase2.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComentarioServiceImpl implements ComentarioService {
    private final ComentarioRepository comentarioRepository;
    private final PostagemRepository postagemRepository;
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    public ComentarioServiceImpl(
            ComentarioRepository comentarioRepository,
            PostagemRepository postagemRepository,
            ProfileRepository profileRepository,
            ProfileService profileService
    ) {
        this.comentarioRepository = comentarioRepository;
        this.postagemRepository = postagemRepository;
        this.profileRepository = profileRepository;
        this.profileService = profileService;
    }

    @Override
    public Comentario adicionarComentario(ComentarioDTO comentarioDTO, UUID autorId, Long postId) {
        Profile autor = profileRepository.findById(autorId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        Postagem postagem = postagemRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));

        Comentario comentario = new Comentario();
        comentario.setConteudo(comentarioDTO.getConteudo());
        comentario.setAutor(autor);
        comentario.setPostagem(postagem);

        return comentarioRepository.save(comentario);
    }

    @Override
    public Optional<Comentario> findById(Long comentarioId) {
        return comentarioRepository.findWithAutorById(comentarioId);
    }

    @Override
    public boolean isOwnerOrAdmin(Comentario comentario, UUID currentUserId) {
        return comentario.getAutor().getId().equals(currentUserId) ||
                profileService.findById(currentUserId)
                        .map(p -> p.getRole() == ProfileRole.ADMIN)
                        .orElse(false);
    }

    @Override
    public List<ComentarioResponseDTO> findByPostIdAndAutorId(Long postId, UUID autorId) {
        Postagem postagem = postagemRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));

        List<Comentario> comentarios = comentarioRepository.findByPostagemIdAndAutorId(postId, autorId);

        return comentarios.stream()
                .map(ComentarioResponseDTO::new)
                .toList();
    }

    @Override
    public List<ComentarioResponseDTO> findComentariosByAutorId(UUID autorId) {
        List<Comentario> comentarios = comentarioRepository.findByAutorId(autorId);
        return comentarios.stream()
                .map(ComentarioResponseDTO::new)
                .toList();
    }

    @Override
    public List<ComentarioResponseDTO> findByPostagemId(Long postId) {
        List<Comentario> comentarios = comentarioRepository.findByPostagemId(postId); // Matches the @Query method
        return comentarios.stream()
                .map(ComentarioResponseDTO::new)
                .toList();
    }

    @Override
    public Comentario updateComentario(Long comentarioId, Long postId, ComentarioDTO comentarioDTO, UUID currentUserId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado"));

        if (!comentario.getPostagem().getId().equals(postId)) {
            throw new IllegalArgumentException("Comentário não pertence ao post especificado");
        }

        if (!isOwnerOrAdmin(comentario, currentUserId)) {
            throw new AccessDeniedException("Acesso negado");
        }

        comentario.setConteudo(comentarioDTO.getConteudo());
        return comentarioRepository.save(comentario);
    }

    @Override
    public void deleteComentario(Long postId, Long comentarioId, UUID currentUserId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado"));

        if (!comentario.getPostagem().getId().equals(postId)) {
            throw new IllegalArgumentException("Comentário não pertence ao post especificado");
        }

        if (!isOwnerOrAdmin(comentario, currentUserId)) {
            throw new AccessDeniedException("Acesso negado");
        }

        comentarioRepository.delete(comentario);
    }
}