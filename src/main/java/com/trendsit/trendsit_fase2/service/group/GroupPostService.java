package com.trendsit.trendsit_fase2.service.group;

import com.trendsit.trendsit_fase2.dto.group.GroupPostCommentRequestDTO;
import com.trendsit.trendsit_fase2.dto.group.GroupPostCommentResponseDTO;
import com.trendsit.trendsit_fase2.dto.group.GroupPostRequestDTO;
import com.trendsit.trendsit_fase2.exception.EntityNotFoundException;
import com.trendsit.trendsit_fase2.model.group.Group;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.group.GroupPostCommentRepository;
import com.trendsit.trendsit_fase2.repository.group.GroupPostRepository;
import com.trendsit.trendsit_fase2.repository.group.GroupRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupPostService {

    private final GroupPostRepository postRepository;
    private final GroupPostCommentRepository commentRepository;
    private final GroupService groupService;


    // Criar postagem (somente membros)
    public GroupPost createPost(UUID groupId, GroupPostRequestDTO request, Profile author) {
        // Carrega o grupo já com membros
        Group group = groupService.getGroupWithMembers(groupId);

        // Verificar se o autor é membro do grupo
        if (!group.getMembros().contains(author)) {
            throw new AccessDeniedException("Apenas membros podem postar");
        }

        GroupPost post = new GroupPost();
        post.setContent(request.getContent());
        post.setGroup(group);
        post.setAuthor(author);
        return postRepository.save(post);
    }

    // Criar comentário (somente membros)
    public GroupPostComment createComment(UUID postId, GroupPostCommentRequestDTO request, Profile author) {
        GroupPost post = postRepository.findByIdWithGroupAndMembers(postId)
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));

        // Verificação de membro no grupo da postagem
        if (post.getGroup().getMembros().stream()
                .noneMatch(member -> member.getId().equals(author.getId()))) {
            throw new AccessDeniedException("Apenas membros do grupo podem comentar");
        }

        GroupPostComment comment = new GroupPostComment();
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setAuthor(author);

        return commentRepository.save(comment);
    }

    // Listar postagens do grupo (somente membros)
    public List<GroupPost> getPostsByGroup(UUID groupId, Profile user) {
        // Carrega o grupo com membros
        Group group = groupService.getGroupWithMembers(groupId);

        if (!group.getMembros().contains(user)) {
            throw new AccessDeniedException("Apenas membros podem visualizar as postagens");
        }

        return postRepository.findByGroupIdWithComments(groupId);
    }

    @Transactional
    public void deletePost(UUID postId, Profile currentUser) {
        GroupPost post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));

        boolean isOwner = post.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == ProfileRole.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Somente o autor ou administradores podem excluir esta postagem");
        }

        postRepository.delete(post);
    }

    @Transactional
    public void deleteComment(UUID commentId, Profile currentUser) {
        // Busca o comentário com relacionamentos necessários
        GroupPostComment comment = commentRepository.findByIdWithPostAndGroupAndAuthor(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado"));

        // Validações implícitas (opcional, mas recomendado)
        UUID postId = comment.getPost().getId();
        UUID groupId = comment.getPost().getGroup().getId();

        // Verifica permissões
        boolean isOwner = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == ProfileRole.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Somente o autor ou administradores podem excluir este comentário");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public GroupPost updatePost(UUID groupId, UUID postId, String newContent, Profile currentUser) {
        // Busca postagem com relacionamentos necessários
        GroupPost post = postRepository.findByIdWithGroupAndAuthor(postId)
                .orElseThrow(() -> new EntityNotFoundException("Postagem não encontrada"));

        // Verifica se a postagem pertence ao grupo
        if (!post.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("Postagem não encontrada neste grupo");
        }

        // Verifica permissões
        boolean isOwner = post.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == ProfileRole.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Somente o autor ou administradores podem editar esta postagem");
        }

        // Atualiza conteúdo
        post.setContent(newContent);
        return postRepository.save(post);
    }

    @Transactional
    public GroupPostComment updateComment(
            UUID postId,
            UUID commentId,
            String newContent,
            Profile currentUser
    ) {
        // Busca o comentário com a postagem e grupo relacionados
        GroupPostComment comment = commentRepository.findByIdWithPostAndGroupAndAuthor(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado"));

        // Verifica se o comentário pertence à postagem (postId)
        if (!comment.getPost().getId().equals(postId)) {
            throw new EntityNotFoundException("Comentário não pertence à postagem");
        }

        // Obtém o groupId da postagem (não precisa ser validado na URL)
        UUID groupId = comment.getPost().getGroup().getId();

        // Verifica se o usuário tem permissão (dono do comentário ou admin)
        boolean isOwner = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == ProfileRole.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Somente o autor ou administradores podem editar este comentário");
        }

        // Atualiza o conteúdo
        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

}
