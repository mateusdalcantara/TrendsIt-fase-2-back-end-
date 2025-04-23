package com.trendsit.trendsit_fase2.service.comentario;

import com.trendsit.trendsit_fase2.dto.comentario.ComentarioDTO;
import com.trendsit.trendsit_fase2.dto.comentario.ComentarioResponseDTO;
import com.trendsit.trendsit_fase2.model.comentario.Comentario;
import com.trendsit.trendsit_fase2.model.postagem.Postagem;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.comentario.ComentarioRepository;
import com.trendsit.trendsit_fase2.repository.postagem.PostagemRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import com.trendsit.trendsit_fase2.service.profile.ProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Comentario adicionarComentario(String conteudo, UUID autorId, Long postId) {
        Profile autor = profileRepository.findById(autorId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        Postagem postagem = postagemRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));

        Comentario comentario = new Comentario();
        comentario.setConteudo(conteudo);
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
        List<Comentario> comentarios = comentarioRepository.findByPostagemId(postId);
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