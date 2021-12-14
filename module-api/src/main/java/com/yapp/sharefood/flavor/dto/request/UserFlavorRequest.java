package com.yapp.sharefood.flavor.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFlavorRequest {
    @NotNull
    private List<String> flavors;

    public static UserFlavorRequest of(List<String> flavors) {
        return new UserFlavorRequest(flavors);
    }
}
