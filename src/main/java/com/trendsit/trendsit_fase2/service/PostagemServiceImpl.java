package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.PostagemDto;
import com.trendsit.trendsit_fase2.dto.PostagemResponseDTO;
import com.trendsit.trendsit_fase2.model.Postagem;
import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.model.ProfileRole;
import com.trendsit.trendsit_fase2.repository.PostagemRepository;
import com.trendsit.trendsit_fase2.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostagemServiceImpl implements PostagemService {

    private final PostagemRepository postagemRepository;
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    public PostagemServiceImpl(
            PostagemRepository postagemRepository,
            ProfileRepository profileRepository,
            ProfileService profileService
    ) {
        this.postagemRepository = postagemRepository;
        this.profileRepository = profileRepository;
        this.profileService = profileService;
    }

    @Override
    public Postagem createPost(PostagemDto postagemDto, UUID autorId) {
        Profile autor = profileRepository.findById(autorId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        Postagem postagem = new Postagem();
        postagem.setTitulo(postagemDto.getTitulo());
        postagem.setConteudo(postagemDto.getConteudo());
        postagem.setAutor(autor);

        return postagemRepository.save(postagem);
    }

    @Override
    public List<PostagemResponseDTO> findAllPosts() {
        return postagemRepository.findAllWithComments().stream()
                .map(PostagemResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isOwnerOrAdmin(Postagem postagem, UUID currentUserId) {
        return postagem.getAutor().getId().equals(currentUserId) ||
                profileService.findById(currentUserId)
                        .map(p -> p.getRole() == ProfileRole.ADMIN)
                        .orElse(false);
    }

    @Override
    public Optional<Postagem> findById(Long postId) {
        return postagemRepository.findWithAuthorById(postId);
    }

    @Override
    public Postagem updatePost(Long postId, PostagemDto postagemDto) {
        Postagem existingPost = postagemRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));

        existingPost.setTitulo(postagemDto.getTitulo());
        existingPost.setConteudo(postagemDto.getConteudo());

        return postagemRepository.save(existingPost);
    }

    @Override
    public void deletePost(Long postId) {
        if (!postagemRepository.existsById(postId)) {
            throw new EntityNotFoundException("Postagem não encontrada");
        }
        postagemRepository.deleteById(postId);
    }
}