package com.yapp.sharefood.flavor.dto.response;

import com.yapp.sharefood.flavor.dto.FlavorDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class UpdateUserFlavorResponse {
    private List<FlavorDto> flavors;

    private UpdateUserFlavorResponse(List<FlavorDto> flavors) {
        this.flavors = flavors;
    }

    public static UpdateUserFlavorResponse of(List<FlavorDto> flavors) {
        return new UpdateUserFlavorResponse(flavors);
    }
}
