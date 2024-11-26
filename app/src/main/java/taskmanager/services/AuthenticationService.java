package taskmanager.services;

import taskmanager.entities.User;
import taskmanager.entities.JwtToken;
import taskmanager.utils.HibernateUtil;
import taskmanager.utils.JwtUtil;
import taskmanager.utils.PasswordUtil;
import taskmanager.exceptions.AuthenticationFailed;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class AuthenticationService {

    public User authenticate(String usernameOrEmail, String password) throws AuthenticationFailed {
        SessionFactory factory = HibernateUtil.getFactory();
        Session session = factory.openSession();

        try {
            // Query for user by username or email
            Query<User> query = session.createQuery(
                    "FROM User WHERE username = :usernameOrEmail OR email = :usernameOrEmail", User.class);
            query.setParameter("usernameOrEmail", usernameOrEmail);
            List<User> users = query.getResultList();

            if (users.isEmpty()) {
                throw new AuthenticationFailed(401, "Invalid username or password");
            }

            User user = users.get(0);

            // Check the password digest
            if (!PasswordUtil.checkPassword(password, user.getPasswordDigest())) {
                throw new AuthenticationFailed(401, "Invalid username or password");
            }

            // Generate the JWT token
            String jwtToken = JwtUtil.generateToken(user.getId());

            // Store the JWT token in the database
            JwtToken jwtTokenEntity = new JwtToken();
            jwtTokenEntity.setUserId(user.getId());
            jwtTokenEntity.setJwtToken(jwtToken);

            session.beginTransaction();
            session.save(jwtTokenEntity);
            session.getTransaction().commit();

            return user; // Return the authenticated user
        } finally {
            session.close();
        }
    }
}
