package com.yapp.sharefood.common.listener;

import com.yapp.sharefood.common.domain.ApiErrorEvent;
import com.yapp.sharefood.external.notification.Notification;
import com.yapp.sharefood.external.notification.dto.NotificationMessageDto;
import com.yapp.sharefood.external.notification.dto.NotificationMessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final Notification notification;

    @Async
    @EventListener
    public void sendErrorNotification(ApiErrorEvent event) {
        log.info("event={}", event);
        NotificationMessageDto notificationMessageDto = NotificationMessageFactory.ofError(event);
        notification.postErrorNotification(notificationMessageDto);
    }
}
