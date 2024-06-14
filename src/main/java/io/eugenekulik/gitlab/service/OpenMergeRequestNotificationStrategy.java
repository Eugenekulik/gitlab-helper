package io.eugenekulik.gitlab.service;

import io.eugenekulik.gitlab.domain.Notification;
import io.eugenekulik.gitlab.domain.OpenMergeRequestNotification;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.Constants.MergeRequestState;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequestFilter;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenMergeRequestNotificationStrategy implements NotificationStrategy {

  private final GitLabApi gitLabApi;


  @Override
  public Set<Notification> getNotifications(Date after) {
    try {
      return gitLabApi.getProjectApi().getMemberProjects().stream()
          .flatMap(project -> {
            try {
              return gitLabApi.getMergeRequestApi()
                  .getMergeRequests(new MergeRequestFilter()
                      .withProjectId(project.getId())
                      .withState(MergeRequestState.OPENED)
                      .withCreatedAfter(after)).stream();
            } catch (GitLabApiException e) {
              log.error("error occurred while getting merge requests for project: {}:{}",
                  project.getId(),project.getName(), e);
            }
            return Stream.empty();
          })
          .map(OpenMergeRequestNotification::new)
          .collect(Collectors.toSet());
    } catch (GitLabApiException e) {
      log.error("error occurred while getting members projects");
    }
    return Collections.emptySet();
  }
}


