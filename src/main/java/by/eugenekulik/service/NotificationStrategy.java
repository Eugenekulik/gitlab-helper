package by.eugenekulik.service;


import by.eugenekulik.domain.Notification;
import java.util.Date;
import java.util.Set;

public interface NotificationStrategy {

  Set<Notification> getNotifications(Date after);
}
