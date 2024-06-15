package io.eugenekulik.gitlab.controller;

import io.eugenekulik.gitlab.service.GitLabService;
import io.eugenekulik.gitlab.view.Gui;
import jakarta.annotation.PostConstruct;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GitLabController {

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
          gui.updateMergeRequestMenu(service.getOpenedMergeRequests());
        } catch (Exception e) {
          log.error("Error occurred in scheduler", e);
        }
      }
    }, 1000, refreshPeriod);
  }


}
