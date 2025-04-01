package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.model.ProfileRole;
import com.trendsit.trendsit_fase2.repository.ProfileRepository;
import com.trendsit.trendsit_fase2.util.OwnableEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorizationService implements UserDetailsService {

    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    @Autowired
    public AuthorizationService(ProfileRepository profileRepository, ProfileService profileService) {
        this.profileRepository = profileRepository;
        this.profileService = profileService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return profileRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public <T extends OwnableEntity> boolean isOwnerOrAdmin(T entity, UUID currentUserId) {
        return entity.getAutor().getId().equals(currentUserId) ||
                profileService.findById(currentUserId)
                        .map(p -> p.getRole() == ProfileRole.ADMIN)
                        .orElse(false);
    }
}
