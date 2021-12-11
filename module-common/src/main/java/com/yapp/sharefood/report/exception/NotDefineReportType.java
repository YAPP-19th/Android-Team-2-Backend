package com.yapp.sharefood.report.exception;

public class NotDefineReportType extends RuntimeException {
    public static String NOT_DEFINE_REPORT_EXCEPTION_MSG = "정의되지 않은 신고 사유입니다.";

    public NotDefineReportType() {
        super(NOT_DEFINE_REPORT_EXCEPTION_MSG);
    }

    public NotDefineReportType(String message) {
        super(message);
    }
}
