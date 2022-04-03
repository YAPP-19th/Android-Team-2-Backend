package com.yapp.sharefood.local;

import com.yapp.sharefood.auth.token.TokenProvider;
import com.yapp.sharefood.auth.utils.AuthUtils;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@Profile("local")
@RequiredArgsConstructor
public class MockController {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @PostMapping("/mockUser/{id}")
    public ResponseEntity<Void> mockUser(@PathVariable("id") Long id, HttpServletResponse response) {
        User findUser = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        String token = tokenProvider.createToken(findUser);
        AuthUtils.setTokenInHeader(response, token);

        return ResponseEntity.ok().build();
    }
}
