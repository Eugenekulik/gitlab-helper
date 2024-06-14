package io.eugenekulik.gitlab.service;


import io.eugenekulik.gitlab.domain.Notification;
import java.util.Date;
import java.util.Set;

public interface NotificationStrategy {

  Set<Notification> getNotifications(Date after);
}
