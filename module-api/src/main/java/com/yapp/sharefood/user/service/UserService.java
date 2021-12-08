package com.yapp.sharefood.user.service;

import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.dto.OtherUserInfoDto;
import com.yapp.sharefood.user.dto.UserInfoDto;
import com.yapp.sharefood.user.dto.request.UserNicknameRequest;
import com.yapp.sharefood.user.dto.response.MyUserInfoResponse;
import com.yapp.sharefood.user.dto.response.OtherUserInfoResponse;
import com.yapp.sharefood.user.dto.response.UserNicknameResponse;
import com.yapp.sharefood.user.exception.UserNicknameExistException;
import com.yapp.sharefood.user.rand.UserNicknameRandomComponent;
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
    private final UserNicknameRandomComponent userNicknameRandomComponent;

    public UserNicknameResponse createUniqueNickname() {
        String newNickname = PREFIX_DEFAULT_NICKNAME + userNicknameRandomComponent.createRandomUserNickname();
        validateNicknameDuplicate(newNickname);

        return new UserNicknameResponse(newNickname);
    }

    public void checkNicknameDuplicate(String nickname) {
        validateNicknameDuplicate(nickname);
    }

    private void validateNicknameDuplicate(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new UserNicknameExistException();
        }
    }

    @Transactional
    public UserNicknameResponse changeUserNickname(Long userId, UserNicknameRequest request) {
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserNicknameExistException();
        }

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.changeNickname(request.getNickname());

        return new UserNicknameResponse(user.getNickname());
    }

    public MyUserInfoResponse findUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return new MyUserInfoResponse(UserInfoDto.of(user));
    }

    public OtherUserInfoResponse findOtherUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return new OtherUserInfoResponse(OtherUserInfoDto.of(user.getId(), user.getNickname()));
    }
}
