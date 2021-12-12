package com.yapp.sharefood.food.domain;

public enum FoodReportStatus {
    /**
     * NORMAL : 정상적인 FOOD
     * JUDGED : 3번 신고당하여 비공개 처리된 FOOD
     * BANNDED : 정지 처리된 FOOD
     **/
    NORMAL, JUDGED, BANNDED;

    public static FoodReportStatus getReportStatus(int reportPoint) {
        if (reportPoint >= 99) {
            return BANNDED;
        } else if (reportPoint >= 3) {
            return JUDGED;
        } else {
            return NORMAL;
        }
    }
}
