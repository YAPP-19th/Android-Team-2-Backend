package com.yapp.sharefood.flavor.dto.request;

import com.yapp.sharefood.flavor.dto.FlavorDto;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserFlavorRequest {
    @NotNull
    private List<FlavorDto> flavors;
}
