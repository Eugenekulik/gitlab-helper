package by.eugenekulik.service;

import by.eugenekulik.domain.NoteNotification;
import by.eugenekulik.domain.Notification;
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
public class NoteNotificationStrategy implements NotificationStrategy{

  private final GitLabApi gitLabApi;

  @Override
  public Set<Notification> getNotifications(Date after) {
    try {
      return gitLabApi.getProjectApi().getMemberProjectsStream()
          .flatMap(project -> {
            try {
              return gitLabApi.getMergeRequestApi()
                  .getMergeRequestsStream(new MergeRequestFilter()
                      .withProjectId(project.getId())
                      .withState(MergeRequestState.OPENED)
                      .withCreatedAfter(after));
            } catch (GitLabApiException e) {
              log.error("error occurred while getting merge requests for project: {}:{}",
                  project.getId(),project.getName(), e);
            }
            return Stream.empty();
          })
          .flatMap(mergeRequest -> {
            try {
              return gitLabApi.getNotesApi()
                  .getMergeRequestNotesStream(mergeRequest.getProjectId(), mergeRequest.getIid())
                  .filter(note -> note.getCreatedAt().after(after));
            } catch (GitLabApiException e) {
              log.error("error occurred while getting merge requests notes: {}:{}",
                  mergeRequest.getIid(),mergeRequest.getTitle(), e);
            }
            return Stream.empty();
          })

          .map(NoteNotification::new)
          .collect(Collectors.toSet());
    } catch (GitLabApiException ex) {
      log.error("Error occurred while getting members projects", ex);
    }



    return Set.of();
  }
}
