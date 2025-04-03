package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.Profile.ProfileAdminDTO;
import com.trendsit.trendsit_fase2.dto.Postagem.PostagemDTO;
import com.trendsit.trendsit_fase2.dto.Postagem.PostagemResponseAdminDTO;
import com.trendsit.trendsit_fase2.dto.Postagem.PostagemResponseDTO;
import com.trendsit.trendsit_fase2.dto.Profile.ProfilePublicoDTO;
import com.trendsit.trendsit_fase2.dto.Profile.ProfileUpdateDTO;
import com.trendsit.trendsit_fase2.model.Postagem;
import com.trendsit.trendsit_fase2.model.Profile;
import com.trendsit.trendsit_fase2.model.ProfileRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostagemService {

    Postagem createPost(PostagemDTO postagemDto, UUID autorId);

    List<PostagemResponseDTO> findAllPosts();

    List<PostagemResponseAdminDTO> findAllPostsAdmin();

    boolean isOwnerOrAdmin(Postagem postagem, UUID currentUserId);

    Optional<Postagem> findById(Long postId);

    Postagem updatePost(Long postId, PostagemDTO postagemDto);

    void deletePost(Long postId);

    Profile updateUserRole(UUID userId, ProfileRole newRole);

    Profile atualizarPerfilUsuario(UUID userId, ProfileUpdateDTO dto);

    List<ProfileAdminDTO> findAllForAdmin();

    List<ProfilePublicoDTO> findAllPublicProfiles();

    List<ProfilePublicoDTO> findAllPublicoProfiles();
}