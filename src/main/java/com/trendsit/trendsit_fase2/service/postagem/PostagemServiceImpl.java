package com.trendsit.trendsit_fase2.service.postagem;

import com.trendsit.trendsit_fase2.dto.comentario.ComentarioResponseDTO;
import com.trendsit.trendsit_fase2.dto.postagem.*;
import com.trendsit.trendsit_fase2.dto.profile.ProfileAdminDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfilePublicoDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfileUpdateDTO;
import com.trendsit.trendsit_fase2.model.postagem.Postagem;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.postagem.PostagemRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import com.trendsit.trendsit_fase2.service.profile.ProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostagemServiceImpl implements PostagemService {
    private final PostagemRepository postagemRepository;
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    @Autowired
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
    public void deleteAllByAutor(Profile autor) {
        postagemRepository.deleteByAutor(autor);
    }

    @Override
    public List<PostagemResponseDTO> findAllPostsProjection() {
        return postagemRepository.findAllPostsProjection();
    }


    @Transactional
    @Override
    public Postagem createPost(PostagemRequestDTO postagemRequest, UUID autorId) {
        Profile autor = profileRepository.findById(autorId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        Postagem postagem = new Postagem();
        postagem.setConteudo(postagemRequest.getConteudo());
        postagem.setAutor(autor);

        return postagemRepository.save(postagem);
    }

    @Override
    public List<PostagemWithCommentsDTO> findAllPostsWithComments() {
        return postagemRepository
                .findAllWithAuthorAndComments()
                .stream()
                .map(p -> new PostagemWithCommentsDTO(
                        new PostagemResponseDTO(p),
                        p.getComentarios().stream()
                                .map(ComentarioResponseDTO::new)
                                .toList()
                ))
                .toList();
    }


    public List<PostagemResponseDTO> findAllPosts() {
        List<Postagem> postagens = postagemRepository.findAllWithAutor(); // Usa a consulta com JOIN FETCH
        return postagens.stream()
                .map(PostagemResponseDTO::new)
                .toList();
    }

    @Override
    public List<PostagemResponseAdminDTO> findAllPostsAdmin() {
        return postagemRepository.findAllWithComments().stream()
                .map(PostagemResponseAdminDTO::new)
                .collect(Collectors.toList());
    }

    public PostagemResponseDTO findPostById(Long id) {
        Postagem postagem = postagemRepository.findByIdWithAutor(id) // Usa a consulta com JOIN FETCH
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));
        return new PostagemResponseDTO(postagem);
    }

    @Override
    public Postagem updatePostContent(Long postId, PostagemUpdateDTO postagemUpdateDTO) {
        Postagem existingPost = postagemRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));

        existingPost.setConteudo(postagemUpdateDTO.getConteudo());
        return postagemRepository.save(existingPost);
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
    public Postagem updatePost(Long postId, PostagemDTO postagemDto) {
        Postagem existingPost = postagemRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));

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

    @Override
    public Profile updateUserRole(UUID userId, ProfileRole newRole) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        profile.setRole(newRole);
        return profileRepository.save(profile);
    }

    @Override
    public Profile atualizarPerfilUsuario(UUID userId, ProfileUpdateDTO dto) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        profile.setUsername(dto.getUsername());
        profile.setIdade(dto.getIdade());


        return profileRepository.save(profile);
    }

    @Override
    public List<ProfileAdminDTO> findAllForAdmin() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream()
                .map(ProfileAdminDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfilePublicoDTO> findAllPublicProfiles() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream()
                .map(ProfilePublicoDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfilePublicoDTO> findAllPublicoProfiles() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream()
                .map(ProfilePublicoDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostagemResponseDTO> findPostsForUser(UUID userId) {
        Profile user = profileRepository.findById(userId).orElseThrow();
        List<UUID> followingIds = user.getFollowing().stream()
                .map(Profile::getId)
                .collect(Collectors.toList());
        followingIds.add(userId);

        return postagemRepository.findByAutorIdIn(followingIds).stream()
                .map(PostagemResponseDTO::new)
                .collect(Collectors.toList());
    }
}