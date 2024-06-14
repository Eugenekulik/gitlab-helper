package by.eugenekulik.domain;

import org.gitlab4j.api.models.Note;

public class NoteNotification implements Notification{

  private final Note note;

  public NoteNotification(Note note) {
    this.note = note;
  }

  @Override
  public String toString() {
    return """
        New event!
        noteable type: %s
        type: %s
        author: %s
        %s
        created at: %s
        """.formatted(
            note.getNoteableType(),
            note.getType(),
            note.getAuthor().getName(),
            note.getBody(),
            note.getCreatedAt()
    );
  }
}
