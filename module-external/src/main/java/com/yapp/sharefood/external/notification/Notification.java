package com.yapp.sharefood.external.notification;

import com.yapp.sharefood.external.notification.dto.NotificationMessageDto;


public interface Notification {
    void postErrorNotification(NotificationMessageDto notificationMessageDto);
}
