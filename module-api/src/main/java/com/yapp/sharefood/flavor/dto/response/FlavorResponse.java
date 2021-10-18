package com.yapp.sharefood.flavor.dto.response;

import com.yapp.sharefood.flavor.dto.FlavorDto;
import lombok.Data;

import java.util.List;

@Data
public class FlavorResponse {
    private List<FlavorDto> flavors;
}
