package by.eugenekulik.service;

import by.eugenekulik.domain.CloseMergeRequestNotification;
import by.eugenekulik.domain.OpenMergeRequestNotification;
import by.eugenekulik.domain.Notification;
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
public class CloseMergeRequestNotificationStrategy implements NotificationStrategy{

  private final GitLabApi gitLabApi;

  @Override
  public Set<Notification> getNotifications(Date after) {
    try {
      return gitLabApi.getProjectApi().getMemberProjectsStream()
          .flatMap(project -> {
            try {
              return gitLabApi.getMergeRequestApi().getMergeRequestsStream(
                  new MergeRequestFilter()
                      .withProjectId(project.getId())
                      .withState(MergeRequestState.CLOSED)
                      .withUpdatedAfter(after));
            } catch (GitLabApiException e) {
              log.error("Error occurred while getting merge requests for project: {}:{}",
                  project.getId(), project.getName(), e);
            }
            return Stream.empty();
          })
          .map(CloseMergeRequestNotification::new)
          .collect(Collectors.toSet());
    } catch (GitLabApiException e) {
      log.error("Error occurred while getting members projects", e);
    }
    return Collections.emptySet();
  }
}
