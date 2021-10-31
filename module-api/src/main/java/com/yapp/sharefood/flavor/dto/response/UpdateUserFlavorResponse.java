package com.yapp.sharefood.flavor.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class UpdateUserFlavorResponse {
    private int updateSuccessCount;

    private UpdateUserFlavorResponse(int resultCount) {
        this.updateSuccessCount = resultCount;
    }

    public static UpdateUserFlavorResponse of(int resultCount) {
        return new UpdateUserFlavorResponse(resultCount);
    }
}
