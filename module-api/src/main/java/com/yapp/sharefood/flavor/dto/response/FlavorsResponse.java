package com.yapp.sharefood.flavor.dto.response;

import com.yapp.sharefood.flavor.dto.FlavorDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlavorsResponse {
    private List<FlavorDto> flavors;
}
