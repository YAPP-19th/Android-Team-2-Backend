package com.yapp.sharefood.external.notification.dto;

public class SlackNotificationMessageDto implements NotificationMessageDto {
    private final String text;

    public SlackNotificationMessageDto(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
