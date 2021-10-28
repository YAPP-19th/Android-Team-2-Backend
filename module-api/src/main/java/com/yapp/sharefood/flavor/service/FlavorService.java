package com.yapp.sharefood.flavor.service;

import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.flavor.dto.request.UserFlavorRequest;
import com.yapp.sharefood.flavor.dto.response.FlavorsResponse;
import com.yapp.sharefood.flavor.exception.FlavorNotFoundException;
import com.yapp.sharefood.flavor.repository.FlavorRepository;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.userflavor.domain.UserFlavor;
import com.yapp.sharefood.userflavor.repository.UserFlavorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlavorService {
    private final FlavorRepository flavorRepository;
    private final UserFlavorRepository userFlavorRepository;

    public FlavorsResponse findAllFlavors() {
        List<FlavorDto> flavors = flavorRepository.findAll().stream()
                .map(flavor -> FlavorDto.of(flavor.getId(), flavor.getFlavorType()))
                .collect(Collectors.toList());
        return new FlavorsResponse(flavors);
    }

    @Transactional
    public int updateUserFlavors(User user, UserFlavorRequest request) {
        userFlavorRepository.deleteUserFlavorsByUser(user);

        ArrayList<UserFlavor> userFlavors = new ArrayList<>();
        for(FlavorDto flavorDto : request.getFlavors()) {
            Flavor flavor = flavorRepository.findById(flavorDto.getId()).orElseThrow(FlavorNotFoundException::new);
            userFlavors.add(UserFlavor.of(user, flavor));
        }

        int saveResultSize = userFlavorRepository.saveAll(userFlavors).size();
        return saveResultSize;
    }
}
