package io.eugenekulik.gitlab.domain;

import org.gitlab4j.api.models.MergeRequest;

public class OpenMergeRequestNotification implements Notification{

  private final MergeRequest mergeRequest;

  public OpenMergeRequestNotification(MergeRequest mergeRequest) {
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
