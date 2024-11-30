package taskmanager.entities;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import taskmanager.utils.HibernateUtil;

@Entity
@Table(name = "cols")
public class Col {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id", nullable = false)
  private Board board;

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

  public Board getBoard() {
    return board;
  }

  public void setBoard(Board board) {
    this.board = board;
  }

  public static Col findById(Long id) {
      return findByField("id", id);
  }

  private static <T> Col findByField(String field, T value) {
    try {
      SessionFactory sessionFactory = HibernateUtil.getFactory();
      Session session = sessionFactory.openSession();
      session.beginTransaction();

      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<Col> criteria = builder.createQuery(Col.class);
      Root<Col> root = criteria.from(Col.class);
      criteria.select(root).where(builder.equal(root.get(field), value));

      Col result = session.createQuery(criteria).uniqueResult();

      session.getTransaction().commit();
      session.close();

      return result;
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
