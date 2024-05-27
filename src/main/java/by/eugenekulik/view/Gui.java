package by.eugenekulik.view;

import by.eugenekulik.domain.Notification;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;

public class Gui {

  private final TrayIcon trayIcon;

  public Gui() {
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
      popup.add(exit);
      trayIcon.setPopupMenu(popup);
      tray.add(trayIcon);
    } catch (AWTException e) {
      throw new RuntimeException(e);
    }
  }


  public void showNotification(Notification notification) {
    trayIcon.displayMessage("Gitlab", notification.toString(), TrayIcon.MessageType.INFO);
  }

}
