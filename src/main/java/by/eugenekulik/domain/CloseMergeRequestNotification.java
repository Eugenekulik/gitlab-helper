package by.eugenekulik.domain;

import org.gitlab4j.api.models.MergeRequest;

public class CloseMergeRequestNotification implements Notification{

  private final MergeRequest mergeRequest;

  public CloseMergeRequestNotification(MergeRequest mergeRequest) {
    this.mergeRequest = mergeRequest;
  }


  @Override
  public String toString() {
    return """
        Merge request closed!
        id: %s
        author: %s
        assignee: %s
        time: %s
        """.formatted(mergeRequest.getId(), mergeRequest.getAuthor(),
        mergeRequest.getAssignee(), mergeRequest.getUpdatedAt());
  }
}
