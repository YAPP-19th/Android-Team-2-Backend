package com.yapp.sharefood.user.domain;


public enum UserReportStatus {
    /**
     * NORMAL : 정상적인 FOOD
     * BANNDED : 정지 처리된 FOOD
     **/
    NORMAL, BANNDED;

    public static UserReportStatus getReportStatus(int reportPoint) {
        if (reportPoint >= 5) {
            return BANNDED;
        } else {
            return NORMAL;
        }
    }
}
