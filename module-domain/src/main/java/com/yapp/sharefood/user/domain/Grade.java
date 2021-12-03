package com.yapp.sharefood.user.domain;

public enum Grade {
    STUDENT(0), BACHELOR(300), MASTER(700), EXPERT(1200), PROFESSOR(1800);

    Grade(int condition) {
        this.condition = condition;
    }

    public static final int POINT_REGISTER_FOOD = 50;
    public static final int POINT_OPEN_FOOD = 10;

    private int condition;

    public static Grade upgrade(int point) {
        for(Grade grade : values()) {
            if(point >= grade.condition) return grade;
        }
        return values()[0];
    }

    public static boolean canEarnPoint(Grade grade) {
        return !grade.name().equals(PROFESSOR.name());
    }
}
