package com.yapp.sharefood.flavor.service;

import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.flavor.dto.request.UserFlavorRequest;
import com.yapp.sharefood.flavor.dto.response.FlavorsResponse;
import com.yapp.sharefood.flavor.dto.response.UpdateUserFlavorResponse;
import com.yapp.sharefood.flavor.repository.FlavorRepository;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import com.yapp.sharefood.userflavor.domain.UserFlavor;
import com.yapp.sharefood.userflavor.repository.UserFlavorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlavorService {
    private final UserRepository userRepository;
    private final FlavorRepository flavorRepository;
    private final UserFlavorRepository userFlavorRepository;

    public FlavorsResponse findAllFlavors() {
        List<FlavorDto> flavors = flavorRepository.findAll().stream()
                .map(flavor -> FlavorDto.of(flavor.getId(), flavor.getFlavorType()))
                .collect(Collectors.toList());
        return new FlavorsResponse(flavors);
    }

    @Transactional
    public UpdateUserFlavorResponse updateUserFlavors(User user, UserFlavorRequest request) {
        List<Flavor> flavors = flavorRepository.findByFlavorTypeIsIn(request.getFlavors().stream().map(FlavorType::of)
                .collect(Collectors.toList()));
        user.updateUserFlavors(new HashSet<>(flavors));

        return UpdateUserFlavorResponse.of(user.getUserFlavors().stream()
                .map(userFlavor -> FlavorDto.of(userFlavor.getId(), userFlavor.getFlavor().getFlavorType()))
                .collect(Collectors.toList()));
    }

    public FlavorsResponse findUserFlavors(User user) {
        User findUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        List<FlavorDto> list = userFlavorRepository.findUserFlavorByUserId(findUser).stream()
                .map(UserFlavor::getFlavor)
                .map(flavor -> FlavorDto.of(flavor.getId(), flavor.getFlavorType())).collect(Collectors.toList());

        return new FlavorsResponse(list);
    }
}
