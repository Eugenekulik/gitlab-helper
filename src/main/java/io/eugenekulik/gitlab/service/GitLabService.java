package io.eugenekulik.gitlab.service;

import io.eugenekulik.gitlab.dao.ConfigStorage;
import io.eugenekulik.gitlab.domain.Notification;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


@Service
public class GitLabService {

  private final NotificationConfiguration notificationConfiguration;
  private final ConfigStorage configStorage;


  public GitLabService(NotificationConfiguration notificationConfiguration,
      ConfigStorage configStorage) {
    this.configStorage = configStorage;
    this.notificationConfiguration = notificationConfiguration;
  }

  public Set<Notification> getNotifications() {
    Set<NotificationStrategy> strategies = notificationConfiguration.getNotificationStrategies();
    Date afrer = configStorage.getConfig("lastUpdated")
        .map(o -> (Date) o)
        .orElse(new Date());
    return strategies.stream()
        .flatMap(notificationStrategy -> notificationStrategy.getNotifications(afrer).stream())
        .collect(Collectors.toSet());
  }
}
