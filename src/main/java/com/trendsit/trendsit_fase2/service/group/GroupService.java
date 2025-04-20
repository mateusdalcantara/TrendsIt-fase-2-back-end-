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
import org.hibernate.Hibernate;
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

    public List<Group> listarGrupos() {
        return groupRepository.findAll();
    }

    public GroupDTO criarGrupo(CreateGroupRequest request, Profile criador) {
        Group group = new Group();
        group.setNome(request.getNome());
        group.setCriador(criador);
        group.getMembros().add(criador); // Adiciona o criador como membro

        Group grupoSalvo = groupRepository.save(group);
        return new GroupDTO(grupoSalvo);
    }

    public boolean isGroupMember(UUID groupId, Profile user) {
        Group group = getGroupById(groupId);
        return group.getMembros().contains(user);
    }

    public Group getGroupById(UUID groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
    }



    public GroupDTO editarGrupo(UUID id, CreateGroupRequest request) { // Tipo UUID
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
        group.setNome(request.getNome());
        return new GroupDTO(groupRepository.save(group));
    }

    public void deletarGrupo(UUID id, Profile user) {
        // Busca o grupo com o criador carregado
        Group group = groupRepository.findByIdWithCreator(id)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        // Verifica se é o criador (qualquer role) ou ADMIN
        boolean isCreator = group.getCriador().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == ProfileRole.ADMIN;

        if (!isCreator && !isAdmin) {
            throw new AccessDeniedException(
                    "Apenas o criador do grupo (" + group.getCriador().getUsername() +
                            ") ou administradores podem deletar este recurso."
            );
        }

        // Lógica de deleção segura
        group.getMembros().clear();
        groupRepository.save(group);
        groupRepository.delete(group);
    }

    public void inviteUserToGroup(UUID groupId, UUID invitedUserId, Profile currentUser){
            // Buscar o grupo com o ID do tipo Long
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

            // Buscar o usuário convidado com o ID do tipo Long
            Profile invitedUser = profileRepository.findById(invitedUserId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

            // Verificar se o usuário atual tem permissão para convidar
            if (!currentUser.getRole().equals(ProfileRole.ALUNO)
                    && !currentUser.getRole().equals(ProfileRole.PROFESSOR)
                    && !currentUser.getRole().equals(ProfileRole.ADMIN)) {
                throw new AccessDeniedException("Permissão negada");
            }

            // Criar a instância de GroupInvitation e setar os grupos e usuários
            GroupInvitation invitation = new GroupInvitation();
            invitation.setGroup(group);  // Aqui, você pode setar diretamente o objeto 'Group' se quiser
            invitation.setInvited(invitedUser);  // E o objeto 'Profile' do convidado

            // Salvar a nova entrada de convite, o ID será gerado automaticamente
            groupInvitationRepository.save(invitation);
        }

    public void aceitarConvite(UUID conviteId, Profile currentUser) {
        GroupInvitation convite = groupInvitationRepository.findById(conviteId)
                .orElseThrow(() -> new EntityNotFoundException("Convite não encontrado"));

        if (!convite.getInvited().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Este convite não é para você.");
        }

        Group grupo = convite.getGroup();
        grupo.getMembros().add(currentUser);  // Adiciona o usuário ao grupo
        groupRepository.save(grupo);

        groupInvitationRepository.delete(convite);  // Exclui o convite após ser aceito
    }

    public void recusarConvite(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        Profile invited = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        GroupInvitation convite = groupInvitationRepository.findByGroupAndInvited(group, invited)
                .orElseThrow(() -> new EntityNotFoundException("Convite para o grupo não encontrado"));

        convite.setStatus(GroupInvitation.Status.DECLINED);
        groupInvitationRepository.save(convite);
    }

    public void removerMembro(UUID groupId, UUID userId, Profile currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        // Nova verificação (exemplo: apenas ADMIN pode remover)
        if (currentUser.getRole() != ProfileRole.ADMIN) {
            throw new AccessDeniedException("Você não tem permissão para remover membros.");
        }

        Profile userToRemove = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        group.getMembros().remove(userToRemove);
        groupRepository.save(group);
    }


    public Group getGroupWithMembers(UUID groupId) {
        return groupRepository.findByIdWithMembros(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
    }

    public Group save(Group group) {
        return groupRepository.save(group);  // Salva o grupo no banco de dados
    }
}

