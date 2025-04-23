package com.trendsit.trendsit_fase2.service.group;

import com.trendsit.trendsit_fase2.dto.group.GroupPostCommentRequestDTO;
import com.trendsit.trendsit_fase2.dto.group.GroupPostRequestDTO;
import com.trendsit.trendsit_fase2.exception.EntityNotFoundException;
import com.trendsit.trendsit_fase2.model.group.Group;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.repository.group.GroupPostCommentRepository;
import com.trendsit.trendsit_fase2.repository.group.GroupPostRepository;
import com.trendsit.trendsit_fase2.repository.group.GroupRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

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
}
