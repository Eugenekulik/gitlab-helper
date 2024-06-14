package io.eugenekulik.gitlab.service;

import io.eugenekulik.gitlab.dao.ConfigStorage;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class NotificationConfiguration {


  public static final String NOTIFICATION_STRATEGIES = "notificationStrategies";
  private final ConfigStorage configStorage;
  private final Map<String, NotificationStrategy> allStrategies;

  public NotificationConfiguration(
      @Qualifier("simpleFileConfigStorage") ConfigStorage configStorage,
      Map<String, NotificationStrategy> allStrategies) {
    this.configStorage = configStorage;
    this.allStrategies = allStrategies;
  }

  public void updateNotificationConfiguration(String type, boolean value) {
    if(allStrategies.get(type) == null) {
      throw new IllegalArgumentException("not found strategy for type " + type);
    }
    Set<String> notificationStrategies = configStorage
        .getConfig(NOTIFICATION_STRATEGIES)
        .map(o -> (Set<String>) o)
        .orElse(new HashSet<>());
    if (value) {
      notificationStrategies.add(type);
    } else {
      notificationStrategies.remove(type);
    }
    configStorage.setConfig(NOTIFICATION_STRATEGIES, notificationStrategies);
  }

  public Set<NotificationStrategy> getNotificationStrategies() {
    return configStorage.getConfig(NOTIFICATION_STRATEGIES)
            .map(obj -> (Set<String>) obj)
            .stream()
            .flatMap(set -> set.stream().map(allStrategies::get))
            .collect(Collectors.toSet());
  }

  public Set<String> getTypeNames() {
    return configStorage.getConfig(NOTIFICATION_STRATEGIES)
        .map(obj -> (Set<String>) obj)
        .orElse(new HashSet<>());
  }

}
