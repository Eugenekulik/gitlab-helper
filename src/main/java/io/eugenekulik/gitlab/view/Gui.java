package io.eugenekulik.gitlab.view;

import io.eugenekulik.gitlab.domain.Notification;
import io.eugenekulik.gitlab.service.NotificationConfiguration;
import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.models.MergeRequest;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class Gui {

  private final NotificationConfiguration notificationConfiguration;
  private final TrayIcon trayIcon;
  private final Menu mergeRequestMenu;

  public Gui(NotificationConfiguration notificationConfiguration) {
    this.notificationConfiguration = notificationConfiguration;
    try {
      System.setProperty("java.awt.headless", "false");
      SystemTray tray = SystemTray.getSystemTray();
      Image image = Toolkit.getDefaultToolkit()
          .createImage(getClass().getResource("/logo.png"));
      mergeRequestMenu = new Menu("Merge Requests");
      trayIcon = new TrayIcon(image, "Gitlab helper");
      trayIcon.setImageAutoSize(true);
      trayIcon.setToolTip("Gitlab helper");
      MenuItem exit = new MenuItem("Exit");
      exit.addActionListener(e -> System.exit(0));
      PopupMenu popup = new PopupMenu();
      popup.add(mergeRequestMenu);
      popup.add(createNotificationConfigMenu());
      popup.addSeparator();
      popup.add(exit);
      trayIcon.setPopupMenu(popup);
      tray.add(trayIcon);
    } catch (AWTException e) {
      throw new RuntimeException(e);
    }
  }

  public void updateMergeRequestMenu(Set<MergeRequest> openMR) {
    openMR.stream().forEach(mergeRequest -> {
      for(int i = 0; i < mergeRequestMenu.getItemCount(); i++) {
        if (mergeRequestMenu.getItem(i).getName().equals(mergeRequest.getTitle())) {
          return;
        }
      }
      MenuItem menuItem = new MenuItem(mergeRequest.getTitle());
      menuItem.addActionListener(event -> {
        try {
          Desktop.getDesktop().browse(URI.create(mergeRequest.getWebUrl()));
        } catch (IOException e) {
          log.error("Error while open merge request page", e);
        }
      });
      mergeRequestMenu.add(menuItem);
    });
  }



  private MenuItem createNotificationConfigMenu() {
    Set<String> strategies = notificationConfiguration.getTypeNames();
    Menu menu = new Menu("Configure notifications");
    CheckboxMenuItem openMergeRequesst = new CheckboxMenuItem("Open merge request",
        strategies.contains("openMergeRequestNotificationStrategy"));
    CheckboxMenuItem closeMergeRequest = new CheckboxMenuItem("Close merge request",
        strategies.contains("closeMergeRequestNotificationStrategy"));
    CheckboxMenuItem notes = new CheckboxMenuItem("Notes",
        strategies.contains("noteNotificationStrategy"));
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
