package com.trendsit.trendsit_fase2.service;

import com.trendsit.trendsit_fase2.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    ProfileRepository profileRepository;

    @Autowired
    AuthorizationService(ProfileRepository profileRepository){this.profileRepository = profileRepository;}
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return profileRepository.findByNome(username);
    }
}
