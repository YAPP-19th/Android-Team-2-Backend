package com.yapp.sharefood.flavor.dto.request;

import com.yapp.sharefood.flavor.dto.FlavorDto;
import lombok.Data;

import java.util.List;

@Data
public class UserFlavorRequest {
    private List<FlavorDto> flavors;
}
