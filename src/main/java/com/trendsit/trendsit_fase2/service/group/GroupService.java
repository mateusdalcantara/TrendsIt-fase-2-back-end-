package com.trendsit.trendsit_fase2.service.group;

import com.trendsit.trendsit_fase2.dto.group.CreateGroupRequest;
import com.trendsit.trendsit_fase2.dto.group.GroupDTO;
import com.trendsit.trendsit_fase2.exception.EntityNotFoundException;
import com.trendsit.trendsit_fase2.model.group.Group;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.group.GroupInvitationRepository;
import com.trendsit.trendsit_fase2.repository.group.GroupRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final ProfileRepository profileRepository;
    private final GroupInvitationRepository groupInvitationRepository;

    public GroupService(
            GroupRepository groupRepository,
            ProfileRepository profileRepository,
            GroupInvitationRepository groupInvitationRepository
    ) {
        this.groupRepository = groupRepository;
        this.profileRepository = profileRepository;
        this.groupInvitationRepository = groupInvitationRepository;
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
    public void deletarGrupo(UUID id, Profile user) {
        Group group = groupRepository.findByIdWithCreator(id)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        boolean isCreator = group.getCriador().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == ProfileRole.ADMIN;

        if (!isCreator && !isAdmin) {
            throw new AccessDeniedException(
                    "Apenas o criador do grupo (" + group.getCriador().getUsername() + ") ou administradores podem deletar este recurso."
            );
        }

        // Remove referências antes de deletar
        group.getMembros().clear();
        groupRepository.save(group);
        groupRepository.delete(group);
    }

    /** Convida um usuário ao grupo. */
    public void inviteUserToGroup(UUID groupId, UUID invitedUserId, Profile currentUser) {
        Group group = getGroupWithMembers(groupId);
        Profile invitedUser = profileRepository.findById(invitedUserId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (!currentUser.getRole().equals(ProfileRole.ALUNO)
                && !currentUser.getRole().equals(ProfileRole.PROFESSOR)
                && !currentUser.getRole().equals(ProfileRole.ADMIN)) {
            throw new AccessDeniedException("Permissão negada");
        }

        if (group.getMembros().contains(invitedUser)) {
            throw new AccessDeniedException("Este usuário já é membro do grupo.");
        }

        var invitation = new GroupInvitation();
        invitation.setGroup(group);
        invitation.setInvited(invitedUser);
        invitation.setStatus(GroupInvitation.Status.PENDING);
        groupInvitationRepository.save(invitation);
    }

    /** Aceita convite e adiciona o usuário ao grupo, removendo o convite. */
    public void aceitarConvite(UUID conviteId, Profile currentUser) {
        var convite = groupInvitationRepository.findById(conviteId)
                .orElseThrow(() -> new EntityNotFoundException("Convite não encontrado"));

        if (!convite.getInvited().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Este convite não é para você.");
        }

        Group group = getGroupWithMembers(convite.getGroup().getId());
        group.getMembros().add(currentUser);
        groupRepository.save(group);

        groupInvitationRepository.delete(convite);
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