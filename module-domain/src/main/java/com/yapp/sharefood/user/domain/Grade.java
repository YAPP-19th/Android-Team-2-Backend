package com.yapp.sharefood.user.domain;

public enum Grade {
    STUDENT(0), BACHELOR(300), MASTER(700), EXPERT(1200), PROFESSOR(1800);

    Grade(int condition) {
        this.condition = condition;
    }

    public static final int POINT_WEEKLY_RANK = 100;
    public static final int POINT_REGISTER_FOOD = 50;
    public static final int POINT_OPEN_FOOD = 10;

    private int condition;

    private Grade upgrade(int point) {
        if (this.ordinal() == values().length - 1) {
            return this;
        }
        int nextOrdinal = this.ordinal() + 1;
        if (point >= values()[nextOrdinal].condition) return values()[nextOrdinal];
        else return this;
    }
}
