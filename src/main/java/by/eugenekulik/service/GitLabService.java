package by.eugenekulik.service;

import by.eugenekulik.dao.ConfigStorage;
import by.eugenekulik.domain.Notification;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import org.gitlab4j.api.GitLabApi;
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
