package com.yapp.sharefood.user.domain;

public enum Grade {
    STUDENT(0), BACHELOR(300), MASTER(700), EXPERT(1200), PROFESSOR(1800);

    Grade(int condition) {
        this.condition = condition;
    }

    private int condition;

    private Grade upgrade(int point) {
        if (this.ordinal() == values().length - 1) {
            return PROFESSOR;
        }
        int nextOrdinal = this.ordinal() + 1;
        if (point >= values()[nextOrdinal].condition) return values()[nextOrdinal];
        else return this;
    }
}
