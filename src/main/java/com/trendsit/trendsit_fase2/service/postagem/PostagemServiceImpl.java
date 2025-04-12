package com.trendsit.trendsit_fase2.service.postagem;

import com.trendsit.trendsit_fase2.dto.profile.ProfileAdminDTO;
import com.trendsit.trendsit_fase2.dto.postagem.PostagemDTO;
import com.trendsit.trendsit_fase2.dto.postagem.PostagemResponseAdminDTO;
import com.trendsit.trendsit_fase2.dto.postagem.PostagemResponseDTO;
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
            ProfileRepository profileRepository, // Add this
            ProfileService profileService
    ) {
        this.postagemRepository = postagemRepository;
        this.profileRepository = profileRepository;
        this.profileService = profileService;
    }


    @Override
    @Transactional // Add this annotation
    public Postagem createPost(PostagemDTO postagemDto, UUID autorId) {
        Profile autor = profileRepository.findById(autorId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        Postagem postagem = new Postagem();
        postagem.setTitulo(postagemDto.getTitulo());
        postagem.setConteudo(postagemDto.getConteudo());
        postagem.setAutor(autor);

        // Explicitly save and flush to generate the ID immediately
        postagem = postagemRepository.saveAndFlush(postagem);

        return postagem;
    }

    @Override
    public List<PostagemResponseDTO> findAllPosts() {
        return postagemRepository.findAllWithComments().stream()
                .map(PostagemResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostagemResponseAdminDTO> findAllPostsAdmin() {
        return postagemRepository.findAllWithComments().stream()
                .map(PostagemResponseAdminDTO::new)
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
    public Postagem updatePost(Long postId, PostagemDTO postagemDto) {
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
        profile.setCurso(dto.getCurso());

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