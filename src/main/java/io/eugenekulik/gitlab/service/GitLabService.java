package io.eugenekulik.gitlab.service;

import io.eugenekulik.gitlab.dao.ConfigStorage;
import io.eugenekulik.gitlab.domain.Notification;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.Constants.MergeRequestState;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.MergeRequestFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class GitLabService {

  private final NotificationConfiguration notificationConfiguration;
  private final ConfigStorage configStorage;
  private final GitLabApi gitLabApi;


  public GitLabService(NotificationConfiguration notificationConfiguration,
      @Qualifier("simpleFileConfigStorage") ConfigStorage configStorage, GitLabApi gitLabApi) {
    this.configStorage = configStorage;
    this.notificationConfiguration = notificationConfiguration;
    this.gitLabApi = gitLabApi;
  }

  public Set<Notification> getNotifications() {
    Set<NotificationStrategy> strategies = notificationConfiguration.getNotificationStrategies();
    Date afrer = configStorage.getConfig("lastUpdated")
        .map(o -> (Date) o)
        .orElse(new Date());
    return strategies.stream()
        .flatMap(notificationStrategy -> notificationStrategy.getNotifications(afrer).stream())
        .collect(Collectors.toSet());
  }


  public Set<MergeRequest> getOpenedMergeRequests() {
    Set<MergeRequest> openMR = new HashSet<>();
    try {
      gitLabApi.getProjectApi().getMemberProjects()
          .forEach(project -> {
            try {
              openMR.addAll(
              gitLabApi.getMergeRequestApi()
                  .getMergeRequests(new MergeRequestFilter()
                      .withProjectId(project.getId())
                      .withState(MergeRequestState.OPENED)));
            } catch (GitLabApiException e) {
              log.error("Error occurred while getting merge requests", e);
            }
          });
    } catch (GitLabApiException e) {
      log.error("Error occurred while getting member projects", e);
    }
    return openMR;
  }


}
