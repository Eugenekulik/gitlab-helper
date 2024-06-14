package io.eugenekulik.gitlab.view;

import io.eugenekulik.gitlab.domain.Notification;
import io.eugenekulik.gitlab.service.NotificationConfiguration;
import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class Gui {

  private final NotificationConfiguration notificationConfiguration;
  private final TrayIcon trayIcon;

  public Gui(NotificationConfiguration notificationConfiguration) {
    this.notificationConfiguration = notificationConfiguration;
    try {
      SystemTray tray = SystemTray.getSystemTray();
      Image image = Toolkit.getDefaultToolkit()
          .createImage(getClass().getResource("/logo.png"));

      trayIcon = new TrayIcon(image, "Gitlab helper");
      trayIcon.setImageAutoSize(true);
      trayIcon.setToolTip("Gitlab helper");
      MenuItem exit = new MenuItem("Exit");
      exit.addActionListener(e -> System.exit(0));
      PopupMenu popup = new PopupMenu();
      popup.add(createNotificationConfigMenu());
      popup.addSeparator();
      popup.add(exit);
      trayIcon.setPopupMenu(popup);
      tray.add(trayIcon);
    } catch (AWTException e) {
      throw new RuntimeException(e);
    }
  }

  private MenuItem createNotificationConfigMenu() {
    Menu menu = new Menu("Configure notifications");
    CheckboxMenuItem openMergeRequesst = new CheckboxMenuItem("Open merge request");
    CheckboxMenuItem closeMergeRequest = new CheckboxMenuItem("Close merge request");
    CheckboxMenuItem notes = new CheckboxMenuItem("Notes");
    openMergeRequesst.addItemListener(e-> notificationConfiguration
        .updateNotificationConfiguration("openMergeRequestNotificationStrategy", e.getStateChange() == 1));
    closeMergeRequest.addItemListener(e-> notificationConfiguration
        .updateNotificationConfiguration("closeMergeRequestNotificationStrategy", e.getStateChange() == 1));
    notes.addItemListener(e-> notificationConfiguration
        .updateNotificationConfiguration("noteNotificationStrategy", e.getStateChange() == 1));
    menu.add(openMergeRequesst);
    menu.add(closeMergeRequest);
    menu.add(notes);
    return menu;
  }


  public void showNotification(Notification notification) {
    log.info(notification.toString());
    trayIcon.displayMessage("Gitlab", notification.toString(), TrayIcon.MessageType.INFO);
  }

}
