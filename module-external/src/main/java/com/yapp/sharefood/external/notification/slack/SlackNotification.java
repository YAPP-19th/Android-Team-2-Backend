package com.yapp.sharefood.external.notification.slack;

import com.yapp.sharefood.config.feign.SlackFeignConfig;
import com.yapp.sharefood.external.notification.Notification;
import com.yapp.sharefood.external.notification.dto.NotificationMessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "slackNotification",
        url = "${slack.base-url}",
        configuration = {
                SlackFeignConfig.class
        },
        primary = false
)
public interface SlackNotification extends Notification {
    @PostMapping("${slack.token}")
    void postErrorNotification(NotificationMessageDto notificationMessageDto);
}
