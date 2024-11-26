package taskmanager.services;

import taskmanager.entities.User;
import taskmanager.utils.HibernateUtil;
import taskmanager.utils.PasswordUtil;
import taskmanager.exceptions.AuthenticationFailed;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class AuthenticationService {

    public User authenticate(String username, String password) throws AuthenticationFailed {
        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();

        try {
            Query query = session.createQuery(
                    "FROM User WHERE (email = :username or username = :username)");
            query.setParameter("username", username);
            List<User> users = query.getResultList();

            if (users.isEmpty()) {
                throw new AuthenticationFailed(401, "Invalid username or password");
            }

            User user = users.get(0);

            if (!PasswordUtil.checkPassword(password, user.getPasswordDigest())) {
                throw new AuthenticationFailed(401, "Invalid username or password");
            }

            return user; // Return the authenticated user
        } finally {
            session.close();
        }
    }
}
