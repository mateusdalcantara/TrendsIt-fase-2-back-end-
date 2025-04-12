package com.trendsit.trendsit_fase2.service.relationship;

import com.trendsit.trendsit_fase2.repository.profile.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class FriendNumberService {

    private static final long MIN_NUMBER = 1L; // 1 dígito.
    private static final long MAX_NUMBER = 999_999_999_999L; // 12 dígitos

    @Autowired
    private ProfileRepository profileRepository;

    public Long generateUniqueFriendNumber() {
        Long friendNumber;
        do {
            friendNumber = ThreadLocalRandom.current().nextLong(MIN_NUMBER, MAX_NUMBER);
        } while (profileRepository.existsByFriendNumber(friendNumber));
        return friendNumber;
    }
}