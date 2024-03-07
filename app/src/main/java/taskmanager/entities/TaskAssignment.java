package taskmanager.entities;

import javax.persistence.*;
import taskmanager.compositekey.TaskAssignmentCKey;

@Entity
@Table(name = "task_assignments")
@IdClass(TaskAssignmentCKey.class)
public class TaskAssignment {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
