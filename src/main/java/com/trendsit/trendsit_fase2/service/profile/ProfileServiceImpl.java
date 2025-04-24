package com.trendsit.trendsit_fase2.service.profile;


import com.trendsit.trendsit_fase2.dto.diretorio.DiretorioDTO;
import com.trendsit.trendsit_fase2.dto.postagem.PostagemResponseDTO;
import com.trendsit.trendsit_fase2.dto.profile.*;
import com.trendsit.trendsit_fase2.dto.auth.AuthProfileDTO;
import com.trendsit.trendsit_fase2.model.diretorio.Diretorio;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.model.profile.ProfileRole;
import com.trendsit.trendsit_fase2.repository.comentario.ComentarioRepository;
import com.trendsit.trendsit_fase2.repository.postagem.PostagemRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import com.trendsit.trendsit_fase2.service.notification.NotificationService;
import com.trendsit.trendsit_fase2.service.relationship.FriendNumberService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trendsit.trendsit_fase2.repository.diretorio.DiretorioRepository;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final DiretorioRepository diretorioRepository;
    private final PostagemRepository postagemRepository;
    private final ProfileRepository profileRepository;
    private final FriendNumberService friendNumberService;
    private final ComentarioRepository comentarioRepository;
    private final NotificationService notificationService;

    // Injeção via construtor
    @Autowired
    public ProfileServiceImpl(
            DiretorioRepository diretorioRepository, PostagemRepository postagemRepository,
            ProfileRepository profileRepository,
            FriendNumberService friendNumberService, ComentarioRepository comentarioRepository, NotificationService notificationService
    ) {
        this.diretorioRepository = diretorioRepository;
        this.postagemRepository = postagemRepository;
        this.profileRepository = profileRepository;
        this.friendNumberService = friendNumberService;
        this.comentarioRepository = comentarioRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void updateProfileImage(UUID userId, String image) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
        profile.setProfileImage(image);
        profileRepository.save(profile);
    }

    @Override
    public Optional<Profile> findByUsername(String username) {
        return profileRepository.findByUsername(username);
    }

    @Override
    public Optional<Profile> findById(UUID id) {
        return profileRepository.findById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return profileRepository.existsById(id);
    }

    @Override
    @Transactional
    public void createDefaultProfile(UUID userId) {
        Profile profile = new Profile();
        profile.setId(userId);
        profile.setUsername("default_username");
        profile.setRole(ProfileRole.ALUNO);

        // atribui friendNumber e imagem padrão logo na criação
        profile.setFriendNumber(friendNumberService.generateUniqueFriendNumber());
        profile.setProfileImage("/default-avatar.png");

        profileRepository.save(profile);
    }

    @Override
    public Profile createProfile(UUID userId, String username) {
        Profile profile = new Profile();
        profile.setId(userId);
        profile.setUsername(username);
        profile.setFriendNumber(friendNumberService.generateUniqueFriendNumber());
        profile.setRole(ProfileRole.ALUNO);
        // atribui sempre a imagem padrão, mesmo que o front não envie nada
        profile.setProfileImage("/default-avatar.png");
        return profileRepository.save(profile);
    }

    @Override
    public Profile criarPerfil(ProfileRequestDTO request) {
        Profile profile = new Profile();
        profile.setUsername(request.getUsername());
        profile.setIdade(request.getIdade());
        profile.setCurso(request.getCurso());
        profile.setFriendNumber(friendNumberService.generateUniqueFriendNumber());
        profile.setRole(ProfileRole.ALUNO);
        profile.setProfileImage("/default-avatar.png");
        return profileRepository.save(profile);
    }

    @Override
    public Optional<AuthProfileDTO> findAuthProfileById(UUID userId) {
        return profileRepository.findAuthProfileById(userId);
    }

    @Override
    @Transactional
    public Profile updateProfile(UUID profileId, ProfileRequestDTO request) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        profile.setUsername(request.getUsername());
        profile.setIdade(request.getIdade());
        profile.setCurso(request.getCurso());

        // se ainda não tiver sido gerado, popula defaults
        if (profile.getFriendNumber() == null) {
            profile.setFriendNumber(friendNumberService.generateUniqueFriendNumber());
        }
        if (profile.getProfileImage() == null) {
            profile.setProfileImage("/default-avatar.png");
        }

        return profileRepository.save(profile);
    }

    @Override
    @Transactional
    public void deleteProfile(UUID profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        // Exclui notificações primeiro!
        notificationService.deleteNotificationsByRecipient(profile);

        // Exclui o perfil após limpar as dependências
        profileRepository.delete(profile);
    }

    @Override
    public ProfileRequestDTO convertToDto(Profile profile) {
        ProfileRequestDTO dto = new ProfileRequestDTO();
        dto.setUsername(profile.getUsername());
        dto.setIdade(profile.getIdade());
        dto.setCurso(profile.getCurso());
        return dto;
    }

    @Override
    public List<ProfileRequestDTO> findAllProfiles() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfileAdminDTO> findAllForAdmin() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream()
                .map(ProfileAdminDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfilePublicoDTO> findAllPublicoProfiles() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream()
                .map(ProfilePublicoDTO::new)
                .collect(Collectors.toList());
    }

    public List<DiretorioDTO> findAllDiretorio() {
        List<Diretorio> diretorios = diretorioRepository.findAllWithRelations();
        return diretorios.stream()
                .map(DiretorioDTO::new)
                .collect(Collectors.toList());
    }


    @Override
    public Profile atualizarPerfilAdmin(UUID userId, ProfileAdminUpdateDTO dto) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado"));

        // Atualiza campos existentes
        profile.setUsername(dto.getUsername());
        profile.setIdade(dto.getIdade());
        profile.setCurso(dto.getCurso());
        profile.setRole(dto.getRole());

        // Atualiza o diretório (se fornecido)
        if (dto.getDiretorioId() != null) {
            Diretorio diretorio = diretorioRepository.findById(dto.getDiretorioId())
                    .orElseThrow(() -> new EntityNotFoundException("Diretório não encontrado"));
            profile.setDiretorio(diretorio);
            profile.setDiretorioNome(diretorio.getTituloDoCurso()); // Define o nome do diretório
        } else {
            profile.setDiretorio(null);
            profile.setDiretorioNome(null);
        }

        return profileRepository.save(profile);
    }

    @Override
    public Profile updateUserProfileAdmin(UUID userId, ProfileRequestDTO request) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        profile.setUsername(request.getUsername());
        profile.setIdade(request.getIdade());
        profile.setCurso(request.getCurso());

        return profileRepository.save(profile);
    }

    public List<PostagemResponseDTO> findPostsForUser(UUID userId) {
        Profile user = profileRepository.findById(userId).orElseThrow();
        List<UUID> followingIds = user.getFollowing().stream()
                .map(Profile::getId)
                .collect(Collectors.toList());
        followingIds.add(userId);

        return postagemRepository.findByAutor_IdIn(followingIds).stream()
                .map(PostagemResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> obterCaminhoMaisCurto(UUID deId, UUID paraId) {
        // Carrega todos os perfis com suas relações 'following' para construir o grafo
        List<Profile> todosPerfis = profileRepository.findAllWithFollowing();

        // Constrói o grafo de adjacência
        Map<UUID, Set<UUID>> grafo = new HashMap<>();
        for (Profile p : todosPerfis) {
            grafo.put(p.getId(), p.getFollowing().stream()
                    .map(Profile::getId)
                    .collect(Collectors.toSet()));
        }

        // Executa BFS para encontrar o caminho mais curto
        List<UUID> caminhoIds = bfsShortestPath(grafo, deId, paraId);

        // Converte UUIDs para usernames
        return caminhoIds.stream()
                .map(id -> profileRepository.findById(id)
                        .map(Profile::getUsername)
                        .orElse("[desconhecido]"))
                .collect(Collectors.toList());
    }

    // Método BFS para encontrar o caminho mais curto
    private List<UUID> bfsShortestPath(Map<UUID, Set<UUID>> grafo, UUID source, UUID target) {
        Queue<UUID> fila = new LinkedList<>();
        Map<UUID, UUID> veioDe = new HashMap<>();
        fila.add(source);
        veioDe.put(source, null);

        while (!fila.isEmpty()) {
            UUID atual = fila.poll();
            if (atual.equals(target)) break;

            for (UUID vizinho : grafo.getOrDefault(atual, Collections.emptySet())) {
                if (!veioDe.containsKey(vizinho)) {
                    veioDe.put(vizinho, atual);
                    fila.add(vizinho);
                }
            }
        }

        if (!veioDe.containsKey(target)) return Collections.emptyList();

        List<UUID> caminho = new ArrayList<>();
        for (UUID at = target; at != null; at = veioDe.get(at)) {
            caminho.add(at);
        }
        Collections.reverse(caminho);
        return caminho;
    }


}
