package com.yapp.sharefood.food.domain;

import com.yapp.sharefood.report.exception.NotDefineReportType;

import java.util.Arrays;

public enum FoodReportType {
    /**
     Point가 1인 경우, 일반적인 사유
     Point가 99인 경우, 특수한 사유

     일반적인 사유로 FOOD가 3번 신고 당한 경우, FOOD REPORT STATUS -> JUDGED
     특수한 사유로 FOOD가 신고 당한 경우, FOOD REPORT STATUS -> BANNDED
     **/
    POSTING_DUPLICATED_CONTENT("중복된 레시피입니다.", 1),
    POSTING_ADVERTISING_CONTENT("광고 내용을 포함하고 있습니다.", 1),
    POSTING_NO_RELATION_CONTENT("레시피와 관련없는 내용을 포함하고 있습니다.", 1),
    POSTING_WRONG_CONTENT("잘못된 사실을 포함하고 있습니다.", 1),
    POSTING_ETC_CONTENT("기타", 1),
    POSTING_SADISTIC_AND_HARMFUL_CONTENT("가학적이거나 유해한 내용을 포함하고 있습니다.", 99),
    POSTING_OBSCENE_CONTENT("음란물을 포함하고 있습니다.", 99);

    FoodReportType(String message, int point) {
        this.message = message;
        this.point = point;
    }

    private final String message;
    private final int point;

    public String getMessage() {
        return message;
    }

    public int getPoint() {
        return point;
    }

    public static FoodReportType getFoodReportType(String message) {
        return Arrays.stream(values()).filter(type -> type.getMessage().equals(message)).findFirst().orElseThrow(NotDefineReportType::new);
    }
}
