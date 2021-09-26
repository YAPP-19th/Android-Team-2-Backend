package com.yapp.sharefood.user.service;

import com.yapp.sharefood.common.random.RandomStringCreator;
import com.yapp.sharefood.user.exception.UserNicknameExistException;
import com.yapp.sharefood.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private static final String PREFIX_DEFAULT_NICKNAME = "냠냠학사 ";

    private final UserRepository userRepository;
    private final RandomStringCreator randomStringCreator;

    public String createUniqueNickname() {
        String newNickname = PREFIX_DEFAULT_NICKNAME + randomStringCreator.createRandomUUIDStr();
        if (userRepository.existsByNickname(newNickname)) {
            throw new UserNicknameExistException("Default Nickname을 생성하지 못 하였습니다.");
        }

        return newNickname;
    }
}
