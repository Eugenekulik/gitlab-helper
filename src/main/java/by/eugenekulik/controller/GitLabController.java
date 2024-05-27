package by.eugenekulik.controller;

import by.eugenekulik.service.GitLabService;
import by.eugenekulik.view.Gui;

import java.util.*;
import org.gitlab4j.api.GitLabApi;

public class GitLabController {


  private final GitLabService service;
  private final Gui gui;


  public GitLabController(GitLabService service, Gui gui) {
    this.service = service;
    this.gui = gui;
    init();
  }

  public static GitLabController run() {
    return new GitLabController(
        new GitLabService(
            new GitLabApi("https://git.yiilab.com/",
                System.getenv("GITLAB_TOKEN"))),
        new Gui());
  }

  private void init() {
    service.getNotifications();
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        service.getNotifications().stream().forEach(gui::showNotification);
      }
    }, 1000, 5000);
  }


}
