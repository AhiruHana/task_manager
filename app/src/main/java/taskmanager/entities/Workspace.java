package taskmanager.entities;

import javax.persistence.*;

@Entity
@Table(name = "workspaces")
public class Workspace {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }


  public User getUser() {
    return this.user;
  }

  public void setUser(User user) {
    this.user = user;
  }
  
}
