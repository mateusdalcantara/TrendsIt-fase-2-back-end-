package com.trendsit.trendsit_fase2.service.group;

import com.trendsit.trendsit_fase2.dto.group.CreateGroupRequest;
import com.trendsit.trendsit_fase2.dto.group.GroupDTO;
import com.trendsit.trendsit_fase2.exception.EntityNotFoundException;
import com.trendsit.trendsit_fase2.model.group.Group;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.group.GroupInvitationRepository;
import com.trendsit.trendsit_fase2.repository.group.GroupRepository;
import com.trendsit.trendsit_fase2.repository.notification.NotificationRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import com.trendsit.trendsit_fase2.service.notification.NotificationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final ProfileRepository profileRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    public GroupService(
            GroupRepository groupRepository,
            ProfileRepository profileRepository,
            GroupInvitationRepository groupInvitationRepository, NotificationRepository notificationRepository, NotificationService notificationService
    ) {
        this.groupRepository = groupRepository;
        this.profileRepository = profileRepository;
        this.groupInvitationRepository = groupInvitationRepository;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    /**
     * Convida um usuário ao grupo e cria notificação.
     */
    public void inviteUserToGroup(UUID groupId,
                                  Long invitedFriendNumber,
                                  Profile currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        Profile invitedUser = profileRepository.findByFriendNumber(invitedFriendNumber)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (!currentUser.getRole().equals(ProfileRole.ALUNO)
                && !currentUser.getRole().equals(ProfileRole.PROFESSOR)
                && !currentUser.getRole().equals(ProfileRole.ADMIN)) {
            throw new AccessDeniedException("Permissão negada");
        }

        if (group.getMembros().contains(invitedUser)) {
            throw new AccessDeniedException("Este usuário já é membro do grupo.");
        }

        GroupInvitation invitation = new GroupInvitation();
        invitation.setGroup(group);
        invitation.setInvited(invitedUser);
        invitation.setStatus(GroupInvitation.Status.PENDING);
        groupInvitationRepository.save(invitation);

        notificationService.createGroupInviteNotification(invitation);
    }

    /** Lista todos os grupos (sem membros carregados). */
    public List<Group> listarGrupos() {
        return groupRepository.findAll();
    }

    /** Cria um grupo e adiciona o criador como membro. */
    public GroupDTO criarGrupo(CreateGroupRequest request, Profile criador) {
        Group group = new Group();
        group.setNome(request.getNome());
        group.setCriador(criador);
        group.getMembros().add(criador);

        Group saved = groupRepository.save(group);
        return new GroupDTO(saved);
    }

    /** Verifica se um usuário é membro do grupo. */
    public boolean isGroupMember(UUID groupId, Profile user) {
        Group group = getGroupWithMembers(groupId);
        return group.getMembros().contains(user);
    }

    /** Busca um grupo carregando também seus membros. */
    public Group getGroupWithMembers(UUID groupId) {
        return groupRepository.findByIdWithMembros(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
    }

    /** Edita o nome do grupo. */
    public GroupDTO editarGrupo(UUID id, CreateGroupRequest request) {
        Group group = getGroupWithMembers(id);
        group.setNome(request.getNome());
        return new GroupDTO(groupRepository.save(group));
    }

    /** Deleta grupo caso seja o criador ou ADMIN. */
    @Transactional
    public void deletarGrupo(UUID groupId, Profile user) {
        Group group = groupRepository.findByIdWithCreator(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        boolean isCreator = group.getCriador().getId().equals(user.getId());
        boolean isAdmin   = user.getRole() == ProfileRole.ADMIN;

        if (!isCreator && !isAdmin) {
            throw new AccessDeniedException(
                    "Apenas o criador do grupo (" + group.getCriador().getUsername() +
                            ") ou administradores podem deletar este recurso."
            );
        }

        // 1) Remove convites pendentes relacionados a este grupo
        groupInvitationRepository.deleteByGroup(group);

        // 2) Remove notificações relacionadas a este grupo
        notificationService.deleteNotificationsByGroup(group);

        // 3) (Opcional) limpa associação membros <-> grupo
        group.getMembros().clear();
        groupRepository.save(group);

        // 4) Deleta o grupo
        groupRepository.delete(group);
    }


    /** Aceita convite e adiciona o usuário ao grupo, removendo o convite. */
    /**
     * Aceita um convite pendente e remove a notificação.
     */
    @Transactional
    public void aceitarConvite(UUID invitationId, Profile currentUser) {
        GroupInvitation invitation = groupInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Convite não encontrado"));

        // Verifica se o convite pertence ao usuário atual
        if (!invitation.getInvited().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para aceitar este convite.");
        }

        if (invitation.getStatus() != GroupInvitation.Status.PENDING) {
            throw new IllegalStateException("Este convite já foi processado.");
        }

        // Adiciona o usuário ao grupo
        Group group = invitation.getGroup();
        group.getMembros().add(currentUser);
        groupRepository.save(group);

        // Atualiza o status do convite
        invitation.setStatus(GroupInvitation.Status.ACCEPTED);
        groupInvitationRepository.save(invitation);

        // Remove a notificação
        notificationService.deleteGroupInviteNotification(invitation);
    }

    /** Recusa convite (marca como declined). */
    public void recusarConvite(UUID groupId, UUID userId) {
        Group group = getGroupWithMembers(groupId);
        Profile invited = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        var convite = groupInvitationRepository.findByGroupAndInvited(group, invited)
                .orElseThrow(() -> new EntityNotFoundException("Convite para o grupo não encontrado"));

        convite.setStatus(GroupInvitation.Status.DECLINED);
        groupInvitationRepository.save(convite);
    }

    /** Remove um membro (somente ADMIN). */
    public void removerMembro(UUID groupId, UUID userId, Profile currentUser) {
        if (currentUser.getRole() != ProfileRole.ADMIN) {
            throw new AccessDeniedException("Você não tem permissão para remover membros.");
        }

        Group group = getGroupWithMembers(groupId);
        Profile userToRemove = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        group.getMembros().remove(userToRemove);
        groupRepository.save(group);
    }
}