package by.eugenekulik.domain;

import org.gitlab4j.api.models.MergeRequest;

public class MergeRequestNotification implements Notification{

  private final MergeRequest mergeRequest;

  public MergeRequestNotification(MergeRequest mergeRequest) {
    this.mergeRequest = mergeRequest;
  }


  @Override
  public String toString() {
    return """
        New merge request!
        identifier: %s
        author: %s
        title: %s
       """.formatted(mergeRequest.getId(),
        mergeRequest.getAuthor().getName(),
        mergeRequest.getTitle());
  }
}
