package com.yapp.sharefood.user.dto.response;

import com.yapp.sharefood.user.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private UserInfoDto userInfo;
}
