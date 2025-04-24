package com.trendsit.trendsit_fase2.service.profile;

import com.trendsit.trendsit_fase2.model.profile.Profile;
import com.trendsit.trendsit_fase2.repository.notification.NotificationRepository;
import com.trendsit.trendsit_fase2.repository.group.GroupRepository;
import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ProfileServiceImplTest {

    @Autowired
    private ProfileRepository profileRepo;

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private GroupRepository groupRepo;

    @Autowired
    private ProfileService profileService;

    private Profile A, B, C, D, E;

    @BeforeEach
    public void setup() {

        // Cria os perfis
        A = profileRepo.save(new Profile(UUID.randomUUID(), "Aline"));
        B = profileRepo.save(new Profile(UUID.randomUUID(), "Bruno"));
        C = profileRepo.save(new Profile(UUID.randomUUID(), "Camila"));
        D = profileRepo.save(new Profile(UUID.randomUUID(), "Daniel"));
        E = profileRepo.save(new Profile(UUID.randomUUID(), "Elisa"));

        // Monta o grafo de follows
        A.getFollowing().add(B);
        B.getFollowing().add(C);
        C.getFollowing().add(D);
        C.getFollowing().add(E);
        D.getFollowing().add(E);

        //  Persiste as relações ManyToMany
        profileRepo.saveAll(List.of(A, B, C, D, E));
    }

    @Test
    public void deveEncontrarCaminhoMaisCurtoAteElisa() {
        List<String> caminho = profileService.obterCaminhoMaisCurto(A.getId(), E.getId());
        assertEquals(List.of("Aline", "Bruno", "Camila", "Elisa"), caminho);
    }
}
