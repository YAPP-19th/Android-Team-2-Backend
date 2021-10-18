package com.yapp.sharefood.flavor.service;

import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.flavor.dto.response.FlavorsResponse;
import com.yapp.sharefood.flavor.repository.FlavorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlavorService {
    private final FlavorRepository flavorRepository;

    public FlavorsResponse findAllFlavors() {
        List<FlavorDto> flavors = flavorRepository.findAll().stream()
                .map(flavor -> FlavorDto.of(flavor.getId(), flavor.getFlavorType()))
                .collect(Collectors.toList());
        return new FlavorsResponse(flavors);
    }
}
