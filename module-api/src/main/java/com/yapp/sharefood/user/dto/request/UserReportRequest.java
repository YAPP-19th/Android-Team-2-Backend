package com.yapp.sharefood.user.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserReportRequest {
    private String reportMessage;

    @Builder
    public UserReportRequest(String reportMessage) {
        this.reportMessage = reportMessage;
    }
}
