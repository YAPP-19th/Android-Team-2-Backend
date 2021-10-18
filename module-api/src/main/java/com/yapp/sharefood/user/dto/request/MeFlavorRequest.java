package com.yapp.sharefood.user.dto.request;

import com.yapp.sharefood.flavor.dto.FlavorDto;
import lombok.Data;

import java.util.List;

@Data
public class MeFlavorRequest {
    private List<FlavorDto> flavors;
}
