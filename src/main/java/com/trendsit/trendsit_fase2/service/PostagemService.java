package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.dto.PostagemDto;
import com.trendsit.trendsit_fase2.dto.PostagemResponseDTO;
import com.trendsit.trendsit_fase2.model.Postagem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostagemService {
    Postagem createPost(PostagemDto postagemDto, UUID autorId);
    List<PostagemResponseDTO> findAllPosts();
    boolean isOwnerOrAdmin(Postagem postagem, UUID currentUserId);
    Optional<Postagem> findById(Long postId);
    Postagem updatePost(Long postId, PostagemDto postagemDto);
    void deletePost(Long postId);
}