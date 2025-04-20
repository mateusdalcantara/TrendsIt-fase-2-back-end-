package com.trendsit.trendsit_fase2.service.postagem;

import com.trendsit.trendsit_fase2.dto.postagem.PostagemRequestDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfileAdminDTO;
import com.trendsit.trendsit_fase2.dto.postagem.PostagemDTO;
import com.trendsit.trendsit_fase2.dto.postagem.PostagemResponseAdminDTO;
import com.trendsit.trendsit_fase2.dto.postagem.PostagemResponseDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfilePublicoDTO;
import com.trendsit.trendsit_fase2.dto.profile.ProfileUpdateDTO;
import com.trendsit.trendsit_fase2.model.postagem.Postagem;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface PostagemService {

    @Transactional
    Postagem createPost(PostagemRequestDTO postagemRequest, UUID autorId);

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

    List<PostagemResponseDTO> findPostsForUser(UUID userId);

    PostagemResponseDTO findPostById(Long id);
}