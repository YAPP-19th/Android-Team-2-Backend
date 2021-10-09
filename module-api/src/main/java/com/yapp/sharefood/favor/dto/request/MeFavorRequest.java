package com.yapp.sharefood.favor.dto.request;

import com.yapp.sharefood.favor.dto.FavorDto;
import lombok.Data;

import java.util.List;

@Data
public class MeFavorRequest {
    private List<FavorDto> favors;
}
