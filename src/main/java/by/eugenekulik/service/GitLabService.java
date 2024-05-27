package by.eugenekulik.service;

import by.eugenekulik.domain.MergeRequestNotification;
import by.eugenekulik.domain.NoteNotification;
import by.eugenekulik.domain.Notification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gitlab4j.api.Constants.MergeRequestState;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.MergeRequestFilter;
import org.gitlab4j.api.models.Note;
import org.gitlab4j.api.models.Project;


public class GitLabService {

  private final GitLabApi gitLabApi;
  private Map<Long, MergeRequest> mergeRequests;
  private Map<Long, Note> notes;

  private Set<Notification> notifications;

  public GitLabService(GitLabApi gitLabApi) {
    this.gitLabApi = gitLabApi;
    mergeRequests = new HashMap<>();
    notes = new HashMap<>();
    notifications = new HashSet<>();
  }


  public Set<Notification> getNotifications() {
    try {
      //get all member projects
      List<Project> projects = gitLabApi.getProjectApi().getMemberProjects();

      //init temporary collections
      List<MergeRequest> allMrs = new ArrayList<>();
      List<Note> allNotes = new ArrayList<>();

      //get all active merge requests and notes from all projects
      allMrs.addAll(projects.stream()
          .flatMap(project -> getMergeRequests(project).stream()).toList());
      allNotes.addAll(allMrs.stream()
          .flatMap(mergeRequest -> getNotes(mergeRequest).stream()).toList());

      //clear old notifications
      notifications.clear();

      //add new notifications
      createNewNotifications(allMrs, allNotes);

      //clear local information about closed merge requests and their notes.
      mergeRequests.entrySet().removeIf(mr -> allMrs.stream()
          .noneMatch(actualMr -> mr.getKey().equals(actualMr.getId())));
      notes.entrySet().removeIf(note -> allNotes.stream()
          .noneMatch(actualNote -> note.getKey().equals(actualNote.getId())));

      return this.notifications;
    } catch (GitLabApiException e) {
      throw new RuntimeException(e);
    }
  }

  private void createNewNotifications(List<MergeRequest> allMrs, List<Note> allNotes) {
    allMrs.
        stream()
        .filter(mr -> !mergeRequests.containsKey(mr.getId()))
        .forEach(mr -> {
          mergeRequests.put(mr.getId(), mr);
          notifications.add(new MergeRequestNotification(mr));
        });
    allNotes.stream()
        .filter(note -> !notes.containsKey(note.getId()))
        .forEach(note -> {
          notes.put(note.getId(), note);
          notifications.add(new NoteNotification(note));
        });
  }

  private List<MergeRequest> getMergeRequests(Project project) {
    try {
      return gitLabApi.getMergeRequestApi()
          .getMergeRequests(new MergeRequestFilter().withProjectId(project.getId()).withState(
              MergeRequestState.OPENED));
    } catch (GitLabApiException e) {
      throw new RuntimeException(e);
    }
  }

  private List<Note> getNotes(MergeRequest mergeRequest) {
    try {
      return gitLabApi.getNotesApi().
          getMergeRequestNotes(mergeRequest.getProjectId(), mergeRequest.getIid());
    } catch (GitLabApiException e) {
      throw new RuntimeException(e);
    }
  }
}
