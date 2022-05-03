package com.yapp.sharefood.external.notification.slack;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SlackNotificationMessageType {
    ERROR_MESSAGE(
            "[에러 코드]\n" +
                    "%s\n" +
                    "[에러 메시지]\n" +
                    "%s\n" +
                    "[에러 발생시간]\n" +
                    "%s\n"
    ),
    INFO_MESSAGE(
            "[메시지]\n" +
                    "%s\n" +
                    "[발생시간]\n" +
                    "%s\n"
    );

    private final String messageTemplate;

    public String getMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
