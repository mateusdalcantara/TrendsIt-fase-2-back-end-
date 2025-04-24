package com.trendsit.trendsit_fase2.controller.group;

import com.trendsit.trendsit_fase2.dto.group.CreateGroupRequest;
import com.trendsit.trendsit_fase2.dto.group.GroupDTO;
import com.trendsit.trendsit_fase2.exception.EntityNotFoundException;
import com.trendsit.trendsit_fase2.model.group.Group;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.group.GroupInvitationRepository;
import com.trendsit.trendsit_fase2.repository.group.GroupRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import com.trendsit.trendsit_fase2.service.group.GroupInvitation;
import com.trendsit.trendsit_fase2.service.group.GroupService;
import com.trendsit.trendsit_fase2.service.profile.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;
    private final GroupInvitationRepository groupInvitationRepository;
    private final ProfileRepository profileRepository;
    private final GroupRepository groupRepository;
    private final ProfileService profileService;

    public GroupController(GroupService groupService, GroupInvitationRepository groupInvitationRepository, ProfileRepository profileRepository, GroupRepository groupRepository, ProfileService profileService) {
        this.groupService = groupService;
        this.groupInvitationRepository = groupInvitationRepository;
        this.profileRepository = profileRepository;
        this.groupRepository = groupRepository;
        this.profileService = profileService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<List<GroupDTO>> listarGrupos() {
        List<GroupDTO> grupos = groupService.listarGrupos().stream()
                .map(GroupDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(grupos);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<List<GroupDTO>> listarMeusGrupos(@AuthenticationPrincipal Profile currentUser) {
        List<Group> grupos = groupRepository.findByCriadorOrMembrosContaining(currentUser, currentUser);
        List<GroupDTO> dtos = grupos.stream()
                .map(GroupDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<GroupDTO> criarGrupo(
            @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal Profile criador
    ) {
        GroupDTO grupoCriado = groupService.criarGrupo(request, criador);
        return ResponseEntity.status(HttpStatus.CREATED).body(grupoCriado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO')")
    public ResponseEntity<GroupDTO> editarGrupo(
            @PathVariable UUID id, // Tipo UUID
            @RequestBody CreateGroupRequest request
    ) {
        return ResponseEntity.ok(groupService.editarGrupo(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @groupSecurity.isGroupCreator(#id, principal)")
    public ResponseEntity<Void> deletarGrupo(
            @PathVariable UUID id,
            @AuthenticationPrincipal Profile user
    ) {
        groupService.deletarGrupo(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupId}/invite/{friendNumber}")
    @PreAuthorize("hasAnyRole('ALUNO','PROFESSOR','ADMIN')")
    public ResponseEntity<Void> inviteUserToGroup(
            @PathVariable UUID groupId,
            @PathVariable Long friendNumber,
            @AuthenticationPrincipal Profile currentUser
    ) {
        groupService.inviteUserToGroup(groupId, friendNumber, currentUser);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{groupId}/convite/aceitar")
    @PreAuthorize("hasAnyRole('ALUNO','PROFESSOR','ADMIN')")
    public ResponseEntity<Void> aceitarConvite(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal Profile currentUser
    ) {
        groupService.aceitarConvite(groupId, currentUser);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/invitations/{invitationId}/accept")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ALUNO', 'ADMIN')")
    public ResponseEntity<Void> aceitarConviteGrupo(
            @PathVariable UUID invitationId,
            @AuthenticationPrincipal Profile currentUser) {
        groupService.aceitarConvite(invitationId, currentUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}/membros/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removerMembro(
            @PathVariable UUID groupId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal Profile currentUser
    ) {
        groupService.removerMembro(groupId, userId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/convites/recusar")
    public void recusarConvite(@RequestParam UUID groupId, @RequestParam UUID userId) {
        groupService.recusarConvite(groupId, userId);  // Passando Long para o serviço
    }

}
