package com.yapp.sharefood.user.dto.response;

import com.yapp.sharefood.user.dto.OtherUserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtherUserInfoResponse {
    private OtherUserInfoDto userInfo;
}
