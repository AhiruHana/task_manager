package taskmanager.compositekey;

import java.io.Serializable;
import java.util.Objects;

public class TaskAssignmentCKey implements Serializable {
  private Long userId;
  private Long taskId;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;

    if (o == null || getClass() != o.getClass())
      return false;

    TaskAssignmentCKey that = (TaskAssignmentCKey) o;
    return Objects.equals(userId, that.userId) && Objects.equals(taskId, that.taskId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taskId, userId);
  }
}
