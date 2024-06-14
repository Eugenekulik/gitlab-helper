package by.eugenekulik.service;

import by.eugenekulik.dao.ConfigStorage;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConfiguration {

  private final ConfigStorage configStorage;
  private final Map<String, NotificationStrategy> allStrategies;

  public void updateNotificationConfiguration(String type, boolean value) {
    Set<NotificationStrategy> notificationStrategies = configStorage.getConfig(
            "notificationStrategies")
        .map(obj -> (Set<NotificationStrategy>) obj)
        .orElse(new HashSet<>());
    if (value) {
      notificationStrategies.add(allStrategies.get(type));
    } else {
      notificationStrategies.remove(allStrategies.get(type));
    }
    configStorage.setConfig("notificationStrategies", notificationStrategies);
  }

  public Set<NotificationStrategy> getNotificationStrategies() {
    return Set.copyOf(
        configStorage.getConfig("notificationStrategies")
            .map(obj -> (Set<NotificationStrategy>) obj)
            .orElse(new HashSet<>()));
  }

}
