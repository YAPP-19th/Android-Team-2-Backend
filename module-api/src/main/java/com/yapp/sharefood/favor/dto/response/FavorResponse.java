package com.yapp.sharefood.favor.dto.response;

import com.yapp.sharefood.favor.dto.FavorDto;
import lombok.Data;

import java.util.List;

@Data
public class FavorResponse {
    private List<FavorDto> flavors;
}
