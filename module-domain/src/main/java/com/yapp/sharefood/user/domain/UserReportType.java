package com.yapp.sharefood.user.domain;

import com.yapp.sharefood.report.exception.NotDefineReportType;

import java.util.Arrays;

public enum UserReportType {
    /**
     * Point가 1인 경우, 일반적인 사유
     * Point가 99인 경우, 특수한 사유
     * <p>
     * 일반적인 사유로 FOOD가 3번 신고 당한 경우, FOOD REPORT STATUS -> JUDGED
     * 특수한 사유로 FOOD가 신고 당한 경우, FOOD REPORT STATUS -> BANNDED
     * <p>
     * POSTING_FOOD_JUDGED -> FOOD가 일반적인 사유로 여러번 신고 당해 User에 영향을 끼치는 경우
     * POSTING_FOOD_BANNDED -> FOOD가 특수한 사유로 신고 당해 User에 영향으 끼치는 경우
     **/
    POSTING_DUPLICATED_USER("반복적으로 중복된 레시피를 게시하였습니다.", 1),
    POSTING_ADVERTISING_USER("반복적으로 광고성 레시피를 게시하였습니다.", 1),
    POSTING_NO_RELATION_USER("반복적으로 레시피와 관련없는 내용을 게시하였습니다.", 1),
    POSTING_WRONG_USER("반복적으로 잘못된 사실을 게시하였습니다.", 1),
    POSTING_ETC_USER("기타", 1),
    POSTING_SADISTIC_AND_HARMFUL_USER("반복적으로 가학적이거나 유해한 내용을 게시하였습니다.", 99),
    POSTING_OBSCENE_USER("반복적으로 음란물을 게시하였습니다.", 99),

    POSTING_FOOD_JUDGED("POSTING_FOOD_JUDGED", 1),
    POSTING_FOOD_BANNDED("POSTING_FOOD_BANNDED", 99);

    UserReportType(String message, int point) {
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

    public static UserReportType getFoodReportType(String message) {
        return Arrays.stream(values()).filter(type -> type.getMessage().equals(message)).findFirst().orElseThrow(NotDefineReportType::new);
    }
}
