package com.yapp.sharefood.external.notification.dto;

import com.yapp.sharefood.common.domain.ApiErrorEvent;
import com.yapp.sharefood.external.notification.slack.SlackNotificationMessageType;

public class NotificationMessageFactory {
    public static NotificationMessageDto ofError(ApiErrorEvent event) {
        String message = SlackNotificationMessageType.ERROR_MESSAGE.getMessage(event.getCode(), event.getMessage(), event.getErrorTime());
        return new SlackNotificationMessageDto(message);
    }
}
