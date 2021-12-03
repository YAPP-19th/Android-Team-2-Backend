package com.yapp.sharefood.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GradeTest {

    @DisplayName("등급 구하기 로직 성공 : 정상적인 경우")
    @Test
    void gradeByPointTest_Success_Normar() {
        //given
        int point1 = 1700;
        int point2 = 1000;

        //when
        Grade grade1 = Grade.gradeByPoint(point1);
        Grade grade2 = Grade.gradeByPoint(point2);

        //then
        assertEquals(Grade.EXPERT, grade1);
        assertEquals(Grade.MASTER, grade2);
    }

    @DisplayName("등급 구하기 로직 성공 : 비정상적인 점수로 기본 등급 배정")
    @Test
    void gradeByPointTest_Success_Not_Normal() {
        //given
        int point = -1;

        //when
        Grade grade = Grade.gradeByPoint(point);

        //then
        assertEquals(Grade.STUDENT, grade);
    }

    @DisplayName("점수 획등 가능 상태")
    @Test
    void canEarnPoint_True() {
        //given
        Grade grade1 = Grade.MASTER;
        Grade grade2 = Grade.BACHELOR;

        //when

        //then
        assertEquals(true, Grade.canEarnPoint(grade1));
        assertEquals(true, Grade.canEarnPoint(grade2));
    }

    @DisplayName("점수 획등 불가능 상태")
    @Test
    void canEarnPoint_False() {
        //given
        Grade grade = Grade.PROFESSOR;

        //when

        //then
        assertEquals(false, Grade.canEarnPoint(grade));
    }
}