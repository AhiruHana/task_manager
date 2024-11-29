package taskmanager.entities;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;

import taskmanager.utils.HibernateUtil;
import taskmanager.utils.PasswordUtil;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Column(name = "username", nullable = false, length = 50, unique = true)
  private String username;

  @Column(name = "email", nullable = false, length = 100, unique = true)
  private String email;

  @Column(name = "password_digest", nullable = false, length = 100)
  private String passwordDigest;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getPasswordDigest() {
    return passwordDigest;
  }

  public void setPasswordDigest(String passwordDigest) {
    this.passwordDigest = passwordDigest;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public User() {}

  public User(String username, String firstName, String lastName, String Email, String password) {
    setUsername(username);
    setFirstName(firstName);
    setLastName(lastName);
    setEmail(Email);
    setPasswordDigest(PasswordUtil.hashPassword(password));
  }

  public static User findByEmail(String email) {
    return findByField("email", email);
}

  public static User findById(Long id) {
      return findByField("id", id);
  }

  private static <T> User findByField(String field, T value) {
    try {
      SessionFactory sessionFactory = HibernateUtil.getFactory();
      Session session = sessionFactory.openSession();
      session.beginTransaction();

      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<User> criteria = builder.createQuery(User.class);
      Root<User> root = criteria.from(User.class);
      criteria.select(root).where(builder.equal(root.get(field), value));

      User result = session.createQuery(criteria).uniqueResult();

      session.getTransaction().commit();
      session.close();

      return result;
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String saveUser(User user) {
    SessionFactory sessionFactory = HibernateUtil.getFactory();
    Session session = sessionFactory.openSession();
    String errorMessage = null;

    try {
        session.beginTransaction();
        session.save(user);
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
