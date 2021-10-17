package com.yapp.sharefood.flavor.service;

import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.dto.FlavorDto;
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

    public List<FlavorDto> findAllFlavors() {
        List<Flavor> flavors = flavorRepository.findAll();
        return flavors.stream()
                .map(flavor -> FlavorDto.of(flavor.getId(), flavor.getFlavorType()))
                .collect(Collectors.toList());
    }
}
