package taskmanager.entities;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;

import taskmanager.utils.HibernateUtil;

@Entity
@Table(name = "tasks")
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "description", nullable = true, length = 255)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "col_id", nullable = false)
  private Col col;

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }


  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description){
    this.description= description;
  }

  public Long getColId() {
    return col.getId();
  }

  public Col getCol() {
    return col;
  }

  public void setCol(Col col) {
    this.col = col;
  }

  public static Task findById(Long id) {
      return findByField("id", id);
  }

  private static <T> Task findByField(String field, T value) {
    try {
      SessionFactory sessionFactory = HibernateUtil.getFactory();
      Session session = sessionFactory.openSession();
      session.beginTransaction();

      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<Task> criteria = builder.createQuery(Task.class);
      Root<Task> root = criteria.from(Task.class);
      criteria.select(root).where(builder.equal(root.get(field), value));

      Task result = session.createQuery(criteria).uniqueResult();

      session.getTransaction().commit();
      session.close();

      return result;
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String save(Task task) {
    SessionFactory sessionFactory = HibernateUtil.getFactory();
    Session session = sessionFactory.openSession();
    String errorMessage = null;

    try {
        session.beginTransaction();
        session.save(task);
        session.getTransaction().commit();
    } catch (ConstraintViolationException e) {
        errorMessage = "Validation error: " + e.getConstraintName();
        if (session.getTransaction() != null) {
            session.getTransaction().rollback();
        }
    } catch (Exception e) {
        errorMessage = "An unexpected error occurred: " + e.getMessage();
        if (session.getTransaction() != null) {
            session.getTransaction().rollback();
        }
    } finally {
        session.close();
    }

    return errorMessage;
  }
}
