package by.eugenekulik.controller;

import by.eugenekulik.service.GitLabService;
import by.eugenekulik.view.Gui;

import jakarta.annotation.PostConstruct;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GitLabController {

  private static final Logger log = LoggerFactory.getLogger(GitLabController.class);
  @Value("${gitlab.refresh-period}")
  private Integer refreshPeriod;


  private final GitLabService service;
  private final Gui gui;


  @PostConstruct
  private void init() {
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        try {
          service.getNotifications().stream().forEach(gui::showNotification);
        } catch (Exception e) {
          log.error("Error getting notifications", e);
        }
      }
    }, 1000, refreshPeriod);
  }


}
