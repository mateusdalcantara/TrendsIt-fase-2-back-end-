package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.ComentarioDto;
import com.trendsit.trendsit_fase2.dto.ComentarioResponseDTO;
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
    public Comentario adicionarComentario(ComentarioDto comentarioDto, UUID autorId, Long postId) {
        Profile autor = profileRepository.findById(autorId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        Postagem postagem = postagemRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));

        Comentario comentario = new Comentario();
        comentario.setConteudo(comentarioDto.getConteudo());
        comentario.setAutor(autor);
        comentario.setPostagem(postagem);

        return comentarioRepository.save(comentario);
    }

    @Override
    public Optional<Comentario> findById(Long commentId) {
        return comentarioRepository.findWithAutorById(commentId);
    }

    @Override
    public boolean isOwnerOrAdmin(Comentario comentario, UUID currentUserId) {
        return comentario.getAutor().getId().equals(currentUserId) ||
                profileService.findById(currentUserId)
                        .map(p -> p.getRole() == ProfileRole.ADMIN)
                        .orElse(false);
    }

    @Override
    public Comentario updateComentario(Long commentId, ComentarioDto comentarioDto) {
        Comentario existingComment = comentarioRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado"));

        existingComment.setConteudo(comentarioDto.getConteudo());
        return comentarioRepository.save(existingComment);
    }

    @Override
    public void deleteComentario(Long postId, Long commentId, UUID currentUserId) {
        Comentario comentario = comentarioRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado"));

        if (!comentario.getPostagem().getId().equals(postId)) {
            throw new IllegalArgumentException("Comentário não pertence ao post especificado");
        }

        if (!isOwnerOrAdmin(comentario, currentUserId)) {
            throw new AccessDeniedException("Acesso negado");
        }

        comentarioRepository.delete(comentario);
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
}