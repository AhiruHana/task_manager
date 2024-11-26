package taskmanager.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import taskmanager.entities.Workspace;

@Entity
@Table(name = "boards")
public class Board {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "color", nullable = false, length = 100)
  private String color;

  @Column(name = "last_opened", nullable = false, length = 100)
  private LocalDateTime lastOpened;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "workspace_id", nullable = false)
  private Workspace workspace;

  public int getId() {
    return id;
  }

  public String getColor() {
    return this.color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public LocalDateTime getLastOpened() {
    return this.lastOpened;
  }

  public void setLastOpened(LocalDateTime lastOpened) {
    this.lastOpened = lastOpened;
  }

  public String getName() {
    return name;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Workspace getWorkspace() {
    return workspace;
  }

  public void setWorkspace(Workspace workspace) {
    this.workspace = workspace;
  }
}
