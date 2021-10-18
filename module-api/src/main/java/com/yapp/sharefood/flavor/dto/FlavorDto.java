package com.yapp.sharefood.flavor.dto;

import com.yapp.sharefood.flavor.domain.FlavorType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FlavorDto {
    private Long id;
    private String flavorName;

    private FlavorDto(Long id, String flavorName) {
        this.id = id;
        this.flavorName = flavorName;
    }

    public static FlavorDto of(Long id, FlavorType flavorType) {
        return new FlavorDto(id, flavorType.getFlavorName());
    }
}
